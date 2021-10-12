package view;

import com.sun.javafx.collections.ObservableListWrapper;
import database.DatabaseConnector;
import database.DatabaseQuery;
import extra.DayDiscrepancyExtra;
import extra.DetailDiscrepancyExtra;
import extra.PhoneQuantityExtra;
import extra.TotalDiscrepancyExtra;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import parser.DetailMttException;
import parser.MTTParser;
import quantity.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class MttParserController {
  public ListView<LocalDate> dateListView;
  public TableColumn<PhoneQuantityInfo, Integer> smsDatabaseColumn;
  public TableColumn<PhoneQuantityInfo, Integer> smsMttColumn;
  public TableColumn<PhoneQuantityInfo, Integer> secDatabaseColumn;
  public TableColumn<PhoneQuantityInfo, Integer> secMttColumn;
  public TableColumn<PhoneQuantityInfo, Long> secPhoneColumn;
  public TableColumn<PhoneQuantityInfo, Long> smsPhoneColumn;
  public TableView<PhoneQuantityInfo> discrepancySecTableView;
  public TableView<PhoneQuantityInfo> discrepancySmsTableView;

  public SplitPane discrepancySplitPane;
  public Button loadDetailMttFileButton;
  public ProgressIndicator detailLoadProgressIndicator;
  public Button loadTotalMttFileButton;
  public ProgressIndicator totalLoadProgressIndicator;
  public Button openTotalDiscrepancyByDaysButton;
  public Label totalSmsLabel;
  public Label totalSecLabel;
  public Button dumpButton;
  public Button writePhoneSecQuantityButton;
  public Button writePhoneSmsQuantityButton;
  public Button writeAllDiscrepanciesButton;
  public Button writeTotalDiscrepancyButton;
  public Button writeSelectedDayButton;

  private Map<LocalDate, Map<Long, Quantity>> databaseQuantityMap;
  private Map<LocalDate, Map<Long, Quantity>> mttQuantityMap;
  private Map<LocalDate, Quantity> totalMttQuantityMap;

  private Map<LocalDate, Map<Long, QuantityDiscrepancy>> detailDiscrepancyMap;
  private Map<LocalDate, QuantityDiscrepancy> totalDiscrepancyMap;

  private LocalDate fromDate;
  private LocalDate toDate;
  private Set<LocalDate> dateSet;

  private File detailFile;
  private File totalFile;

  private TableColumn<PhoneQuantityInfo, ?> currentSortingOrderSms;
  private TableColumn<PhoneQuantityInfo, ?> currentSortingOrderSec;

  private static final MTTParser mttParser = new MTTParser();
  private static final FileChooser fileChooser = new FileChooser();

  private static final String RESULT_DISCREPANCY_STRING_FORMAT = "В %s на %d %s больше";
  private static final String ERROR_TITLE = "Произошла ошибка";

  @FXML
  private void initialize() {
    buttonsSetOnAction();

    setDiscrepancyTableViewValueFactory();

    dateListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null || detailDiscrepancyMap == null) return;
      updateTableViews(newValue, detailDiscrepancyMap);

      if (currentSortingOrderSms != null)
        discrepancySmsTableView.getSortOrder().add(currentSortingOrderSms);
      if (currentSortingOrderSec != null)
        discrepancySecTableView.getSortOrder().add(currentSortingOrderSec);
    });

    dateListView.setCellFactory((v) -> new ListCell<LocalDate>() {
      @Override
      protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setGraphic(null);
        } else {
          Label label = new Label(item.toString());
          if (dateSet != null && !dateSet.contains(item))
            label.setTextFill(Paint.valueOf("red"));
          HBox hBox = new HBox(label);

          setGraphic(hBox);
        }
      }
    });

    discrepancySmsTableView.getSortOrder().addListener((ListChangeListener<TableColumn<PhoneQuantityInfo, ?>>) c -> {
      if (c.next() && c.wasAdded()) {
        currentSortingOrderSms = discrepancySmsTableView.getSortOrder().get(discrepancySmsTableView.getSortOrder().size() - 1);
      }
    });

    discrepancySecTableView.getSortOrder().addListener((ListChangeListener<TableColumn<PhoneQuantityInfo, ?>>) c -> {
      if (c.next() && c.wasAdded()) {
        currentSortingOrderSec = discrepancySecTableView.getSortOrder().get(discrepancySecTableView.getSortOrder().size() - 1);
      }
    });

    configureTableView(discrepancySmsTableView);
    configureTableView(discrepancySecTableView);
  }

  private void buttonsSetOnAction() {
    writePhoneSecQuantityButton.setOnAction((event) ->
      writeDiscrepancyToFile(new PhoneQuantityExtra(discrepancySecTableView.getItems(), dateListView.getSelectionModel().getSelectedItem()),
        discrepancySecTableView.getScene().getWindow()));

    writePhoneSmsQuantityButton.setOnAction((event) ->
      writeDiscrepancyToFile(new PhoneQuantityExtra(discrepancySmsTableView.getItems(), dateListView.getSelectionModel().getSelectedItem()),
        discrepancySmsTableView.getScene().getWindow()));

    writeAllDiscrepanciesButton.setOnAction((event) ->
      writeDiscrepancyToFile(new DetailDiscrepancyExtra(detailDiscrepancyMap), discrepancySmsTableView.getScene().getWindow()));

    writeTotalDiscrepancyButton.setOnAction((event) -> {
      if (totalDiscrepancyMap == null) {
        totalMttQuantityMap = QuantityMapAdapter.getTotalMttDiscrepancyMapFromDetailMap(mttQuantityMap);
        totalDiscrepancyMap = QuantityComparator.getTotalDiscrepancies(totalMttQuantityMap, databaseQuantityMap);
      }
      writeDiscrepancyToFile(new TotalDiscrepancyExtra(totalDiscrepancyMap), discrepancySmsTableView.getScene().getWindow());
    });

    writeSelectedDayButton.setOnAction((event) -> {
      LocalDate selectedDay = dateListView.getSelectionModel().getSelectedItem();
      if (selectedDay == null) return;

      writeDiscrepancyToFile(new DayDiscrepancyExtra(detailDiscrepancyMap.get(selectedDay), selectedDay),
        discrepancySmsTableView.getScene().getWindow());
    });

    openTotalDiscrepancyByDaysButton.setOnAction((event) -> {
      openTotalDiscrepancyByDaysButton.setDisable(true);
      showTotalDiscrepancyByDays();
    });

    loadDetailMttFileButton.setOnAction((event) -> {
      detailFile = loadCsvFile(discrepancySecTableView.getScene().getWindow());
      if (detailFile == null) return;

      boolean isLoadButtonsNotDisable = onLoadStart(detailLoadProgressIndicator);

      new Thread(() -> {
        try {
          mttQuantityMap = mttParser.parseQuantityByPhones(detailFile);

          if (checkFileFormat(isLoadButtonsNotDisable, mttQuantityMap.isEmpty(), detailLoadProgressIndicator, loadTotalMttFileButton, loadDetailMttFileButton))
            return;

          checkDates(mttQuantityMap.keySet());

          detailDiscrepancyMap = QuantityComparator.getDiscrepancies(mttQuantityMap, databaseQuantityMap);
          Platform.runLater(() -> discrepancySplitPane.setDisable(false));

          onLoadEnd(detailLoadProgressIndicator, isLoadButtonsNotDisable, loadTotalMttFileButton, loadDetailMttFileButton,
            new ArrayList<>(databaseQuantityMap.keySet()), new TreeSet<>(mttQuantityMap.keySet()));

          Platform.runLater(() -> {
            writeAllDiscrepanciesButton.setDisable(false);
            writeSelectedDayButton.setDisable(false);
            writePhoneSmsQuantityButton.setDisable(false);
            writePhoneSecQuantityButton.setDisable(false);
          });
        } catch (DetailMttException e) {
          showAlert(Alert.AlertType.ERROR, "Файл некорректен", "Проверьте строчку:\n" + e.getMessage());
          onLoadFailed(detailLoadProgressIndicator, isLoadButtonsNotDisable, loadTotalMttFileButton, loadDetailMttFileButton);
        } catch (Exception e) {
          showAlert(Alert.AlertType.ERROR, ERROR_TITLE, e.getMessage() == null ? "null" : e.getMessage());
          onLoadFailed(detailLoadProgressIndicator, isLoadButtonsNotDisable, loadTotalMttFileButton, loadDetailMttFileButton);
        }
      }).start();
    });

    loadTotalMttFileButton.setOnAction((event) -> {
      totalFile = loadCsvFile(discrepancySecTableView.getScene().getWindow());
      if (totalFile == null) return;

      boolean isLoadButtonsNotDisable = onLoadStart(totalLoadProgressIndicator);

      new Thread(() -> {
        String smsLabelText = "";
        String secLabelText = "";
        try {
          totalMttQuantityMap = mttParser.parseTotalQuantity(totalFile);

          if (checkFileFormat(isLoadButtonsNotDisable, totalMttQuantityMap.isEmpty(), totalLoadProgressIndicator, loadDetailMttFileButton, loadTotalMttFileButton))
            return;

          checkDates(totalMttQuantityMap.keySet());

          totalDiscrepancyMap = QuantityComparator.getTotalDiscrepancies(totalMttQuantityMap, databaseQuantityMap);
          Quantity generalDiscrepancy = QuantityComparator.getGeneralDiscrepancies(totalDiscrepancyMap);

          if (generalDiscrepancy.getSecCount() > 0) {
            secLabelText = String.format(RESULT_DISCREPANCY_STRING_FORMAT, "базе", generalDiscrepancy.getSecCount(), "секунд");
          } else {
            secLabelText = String.format(RESULT_DISCREPANCY_STRING_FORMAT, "файле", -generalDiscrepancy.getSecCount(), "секунд");
          }
          if (generalDiscrepancy.getSmsCount() > 0) {
            smsLabelText = String.format(RESULT_DISCREPANCY_STRING_FORMAT, "базе", generalDiscrepancy.getSmsCount(), "смс");
          } else {
            smsLabelText = String.format(RESULT_DISCREPANCY_STRING_FORMAT, "файле", -generalDiscrepancy.getSmsCount(), "смс");
          }

          onLoadEnd(totalLoadProgressIndicator, isLoadButtonsNotDisable, loadDetailMttFileButton, loadTotalMttFileButton,
            new ArrayList<>(databaseQuantityMap.keySet()), new TreeSet<>(totalMttQuantityMap.keySet()));
        } catch (Exception e) {
          showAlert(Alert.AlertType.ERROR, ERROR_TITLE, e.getMessage());
          onLoadFailed(totalLoadProgressIndicator, isLoadButtonsNotDisable, loadDetailMttFileButton, loadTotalMttFileButton);
        }

        setLabelsText(smsLabelText, secLabelText);
      }).start();
    });

    dumpButton.setOnAction((event) -> {
      mttQuantityMap = null;
      databaseQuantityMap = null;
      totalDiscrepancyMap = null;
      detailDiscrepancyMap = null;
      totalMttQuantityMap = null;
      totalFile = null;
      detailFile = null;
      dateSet = null;
      writeTotalDiscrepancyButton.setDisable(true);
      writeAllDiscrepanciesButton.setDisable(true);
      writeSelectedDayButton.setDisable(true);
      writePhoneSmsQuantityButton.setDisable(true);
      writePhoneSecQuantityButton.setDisable(true);
      loadDetailMttFileButton.setDisable(false);
      loadTotalMttFileButton.setDisable(false);
      dumpButton.setDisable(true);
      openTotalDiscrepancyByDaysButton.setDisable(true);
      discrepancySplitPane.setDisable(true);
      dateListView.setItems(null);
      discrepancySecTableView.setItems(null);
      discrepancySmsTableView.setItems(null);
      setLabelsText("", "");
    });
  }

  private void setDiscrepancyTableViewValueFactory() {
    secPhoneColumn.setCellValueFactory(phoneColumnValue ->
      new SimpleObjectProperty<>(phoneColumnValue
        .getValue()
        .getPhone()));

    smsPhoneColumn.setCellValueFactory(phoneColumnValue ->
      new SimpleObjectProperty<>(phoneColumnValue
        .getValue()
        .getPhone()));

    smsDatabaseColumn.setCellValueFactory(smsDatabaseValue ->
      new SimpleObjectProperty<>(smsDatabaseValue
        .getValue()
        .getDatabaseCount()));

    secDatabaseColumn.setCellValueFactory(secDatabaseValue ->
      new SimpleObjectProperty<>(secDatabaseValue
        .getValue()
        .getDatabaseCount()));

    smsMttColumn.setCellValueFactory(smsMttValue ->
      new SimpleObjectProperty<>(smsMttValue
        .getValue()
        .getMttCount()));

    secMttColumn.setCellValueFactory(secMttValue ->
      new SimpleObjectProperty<>(secMttValue
        .getValue()
        .getMttCount()));
  }

  private void configureTableView(@NotNull TableView<PhoneQuantityInfo> tableView) {
    tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
  }

  private boolean checkFileFormat(boolean isLoadButtonsNotDisable, boolean isEmptyMap,
                                  @NotNull ProgressIndicator totalLoadProgressIndicator, @NotNull Button otherButton,
                                  @NotNull Button currentButton) throws SQLException, IOException, ClassNotFoundException {

    if (isEmptyMap || mttParser.getFromDate() == null || mttParser.getToDate() == null) {
      showAlert(Alert.AlertType.ERROR, "Неправильный файл", "Файл не соответсвует формату");
      onLoadFailed(totalLoadProgressIndicator, isLoadButtonsNotDisable, otherButton, currentButton);
      Platform.runLater(() -> currentButton.setDisable(false));
      return true;
    }

    if (databaseQuantityMap == null) {
      setFromToDate(mttParser.getFromDate(), mttParser.getToDate());
      updateDatabaseMap();
    }
    return false;
  }

  private boolean onLoadStart(@NotNull ProgressIndicator progressIndicator) {
    progressIndicator.setVisible(true);

    boolean isLoadButtonsNotDisable = !loadDetailMttFileButton.isDisable() && !loadTotalMttFileButton.isDisable();
    loadTotalMttFileButton.setDisable(true);
    loadDetailMttFileButton.setDisable(true);

    return isLoadButtonsNotDisable;
  }

  private void onLoadEnd(@NotNull ProgressIndicator progressIndicator, boolean isLoadButtonsNotDisabled, @NotNull Button loadButton,
                         @NotNull Button currentButton, @Nullable List<LocalDate> allDatesList, @NotNull Set<LocalDate> fileDatesSet) {
    Platform.runLater(() -> {
      progressIndicator.setVisible(false);
      dumpButton.setDisable(false);
      if (isLoadButtonsNotDisabled)
        loadButton.setDisable(false);
      openTotalDiscrepancyByDaysButton.setDisable(false);
      currentButton.setDisable(true);

      writeTotalDiscrepancyButton.setDisable(false);

      if (dateListView.getItems() == null || dateListView.getItems().isEmpty() || allDatesList != null) {
        dateListView.setItems(new ObservableListWrapper<>(allDatesList));
        dateListView.getItems().sort(LocalDate::compareTo);
        dateListView.getSelectionModel().selectFirst();
      }
      discrepancySmsTableView.getSortOrder().add(smsPhoneColumn);
      discrepancySecTableView.getSortOrder().add(secPhoneColumn);

      if (dateSet != null) return;
      dateSet = fileDatesSet;
    });
  }

  private void onLoadFailed(@NotNull ProgressIndicator progressIndicator, boolean isLoadButtonsNotDisabled,
                            @NotNull Button loadButton, @NotNull Button currentLoadButton) {
    Platform.runLater(() -> {
      if (isLoadButtonsNotDisabled)
        loadButton.setDisable(false);

      progressIndicator.setVisible(false);
      currentLoadButton.setDisable(false);
    });
  }

  private void updateTableViews(@NotNull LocalDate date, @NotNull Map<LocalDate, Map<Long, QuantityDiscrepancy>> detailDiscrepancyMap) {
    List<PhoneQuantityInfo> smsRows = new ArrayList<>();
    List<PhoneQuantityInfo> secRows = new ArrayList<>();

    for (Long phone : detailDiscrepancyMap.get(date).keySet()) {
      Quantity databaseQuantity = detailDiscrepancyMap.get(date).get(phone).getDatabaseQuantity();
      Quantity mttQuantity = detailDiscrepancyMap.get(date).get(phone).getMttQuantity();

      if (databaseQuantity.getSmsCount() != mttQuantity.getSmsCount())
        smsRows.add(new PhoneQuantityInfo(phone, mttQuantity.getSmsCount(), databaseQuantity.getSmsCount()));

      if (databaseQuantity.getSecCount() != mttQuantity.getSecCount())
        secRows.add(new PhoneQuantityInfo(phone, mttQuantity.getSecCount(), databaseQuantity.getSecCount()));
    }
    discrepancySmsTableView.setItems(new ObservableListWrapper<>(smsRows));
    discrepancySecTableView.setItems(new ObservableListWrapper<>(secRows));
  }

  private void showTotalDiscrepancyByDays() {
    try {
      if (totalFile == null && detailFile != null && !mttQuantityMap.isEmpty() && !databaseQuantityMap.isEmpty()) {
        totalMttQuantityMap = QuantityMapAdapter.getTotalMttDiscrepancyMapFromDetailMap(mttQuantityMap);
        totalDiscrepancyMap = QuantityComparator.getTotalDiscrepancies(totalMttQuantityMap, databaseQuantityMap);
      }

      TotalDiscrepancyByDaysController.setTotalDiscrepancyMap(totalDiscrepancyMap);

      Scene scene = new Scene(new FXMLLoader(ParserMainTest.class.getResource("TotalDiscrepancyByDays.fxml")).load());
      Stage stage = new Stage();

      stage.setScene(scene);
      stage.setTitle("Общие расхождения по датам");
      stage.initModality(Modality.WINDOW_MODAL);
      stage.initOwner(discrepancySplitPane.getScene().getWindow());
      stage.showAndWait();

    } catch (Exception e) {
      showAlert(Alert.AlertType.ERROR, ERROR_TITLE, e.getMessage());
    } finally {
      openTotalDiscrepancyByDaysButton.setDisable(false);
    }
  }

  @Nullable
  private File loadCsvFile(@NotNull Window window) {
    return fileChooser.showOpenDialog(window);
  }

  private void setFromToDate(@NotNull LocalDate fromDate, @NotNull LocalDate toDate) {
    this.fromDate = fromDate;
    this.toDate = toDate;
  }

  private void updateDatabaseMap() throws SQLException, IOException, ClassNotFoundException {
    if (fromDate == null || toDate == null) return;

    FileReader fileReader = new FileReader("jsonMttSms.txt");
    BufferedReader bufferedReader = new BufferedReader(fileReader);
    String line;
    StringBuilder stringBuilder = new StringBuilder();
    while ((line = bufferedReader.readLine()) != null) {
      stringBuilder.append(line);
    }

    Map<LocalDate, Map<Long, Integer>> databaseSmsCountMap = DatabaseQuery.parseDatabaseSmsInfoFromJson(stringBuilder.toString());
    Map<LocalDate, Map<Long, Integer>> databaseSecCountMap = DatabaseQuery.getSecInfoFromDatabase(DatabaseConnector.getConnection(), fromDate, toDate);

    databaseQuantityMap = QuantityMapAdapter.mergeSmsSecMaps(databaseSmsCountMap, databaseSecCountMap);
  }

  private void setLabelsText(@NotNull String smsLabelText, @NotNull String secLabelText) {
    Platform.runLater(() -> {
      totalSmsLabel.setText(smsLabelText);
      totalSecLabel.setText(secLabelText);
    });
  }

  private void showAlert(@NotNull Alert.AlertType alertType, @NotNull String title, @NotNull String message) {
    Platform.runLater(() -> {
      Alert alert = new Alert(alertType);
      alert.setTitle(title);
      alert.setHeaderText(message);

      alert.showAndWait();
    });
  }

  private void writeDiscrepancyToFile(@NotNull Writeable written, @NotNull Window window) {
    try {
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", ".csv"));
      File file = fileChooser.showSaveDialog(window);
      if (file == null) return;

      QuantityWriter.writeToFile(file, written);
      fileChooser.getExtensionFilters().clear();
    } catch (Exception e) {
      showAlert(Alert.AlertType.ERROR, ERROR_TITLE, e.getMessage());
    }
  }

  private void checkDates(Set<LocalDate> localDates) {
    Set<LocalDate> variousDates = getVariousDatesSet(databaseQuantityMap.keySet(), localDates);
    if (variousDates.isEmpty()) return;

    StringBuilder stringBuilder = new StringBuilder();
    int datesCount = 0;
    for (LocalDate date : variousDates) {
      stringBuilder.append(date);
      if (++datesCount % 3 != 0)
        stringBuilder.append("\t");
      else
        stringBuilder.append("\n");
    }
    showAlert(Alert.AlertType.INFORMATION, "Предупреждение",
      "В файле задан не целый промежуток времени. Пропущены даты:\n" + stringBuilder);
  }

  @NotNull
  private Set<LocalDate> getVariousDatesSet(@NotNull Set<LocalDate> databaseDatesSet, @NotNull Set<LocalDate> mttDatesSet) {
    Set<LocalDate> variousDates = new HashSet<>();
    for (LocalDate date : databaseDatesSet) {
      if (mttDatesSet.contains(date)) continue;

      variousDates.add(date);
    }
    return variousDates;
  }
}