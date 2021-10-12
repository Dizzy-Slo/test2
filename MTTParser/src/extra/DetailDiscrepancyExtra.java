package extra;

import quantity.Quantity;
import quantity.QuantityDiscrepancy;
import quantity.QuantityMapAdapter;
import view.Writeable;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DetailDiscrepancyExtra implements Writeable {
  Map<Long, Map<LocalDate, QuantityDiscrepancy>> detailDiscrepancyMap;
  Set<LocalDate> dateSet;

  public DetailDiscrepancyExtra(Map<LocalDate, Map<Long, QuantityDiscrepancy>> detailDiscrepancyMap) {
    this.detailDiscrepancyMap = QuantityMapAdapter.convertDetailDiscrepancyMap(detailDiscrepancyMap);
    this.dateSet = new TreeSet<>(detailDiscrepancyMap.keySet());
  }

  @Override
  public String getStringForMttReport() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("PHONES;RESULT;");
    for (LocalDate date : dateSet) {
      stringBuilder.append(date).append(';');
    }
    stringBuilder.append('\n');
    for (Long phone : detailDiscrepancyMap.keySet()) {
      stringBuilder.append(phone).append(';');

      StringBuilder phoneStringBuilder = new StringBuilder();
      Quantity resultQuantity = new Quantity(0, 0);
      for (LocalDate date : dateSet) {
        QuantityDiscrepancy quantityDiscrepancy = detailDiscrepancyMap.get(phone).get(date);
        if (quantityDiscrepancy == null) {
          phoneStringBuilder.append(" ;");
        } else {
          resultQuantity.add(quantityDiscrepancy.subtractMttQuantityFromDatabaseQuantity());
          phoneStringBuilder.append(quantityDiscrepancy.subtractMttQuantityFromDatabaseQuantity().getStringForCsvFile()).append(';');
        }
      }
      stringBuilder.append(resultQuantity.getStringForCsvFile()).append(';')
        .append(phoneStringBuilder).append('\n');
    }
    return stringBuilder.toString();
  }
}
