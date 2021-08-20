package com.company;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserOnlineSim {
  private static Map<String, Map<String, ServicePrice>> countriesWithServicesMap;
  private static String countriesWithServicesJsonString;
  private static Logger logger;

  private static final String url = "https://onlinesim.ru/price-list-data?type=receive";
  private static final Pattern VALIDATION_REGEX = Pattern.compile("(?<price>\\d+)(?<currency>\\D)");

  static {
    try (FileInputStream inputStream = new FileInputStream("src/com/company/LogConfig.config")) {
      LogManager.getLogManager().readConfiguration(inputStream);
      logger = Logger.getLogger(ParserOnlineSim.class.getName());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Logger getLogger() {
    return logger;
  }

  @Nullable
  public static String getCountriesWithServicesJsonString() {
    return countriesWithServicesJsonString;
  }

  @Nullable
  public static Map<String, Map<String, ServicePrice>> getCountriesWithServicesMap() {
    return countriesWithServicesMap;
  }

  @Nullable
  public static Map<String, Map<String, ServicePrice>> parse(boolean saveToFile) throws Exception {
    JsonObject parsedJSON = getJsonDataFromSite();

    countriesWithServicesMap = getCountriesWithServicesMapFromJson(parsedJSON.get("list").getAsJsonObject(),
      parsedJSON.get("text").getAsJsonObject());

    if (saveToFile) writeToFile();

    countriesWithServicesJsonString = new Gson().toJson(countriesWithServicesMap);
    return countriesWithServicesMap;
  }

  @NotNull
  private static Map<String, Map<String, ServicePrice>> getCountriesWithServicesMapFromJson(@NotNull JsonObject servicesJson,
                                                                                            @NotNull JsonObject countriesJson) {
    Map<String, Map<String, ServicePrice>> countriesWithServicesMap = new HashMap<>();
    for (String countryNumber : countriesJson.keySet()) {
      Map<String, ServicePrice> servicePriceMap = new HashMap<>();
      JsonElement country = servicesJson.get(countryNumber.replaceAll("country_", ""));

      if (country != null) {
        JsonObject servicePrice = country.getAsJsonObject();

        for (String service : servicePrice.keySet()) {
          String servicePriceWithCurrency = servicePrice.get(service).getAsString();

          Matcher matcher = VALIDATION_REGEX.matcher(servicePriceWithCurrency);
          if (matcher.find()) {
            ServicePrice priceAndCurrency = new ServicePrice(new BigDecimal(matcher.group("price")), matcher.group("currency"));
            servicePriceMap.put(service, priceAndCurrency);
          }
        }
      }
      countriesWithServicesMap.put(countriesJson.get(countryNumber).getAsString(), servicePriceMap);
    }
    return countriesWithServicesMap;
  }

  @NotNull
  private static JsonObject getJsonDataFromSite() throws Exception {
    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        stringBuilder.append(inputLine);
      }
    }
    return new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();
  }

  private static void writeToFile() {
    StringBuilder stringBuilder = new StringBuilder();

    countriesWithServicesMap.forEach((country, servicesWithPricesMap) -> {
      stringBuilder.append("\"").append(country).append("\" : {\n");
      if (servicesWithPricesMap != null) {
        servicesWithPricesMap.forEach((service, priceWithCurrency) -> stringBuilder.append("\t\"").append(service).append("\" : ")
          .append(priceWithCurrency.getPrice()).append(priceWithCurrency.getCurrency()).append("\n"));
      }
    });

    try (FileWriter fileWriter = new FileWriter("Result.txt")) {
      fileWriter.write(stringBuilder.toString());
      fileWriter.flush();
    } catch (IOException e) {
      logger.log(Level.WARNING, "Write to file failed", e);
    }
  }
}
