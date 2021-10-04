import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DatabaseQuery {
  @NotNull
  public static Map<LocalDate, Map<Long, Integer>> getSecInfoFromDatabase(@NotNull Connection connection, @NotNull LocalDate fromDate,
                                                                          @NotNull LocalDate toDate) throws SQLException {
    long startTime = System.currentTimeMillis();
    PreparedStatement ps = connection.prepareStatement(
      "SELECT DATE(callDate) AS day, mttAccount as phone, SUM(second) AS totalSec " +
        "FROM mttCallHistory " +
        "WHERE callDate >= ? AND callDate < ? AND second > 0 " +
        "GROUP BY day, phone");

    ps.setDate(1, Date.valueOf(fromDate));
    ps.setDate(2, Date.valueOf(toDate.plusDays(1)));

    ResultSet rs = ps.executeQuery();
    Map<LocalDate, Map<Long, Integer>> secQuantityMap = new HashMap<>();

    while (rs.next()) {
      LocalDate date = LocalDate.parse(rs.getString("day"));
      Map<Long, Integer> phoneSecMap = secQuantityMap.get(date);

      if (phoneSecMap == null) {
        phoneSecMap = new HashMap<>();
        phoneSecMap.put(rs.getLong("phone"), rs.getInt("totalSec"));
        secQuantityMap.put(date, phoneSecMap);

      } else {
        phoneSecMap.put(rs.getLong("phone"), rs.getInt("totalSec"));
      }
    }

    System.out.println("get sec from database time = " + (System.currentTimeMillis() - startTime));
    return secQuantityMap;
  }

  @NotNull
  public static Map<LocalDate, Map<Long, Integer>> parseDatabaseSmsInfoFromJson(@NotNull String json) {
    Type type = new TypeToken<Map<String, Map<Long, Integer>>>() {}.getType();
    Map<String, Map<Long, Integer>> smsQuantityMap = new Gson().fromJson(json, type);

    Map<LocalDate, Map<Long, Integer>> smsQuantityMapWithLocalDateMap = new HashMap<>();
    for (String date : smsQuantityMap.keySet()) {
      smsQuantityMapWithLocalDateMap.put(LocalDate.parse(date), smsQuantityMap.get(date));
    }

    return smsQuantityMapWithLocalDateMap;
  }
}
