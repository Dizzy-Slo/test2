package parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import quantity.Quantity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MTTParser {
  private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");
  private static final Pattern regexForTotalFile =
    Pattern.compile("/(?<sec>\\d+)\\ssec/(?<sms>\\d+)\\scount_sms;(?<date>\\d{1,2}\\.\\d{1,2}\\.\\d{4})");
  private static final Pattern regexForAccountFile =
    Pattern.compile("(?<phone>\\d+);\\d{1,2}(?<monthAndYear>\\.\\d{1,2}\\.\\d{4});(?<quantities>(\\d+\\ssms/\\d+\\ssec/\\d+\\scount_sms\\s?;)+)");
  private static final Pattern regexForQuantity = Pattern.compile("/(?<sec>\\d+)\\ssec/(?<sms>\\d+)\\scount_sms");
  private static final Pattern regexForDays = Pattern.compile(";_(?<day>\\d{1,2})_DAY");

  private LocalDate fromDate;
  private LocalDate toDate;

  @NotNull
  public Map<LocalDate, Quantity> parseTotalQuantity(@NotNull File totalFile) throws IOException {
    long startTime = System.currentTimeMillis();
    Map<LocalDate, Quantity> totalQuantityMap = new HashMap<>();

    try (FileReader fileReader = new FileReader(totalFile)) {
      BufferedReader bufferedReader = new BufferedReader(fileReader);

      Matcher matcher;
      String line;
      List<LocalDate> datesList = new ArrayList<>();
      while ((line = bufferedReader.readLine()) != null) {

        matcher = regexForTotalFile.matcher(line);
        if (!matcher.find()) continue;

        datesList.add(LocalDate.parse(matcher.group("date"), dateTimeFormatter));
        totalQuantityMap.put(LocalDate.parse(matcher.group("date"), dateTimeFormatter),
          new Quantity(matcher.group("sms"), matcher.group("sec")));
      }
      if (!datesList.isEmpty()) {
        fromDate = datesList.get(0);
        toDate = datesList.get(datesList.size() - 1);
      }
      System.out.println("parsing total file time = " + (System.currentTimeMillis() - startTime));
      return totalQuantityMap;
    }
  }

  @NotNull
  public Map<LocalDate, Map<Long, Quantity>> parseQuantityByPhones(@NotNull File detailFile) throws IOException, DetailMttException {
    long startTime = System.currentTimeMillis();

    List<DetailMttFileRowInfo> detailMttFileRowList = new ArrayList<>();
    List<LocalDate> datesList = new ArrayList<>();
    try (FileReader fileReader = new FileReader(detailFile)) {
      BufferedReader bufferedReader = new BufferedReader(fileReader);
      bufferedReader.readLine();

      String line = bufferedReader.readLine();
      Matcher dayMatcher = regexForDays.matcher(line);

      List<String> days = new ArrayList<>();
      while (dayMatcher.find()) {
        days.add(dayMatcher.group("day"));
      }

      String monthAndYear = null;
      Matcher globalMatcher;
      while ((line = bufferedReader.readLine()) != null) {

        globalMatcher = regexForAccountFile.matcher(line);
        if (!globalMatcher.find()) continue;

        long phone = Long.parseLong(globalMatcher.group("phone"));

        if (monthAndYear == null) {
          monthAndYear = globalMatcher.group("monthAndYear");

          for (String day : days) {
            try {
              LocalDate date = LocalDate.parse(day + monthAndYear, dateTimeFormatter.withResolverStyle(ResolverStyle.STRICT));
              datesList.add(date);
            } catch (Exception ignore) {
            }
          }

          fromDate = datesList.get(0);
          toDate = datesList.get(datesList.size() - 1);
        }
        Matcher quantitiesMatcher = regexForQuantity.matcher(globalMatcher.group("quantities"));
        List<Quantity> quantitiesList = new ArrayList<>();
        while (quantitiesMatcher.find()) {
          quantitiesList.add(new Quantity(quantitiesMatcher.group("sms"), quantitiesMatcher.group("sec")));
        }

        detailMttFileRowList.add(new DetailMttFileRowInfo(phone, monthAndYear, quantitiesList));
      }

      Map<LocalDate, Map<Long, Quantity>> accountInfoMap = new HashMap<>();
      int dayCount = 0;
      for (LocalDate date : datesList) {
        if (accountInfoMap.containsKey(date)) continue;

        Map<Long, Quantity> accountQuantity = new HashMap<>();
        for (DetailMttFileRowInfo row : detailMttFileRowList) {
          if (row.getQuantities().size() != datesList.size()) {
            throw new DetailMttException(date, row);
          }
          accountQuantity.put(row.getPhone(), row.getQuantities().get(dayCount));
        }

        accountInfoMap.put(date, accountQuantity);
        dayCount++;
      }
      System.out.println("parsing detail file time = " + (System.currentTimeMillis() - startTime));
      return accountInfoMap;
    }
  }

  @Nullable
  public LocalDate getFromDate() {
    return fromDate;
  }

  @Nullable
  public LocalDate getToDate() {
    return toDate;
  }
}