import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class QuantityComparator {

  @NotNull
  public static Map<LocalDate, Map<Long, QuantityDiscrepancy>> getDiscrepancies(@NotNull Map<LocalDate, Map<Long, Quantity>> mttQuantitiesMap,
                                                                                @NotNull Map<LocalDate, Map<Long, Quantity>> databaseQuantitiesMap) {

    long startSTime = System.currentTimeMillis();
    Map<LocalDate, Map<Long, QuantityDiscrepancy>> discrepanciesMap = new HashMap<>();
    for (LocalDate date : mttQuantitiesMap.keySet()) {
      if (databaseQuantitiesMap.get(date).isEmpty()) {
        if (!mttQuantitiesMap.get(date).isEmpty()) {

          Map<Long, QuantityDiscrepancy> phoneDiscrepanciesMap = new HashMap<>();
          for (Long phone : mttQuantitiesMap.get(date).keySet()) {
            Quantity mttQuantity = mttQuantitiesMap.get(date).get(phone);
            phoneDiscrepanciesMap.put(phone, new QuantityDiscrepancy(new Quantity(0, 0), mttQuantity));
          }
          discrepanciesMap.put(date, phoneDiscrepanciesMap);
        }
        continue;
      }

      Map<Long, QuantityDiscrepancy> phoneDiscrepanciesMap = new HashMap<>();
      for (Long phone : mttQuantitiesMap.get(date).keySet()) {
        Quantity databaseQuantity = databaseQuantitiesMap.get(date).get(phone);
        Quantity mttQuantity = mttQuantitiesMap.get(date).get(phone);

        if (databaseQuantity == null) {
          if (!mttQuantity.isEmpty()) {
            phoneDiscrepanciesMap.put(phone, new QuantityDiscrepancy(new Quantity(0, 0), mttQuantity));
          }
          continue;
        }
        if (!mttQuantity.isEquals(databaseQuantity)) {
          phoneDiscrepanciesMap.put(phone, new QuantityDiscrepancy(databaseQuantity, mttQuantity));
        }
      }
      if (phoneDiscrepanciesMap.isEmpty()) continue;
      discrepanciesMap.put(date, phoneDiscrepanciesMap);
    }
    System.out.println("get discrepancies time = " + (System.currentTimeMillis() - startSTime));
    return discrepanciesMap;
  }

  @NotNull
  public static Map<LocalDate, QuantityDiscrepancy> getTotalDiscrepancies(@NotNull Map<LocalDate, Quantity> totalMttQuantityMap,
                                                                          @NotNull Map<LocalDate, Map<Long, Quantity>> databaseQuantityMap) {

    Map<LocalDate, QuantityDiscrepancy> totalDiscrepancy = new HashMap<>();
    for (LocalDate date : databaseQuantityMap.keySet()) {
      int smsCount = 0;
      int secCount = 0;
      for (Long phone : databaseQuantityMap.get(date).keySet()) {
        secCount += databaseQuantityMap.get(date).get(phone).getSecCount();
        smsCount += databaseQuantityMap.get(date).get(phone).getSmsCount();
      }

      if (secCount == totalMttQuantityMap.get(date).getSecCount() && smsCount == totalMttQuantityMap.get(date).getSmsCount()) continue;

      totalDiscrepancy.put(date, new QuantityDiscrepancy(new Quantity(smsCount, secCount), totalMttQuantityMap.get(date)));
    }

    return totalDiscrepancy;
  }
}