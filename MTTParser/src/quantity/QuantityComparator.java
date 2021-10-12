package quantity;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class QuantityComparator {

  @NotNull
  public static Map<LocalDate, Map<Long, QuantityDiscrepancy>> getDiscrepancies(@NotNull Map<LocalDate, Map<Long, Quantity>> mttQuantitiesMap,
                                                                                @NotNull Map<LocalDate, Map<Long, Quantity>> databaseQuantitiesMap) {

    long startSTime = System.currentTimeMillis();

    Map<LocalDate, Map<Long, QuantityDiscrepancy>> discrepanciesMap = new HashMap<>();
    for (LocalDate date : databaseQuantitiesMap.keySet()) {

      Map<Long, QuantityDiscrepancy> phoneDiscrepancyMap = new HashMap<>();
      Set<Long> phoneSet;
      boolean isMttContainsCurrentDate = mttQuantitiesMap.containsKey(date);
      if (!isMttContainsCurrentDate) {
        phoneSet = databaseQuantitiesMap.get(date).keySet();
      } else {
        phoneSet = new TreeSet<>(mttQuantitiesMap.get(date).keySet());
        /*int k = 0;
        for (Long phone : databaseQuantitiesMap.get(date).keySet()) {

          if(!phoneSet.contains(phone)) {
            System.out.println(date + " номер: " + phone + "\n всего: " + ++k);
          }
        }*/
        phoneSet.addAll(databaseQuantitiesMap.get(date).keySet());
      }

      for (Long phone : phoneSet) {
        Quantity databaseQuantity = databaseQuantitiesMap.get(date).get(phone);
        Quantity mttQuantity;
        if (isMttContainsCurrentDate) {
          mttQuantity = mttQuantitiesMap.get(date).get(phone);
        } else {
          if (databaseQuantity == null) continue;

          phoneDiscrepancyMap.put(phone, new QuantityDiscrepancy(databaseQuantity, new Quantity(0, 0)));
          continue;
        }
        if (databaseQuantity == null) {
          if (mttQuantity.isEmpty()) continue;

          phoneDiscrepancyMap.put(phone, new QuantityDiscrepancy(new Quantity(0, 0), mttQuantity));
          continue;
        }
        if (mttQuantity == null) {
          if (databaseQuantity.isEmpty()) continue;

          phoneDiscrepancyMap.put(phone, new QuantityDiscrepancy(databaseQuantity, new Quantity(0, 0)));
          continue;
        }
        if (!databaseQuantity.isEquals(mttQuantity)) {
          phoneDiscrepancyMap.put(phone, new QuantityDiscrepancy(databaseQuantity, mttQuantity));
        }
      }
      discrepanciesMap.put(date, phoneDiscrepancyMap);
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

      if (totalMttQuantityMap.get(date) == null) {
        totalDiscrepancy.put(date, new QuantityDiscrepancy(new Quantity(smsCount, secCount), new Quantity(0, 0)));
        continue;
      }
      if (secCount == totalMttQuantityMap.get(date).getSecCount() && smsCount == totalMttQuantityMap.get(date).getSmsCount()) continue;

      totalDiscrepancy.put(date, new QuantityDiscrepancy(new Quantity(smsCount, secCount), totalMttQuantityMap.get(date)));
    }

    return totalDiscrepancy;
  }

  @NotNull
  public static Quantity getGeneralDiscrepancies(@NotNull Map<LocalDate, QuantityDiscrepancy> totalDiscrepanciesMap) {
    int smsCountMtt = 0;
    int secCountMtt = 0;
    int smsCountDatabase = 0;
    int secCountDatabase = 0;
    for (LocalDate date : totalDiscrepanciesMap.keySet()) {
      Quantity mttQuantity = totalDiscrepanciesMap.get(date).getMttQuantity();
      Quantity databaseQuantity = totalDiscrepanciesMap.get(date).getDatabaseQuantity();

      smsCountDatabase += databaseQuantity.getSmsCount();
      secCountDatabase += databaseQuantity.getSecCount();
      smsCountMtt += mttQuantity.getSmsCount();
      secCountMtt += mttQuantity.getSecCount();
    }

    return new Quantity(smsCountDatabase - smsCountMtt, secCountDatabase - secCountMtt);
  }
}