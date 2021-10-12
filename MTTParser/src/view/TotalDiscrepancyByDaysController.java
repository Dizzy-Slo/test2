package view;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.jetbrains.annotations.NotNull;
import quantity.QuantityDiscrepancy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TotalDiscrepancyByDaysController {

  public TableColumn<TotalDiscrepancyInfo, LocalDate> date;
  public TableColumn<TotalDiscrepancyInfo, Integer> smsDatabase;
  public TableColumn<TotalDiscrepancyInfo, Integer> smsMtt;
  public TableColumn<TotalDiscrepancyInfo, Integer> secDatabase;
  public TableColumn<TotalDiscrepancyInfo, Integer> secMtt;
  public TableColumn<TotalDiscrepancyInfo, Integer> residualSms;
  public TableColumn<TotalDiscrepancyInfo, Integer> residualSec;
  public TableView<TotalDiscrepancyInfo> totalDiscrepancyByDaysTableView;

  private static Map<LocalDate, QuantityDiscrepancy> totalDiscrepancyMap;

  @FXML
  private void initialize() {
    List<TotalDiscrepancyInfo> totalDiscrepancyRows = new ArrayList<>();
    for (LocalDate date : totalDiscrepancyMap.keySet()) {
      totalDiscrepancyRows.add(new TotalDiscrepancyInfo(date, totalDiscrepancyMap.get(date).getDatabaseQuantity(), totalDiscrepancyMap.get(date).getMttQuantity()));
    }
    setTableViewValueFactory();
    totalDiscrepancyByDaysTableView.setItems(new ObservableListWrapper<>(totalDiscrepancyRows));
    totalDiscrepancyByDaysTableView.getSortOrder().add(date);
    totalDiscrepancyByDaysTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
  }

  private void setTableViewValueFactory() {
    date.setCellValueFactory(dateColumnValue ->
      new SimpleObjectProperty<>(dateColumnValue
        .getValue()
        .getDate()));

    smsDatabase.setCellValueFactory(smsDatabaseValue ->
      new SimpleObjectProperty<>(smsDatabaseValue
        .getValue()
        .getDatabaseQuantity()
        .getSmsCount()));

    secDatabase.setCellValueFactory(secDatabaseValue ->
      new SimpleObjectProperty<>(secDatabaseValue
        .getValue()
        .getDatabaseQuantity()
        .getSecCount()));

    smsMtt.setCellValueFactory(smsMttValue ->
      new SimpleObjectProperty<>(smsMttValue
        .getValue()
        .getMttQuantity()
        .getSmsCount()));

    secMtt.setCellValueFactory(secMttValue ->
      new SimpleObjectProperty<>(secMttValue
        .getValue()
        .getMttQuantity()
        .getSecCount()));

    residualSms.setCellValueFactory(residualSmsValue ->
      new SimpleObjectProperty<>(residualSmsValue
        .getValue()
        .getResidualSms())
    );

    residualSec.setCellValueFactory(residualSecValue ->
      new SimpleObjectProperty<>(residualSecValue
        .getValue()
        .getResidualSec())
    );
  }

  public static void setTotalDiscrepancyMap(@NotNull Map<LocalDate, QuantityDiscrepancy> totalDiscrepancyMap) {
    TotalDiscrepancyByDaysController.totalDiscrepancyMap = totalDiscrepancyMap;
  }
}
