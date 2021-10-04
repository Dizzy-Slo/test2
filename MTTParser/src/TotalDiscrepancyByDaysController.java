import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TotalDiscrepancyByDaysController {

  public TableColumn<Row, LocalDate> date;
  public TableColumn<Row, Integer> smsDatabase;
  public TableColumn<Row, Integer> smsMtt;
  public TableColumn<Row, Integer> secDatabase;
  public TableColumn<Row, Integer> secMtt;
  public TableColumn<Row, Integer> residualSms;
  public TableColumn<Row, Integer> residualSec;
  public TableView<Row> totalDiscrepancyByDaysTableView;

  private static Map<LocalDate, QuantityDiscrepancy> totalDiscrepancyMap;

  @FXML
  private void initialize() {
    List<Row> rows = new ArrayList<>();
    for (LocalDate date : totalDiscrepancyMap.keySet()) {
      rows.add(new Row(date, totalDiscrepancyMap.get(date).getDatabaseQuantity(), totalDiscrepancyMap.get(date).getFileQuantity()));
    }
    setTableViewValueFactory();
    totalDiscrepancyByDaysTableView.setItems(new ObservableListWrapper<>(rows));
    totalDiscrepancyByDaysTableView.getSortOrder().add(date);
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

  static class Row {
    LocalDate date;
    Quantity databaseQuantity;
    Quantity mttQuantity;
    int residualSms;
    int residualSec;

    public Row(@NotNull LocalDate date, @NotNull Quantity databaseQuantity, @NotNull Quantity mttQuantity) {
      this.date = date;
      this.databaseQuantity = databaseQuantity;
      this.mttQuantity = mttQuantity;
      residualSec = databaseQuantity.getSecCount() - mttQuantity.getSecCount();
      residualSms = databaseQuantity.getSmsCount() - mttQuantity.getSmsCount();
    }

    public int getResidualSec() {
      return residualSec;
    }

    public int getResidualSms() {
      return residualSms;
    }

    public LocalDate getDate() {
      return date;
    }

    public Quantity getDatabaseQuantity() {
      return databaseQuantity;
    }

    public Quantity getMttQuantity() {
      return mttQuantity;
    }
  }
}
