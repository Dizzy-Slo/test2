import com.sun.javafx.collections.ObservableListWrapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MttParserController {
  public ListView<LocalDate> dateListView;
  public TableColumn<Row, Integer> smsDatabaseColumn;
  public TableColumn<Row, Integer> smsMttColumn;
  public TableColumn<Row, Integer> secDatabaseColumn;
  public TableColumn<Row, Integer> secMttColumn;
  public TableColumn<Row, Long> phoneColumn;
  public TableColumn<Row, Long> phoneColumnSms;
  public TableView<Row> discrepancySecTableView;
  public TableView<Row> discrepancySmsTableView;

  public SplitPane discrepancySplitPane;
  public Button loadDetailMttFileButton;
  public ProgressIndicator detailLoadProgressIndicator;
  public Button loadTotalMttFileButton;
  public ProgressIndicator totalLoadProgressIndicator;
  public Button openTotalDiscrepancyByDaysButton;
  public Label totalSmsLabel;
  public Label totalSecLabel;
  public Button dumpButton;

  private Map<LocalDate, Map<Long, Quantity>> databaseQuantityMap;
  private Map<LocalDate, Map<Long, Quantity>> mttQuantityMap;
  private Map<LocalDate, Quantity> totalMttQuantityMap;

  private Map<LocalDate, Map<Long, QuantityDiscrepancy>> detailDiscrepancyMap;
  private Map<LocalDate, QuantityDiscrepancy> totalDiscrepancyMap;

  private LocalDate fromDate;
  private LocalDate toDate;

  private File detailFile;
  private File totalFile;

  private static final MTTParser mttParser = new MTTParser();

  @FXML
  private void initialize() {
    dateListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null || detailDiscrepancyMap == null) return;
      updateTableView(newValue, detailDiscrepancyMap);
    });

    openTotalDiscrepancyByDaysButton.setOnAction((event) -> {
      openTotalDiscrepancyByDaysButton.setDisable(true);
      try {
        showTotalDiscrepancyByDays();
      } catch (IOException e) {
        showErrorMessage("Ошибка", "Не удалось открыть талицу расхождений по дням");
      }
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

          detailDiscrepancyMap = QuantityComparator.getDiscrepancies(mttQuantityMap, databaseQuantityMap);
          Platform.runLater(() -> discrepancySplitPane.setDisable(false));

          onLoadEnd(detailLoadProgressIndicator, isLoadButtonsNotDisable, loadTotalMttFileButton, loadDetailMttFileButton, mttParser.getDatesList());
        } catch (MttException e) {
          showErrorMessage("Файл некорректен", "Проверьте строчку:\n" + e.getMessage());
          onLoadFailed(detailLoadProgressIndicator, isLoadButtonsNotDisable, loadTotalMttFileButton, loadDetailMttFileButton);
        } catch (Exception e) {
          showErrorMessage("Произошла ошибка", e.getMessage());
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

          totalDiscrepancyMap = QuantityComparator.getTotalDiscrepancies(totalMttQuantityMap, databaseQuantityMap);

          int totalSmsCount = 0;
          int totalSecCount = 0;
          for (LocalDate date : totalDiscrepancyMap.keySet()) {
            totalSmsCount += totalDiscrepancyMap.get(date).getDatabaseQuantity().getSmsCount() - totalDiscrepancyMap.get(date).getFileQuantity().getSmsCount();
            totalSecCount += totalDiscrepancyMap.get(date).getDatabaseQuantity().getSecCount() - totalDiscrepancyMap.get(date).getFileQuantity().getSecCount();
          }
          if (totalSecCount > 0) {
            secLabelText = "В базе на " + totalSecCount + " секунд больше";
          } else {
            secLabelText = "В файле на " + -totalSecCount + " секунд больше";
          }
          if (totalSmsCount > 0) {
            smsLabelText = "В базе на " + totalSmsCount + " смс больше";
          } else {
            smsLabelText = "В файле на " + -totalSmsCount + " смс больше";
          }

          onLoadEnd(totalLoadProgressIndicator, isLoadButtonsNotDisable, loadDetailMttFileButton, loadTotalMttFileButton, mttParser.getDatesList());
        } catch (Exception e) {
          showErrorMessage("Произошла ошибка", e.getMessage());
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

    setDiscrepancyTableViewValueFactory();
  }

  private boolean checkFileFormat(boolean isLoadButtonsNotDisable, boolean isEmptyMap,
                                  @NotNull ProgressIndicator totalLoadProgressIndicator, @NotNull Button otherButton,
                                  @NotNull Button currentButton) throws SQLException, IOException, ClassNotFoundException {

    if (isEmptyMap || mttParser.getFromDate() == null || mttParser.getToDate() == null) {
      showErrorMessage("Неправильный файл", "Файл не соответсвует формату");
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
                         @NotNull Button currentButton, @Nullable List<LocalDate> dates) {
    Platform.runLater(() -> {
      progressIndicator.setVisible(false);
      dumpButton.setDisable(false);
      if (isLoadButtonsNotDisabled)
        loadButton.setDisable(false);
      openTotalDiscrepancyByDaysButton.setDisable(false);
      currentButton.setDisable(true);

      if (dateListView.getItems() == null || dateListView.getItems().isEmpty() || dates != null) {
        dateListView.setItems(new ObservableListWrapper<>(dates));
        dateListView.getItems().sort(LocalDate::compareTo);
        dateListView.getSelectionModel().selectFirst();
      }
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

  private void updateTableView(@NotNull LocalDate date, @NotNull Map<LocalDate, Map<Long, QuantityDiscrepancy>> detailDiscrepancyMap) {
    List<Row> smsRows = new ArrayList<>();
    List<Row> secRows = new ArrayList<>();
    for (Long phone : detailDiscrepancyMap.get(date).keySet()) {
      Quantity databaseQuantity = detailDiscrepancyMap.get(date).get(phone).getDatabaseQuantity();
      Quantity mttQuantity = detailDiscrepancyMap.get(date).get(phone).getFileQuantity();

      if (databaseQuantity.getSmsCount() != mttQuantity.getSmsCount())
        smsRows.add(new Row(phone, mttQuantity.getSmsCount(), databaseQuantity.getSmsCount()));

      if (databaseQuantity.getSecCount() != mttQuantity.getSecCount())
        secRows.add(new Row(phone, mttQuantity.getSecCount(), databaseQuantity.getSecCount()));

    }
    discrepancySmsTableView.setItems(new ObservableListWrapper<>(smsRows));
    discrepancySecTableView.setItems(new ObservableListWrapper<>(secRows));
  }

  private void showTotalDiscrepancyByDays() throws IOException {
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

    openTotalDiscrepancyByDaysButton.setDisable(false);
  }

  @Nullable
  private File loadCsvFile(@NotNull Window window) {
    FileChooser fileChooser = new FileChooser();
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

  private void setDiscrepancyTableViewValueFactory() {
    phoneColumn.setCellValueFactory(phoneColumnValue ->
      new SimpleObjectProperty<>(phoneColumnValue
        .getValue()
        .getPhone()));

    phoneColumnSms.setCellValueFactory(phoneColumnValue ->
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

  private void showErrorMessage(@NotNull String title, @NotNull String message) {
    Platform.runLater(() -> {
      Alert fileErrorAlert = new Alert(Alert.AlertType.ERROR);
      fileErrorAlert.setTitle(title);
      fileErrorAlert.setHeaderText(message);

      fileErrorAlert.showAndWait();
    });
  }

  static class Row {
    long phone;
    int mttCount;
    int databaseCount;

    Row(long phone, int mttCount, int databaseCount) {
      this.phone = phone;
      this.mttCount = mttCount;
      this.databaseCount = databaseCount;
    }

    public long getPhone() {
      return phone;
    }

    public int getMttCount() {
      return mttCount;
    }

    public int getDatabaseCount() {
      return databaseCount;
    }
  }
}