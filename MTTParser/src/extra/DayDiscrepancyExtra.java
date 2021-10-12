package extra;

import org.jetbrains.annotations.NotNull;
import quantity.QuantityDiscrepancy;
import view.Writeable;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeSet;

public class DayDiscrepancyExtra implements Writeable {
  Map<Long, QuantityDiscrepancy> dayDiscrepancyMap;
  LocalDate date;

  public DayDiscrepancyExtra(@NotNull Map<Long, QuantityDiscrepancy> dayDiscrepancyMap, @NotNull LocalDate date){
    this.dayDiscrepancyMap = dayDiscrepancyMap;
    this.date = date;
  }

  @Override
  public String getStringForMttReport() {
    long time = System.nanoTime();
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("PHONES;").append(date).append(";\n");
    for (Long phone : new TreeSet<>(dayDiscrepancyMap.keySet())) {
      stringBuilder.append(phone).append(';')
        .append(dayDiscrepancyMap.get(phone).subtractMttQuantityFromDatabaseQuantity().getStringForCsvFile()).append(";\n");
    }
    System.out.println("time = " + (System.nanoTime() - time));
    return stringBuilder.toString();
  }
}
