package extra;

import quantity.QuantityDiscrepancy;
import view.Writeable;

import java.time.LocalDate;
import java.util.Map;

public class TotalDiscrepancyExtra implements Writeable {
  Map<LocalDate, QuantityDiscrepancy> totalDiscrepancyMap;

  public TotalDiscrepancyExtra(Map<LocalDate, QuantityDiscrepancy> totalDiscrepancyMap) {
    this.totalDiscrepancyMap = totalDiscrepancyMap;
  }

  @Override
  public String getStringForMttReport() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("DATE;DISCREPANCY;\n");
    for (LocalDate date : totalDiscrepancyMap.keySet()) {
      stringBuilder.append(date).append(';')
        .append(totalDiscrepancyMap.get(date).subtractMttQuantityFromDatabaseQuantity().getStringForCsvFile()).append(";\n");
    }
    return stringBuilder.toString();
  }
}
