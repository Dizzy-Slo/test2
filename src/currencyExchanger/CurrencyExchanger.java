package currencyExchanger;

import com.company.AlertShower;
import com.company.ServicePrice;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.*;

public class CurrencyExchanger {
  private static final String url = "http://api.currencylayer.com/live?access_key=c14ba2838bb866d511dd86dc6a8fa6ed&format=1";
  private static Map<String, BigDecimal> currencyExchangeRateMap = new TreeMap<>();

  static {
    try {
      currencyExchangeRateMap = getCurrencyExchangeRateMap();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int comparingCurrency(@NotNull ServicePrice service1, @NotNull ServicePrice service2) {
    if (service1.getCurrency1() != Currency.UNKNOWN && service2.getCurrency1() != Currency.UNKNOWN) {
      BigDecimal price1 = priceInUsd(service1);
      BigDecimal price2 = priceInUsd(service2);

      return price1.compareTo(price2);
    }
    else {
      AlertShower.showErrorAlert("Неизвестные валюты",
        "Исключение вызвано сравнением: " + service1.getCurrency() + " и " + service2.getCurrency(),
        true);
      return 0;
    }
  }

  @NotNull
  public static BigDecimal priceInUsd(@NotNull ServicePrice servicePrice) {
    BigDecimal d = currencyExchangeRateMap.get("USD" + Objects.requireNonNull(servicePrice.getCurrency1()).getName());
    return servicePrice.getPrice().divide(d, RoundingMode.CEILING);
  }

  @NotNull
  private static Map<String, BigDecimal> getCurrencyExchangeRateMap() throws IOException {
    Map<String, BigDecimal> currencyExchangeMap = new HashMap<>();
    JsonObject rate = stringRate();

    for (String pare : rate.keySet()) {
      currencyExchangeMap.put(pare, new BigDecimal(rate.get(pare).getAsString()));
    }

    return currencyExchangeMap;
  }

  private static JsonObject stringRate() throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    FileReader fileReader = new FileReader("CurrencyExchangeRate.txt");
    Scanner fileScanner = new Scanner(fileReader);
    while (fileScanner.hasNext()) {
      stringBuilder.append(fileScanner.next());
    }

    fileReader.close();
    return new JsonParser().parse(stringBuilder.toString()).getAsJsonObject().get("quotes").getAsJsonObject();
  }

  private static JsonObject getJsonCurrencyExchangeRate() {
    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader input = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
      String inputLine;
      while ((inputLine = input.readLine()) != null) {
        stringBuilder.append(inputLine);
      }
    } catch (Exception e) {
      AlertShower.showErrorAlert("Ops", Arrays.toString(e.getStackTrace()), false);
    }
    return new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();
  }
}
