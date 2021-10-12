package quantity;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.*;

public class QuantityMapAdapter {
  @NotNull
  public static Map<LocalDate, Map<Long, Quantity>> mergeSmsSecMaps(@NotNull Map<LocalDate, Map<Long, Integer>> smsMap,
                                                                    @NotNull Map<LocalDate, Map<Long, Integer>> secMap) {
    Map<LocalDate, Map<Long, Quantity>> databaseQuantityMap = new HashMap<>();

    for (LocalDate date : smsMap.keySet()) {
      Map<Long, Quantity> quantityMap = new HashMap<>();
      for (Long phone : smsMap.get(date).keySet()) {
        quantityMap.put(phone, new Quantity(smsMap.get(date).get(phone), 0));
      }
      databaseQuantityMap.put(date, quantityMap);
    }

    for (LocalDate date : secMap.keySet()) {
      if (databaseQuantityMap.get(date) == null) {
        Map<Long, Quantity> quantityMap = new HashMap<>();
        for (Long phone : secMap.get(date).keySet()) {
          quantityMap.put(phone, new Quantity(0, secMap.get(date).get(phone)));
        }
        databaseQuantityMap.put(date, quantityMap);
        continue;
      }

      for (Long phone : secMap.get(date).keySet()) {
        Quantity phoneQuantity = databaseQuantityMap.get(date).get(phone);
        if (phoneQuantity == null) {
          databaseQuantityMap.get(date).put(phone, new Quantity(0, secMap.get(date).get(phone)));
          continue;
        }
        databaseQuantityMap.get(date).get(phone).addSecCount(secMap.get(date).get(phone));
      }
    }
    return databaseQuantityMap;
  }

  @NotNull
  public static Map<LocalDate, Quantity> getTotalMttDiscrepancyMapFromDetailMap(@NotNull Map<LocalDate, Map<Long, Quantity>> detailMap) {
    Map<LocalDate, Quantity> totalMttDiscrepancyMap = new HashMap<>();

    for (LocalDate date : detailMap.keySet()) {
      int smsCount = 0;
      int secCount = 0;
      for (Long phone : detailMap.get(date).keySet()) {
        smsCount += detailMap.get(date).get(phone).getSmsCount();
        secCount += detailMap.get(date).get(phone).getSecCount();
      }
      totalMttDiscrepancyMap.put(date, new Quantity(smsCount, secCount));
    }

    return totalMttDiscrepancyMap;
  }

  @NotNull
  public static Map<Long, Map<LocalDate, QuantityDiscrepancy>> convertDetailDiscrepancyMap(@NotNull Map<LocalDate, Map<Long, QuantityDiscrepancy>> detailDiscrepancyMap) {
    Map<Long, Map<LocalDate, QuantityDiscrepancy>> convertedDetailDiscrepancyMap = new TreeMap<>();
    for (LocalDate date : detailDiscrepancyMap.keySet()) {
      for (Long phone : detailDiscrepancyMap.get(date).keySet()) {

        convertedDetailDiscrepancyMap.computeIfAbsent(phone, k -> new HashMap<>());
        convertedDetailDiscrepancyMap.get(phone).put(date, detailDiscrepancyMap.get(date).get(phone));
      }
    }
    return convertedDetailDiscrepancyMap;
  }
}
