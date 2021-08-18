package com.company;

import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserOnlineSim {
  private static Map<String, Map<String, ServicePrice>> countriesWithServicesMap;
  private static String countriesWithServicesJsonString;

  private static final String url = "https://onlinesim.ru/price-list-data?type=receive";
  private static final Pattern PRICE_REGEX = Pattern.compile("^[0-9]*[,.]?[0-9]*");
  private static final Pattern VALIDATION_REGEX = Pattern.compile("[0-9]*[,.]?[0-9]*.");

  @Nullable
  public static String getCountriesWithServicesJsonString() {
    return countriesWithServicesJsonString;
  }

  @Nullable
  public static Map<String, Map<String, ServicePrice>> getCountriesWithServicesMap() {
    return countriesWithServicesMap;
  }

  public static void parse() {
    JsonObject parsedJSON = getJsonDataFromSite();

    countriesWithServicesMap = getCountriesWithServicesMapFromJson(parsedJSON.get("list").getAsJsonObject(),
      parsedJSON.get("text").getAsJsonObject());

    Gson gson = new Gson();
    writeToFile();
    countriesWithServicesJsonString = gson.toJson(countriesWithServicesMap);
  }

  @NotNull
  private static Map<String, Map<String, ServicePrice>> getCountriesWithServicesMapFromJson(@NotNull JsonObject servicesJson,
                                                                                            @NotNull JsonObject countriesJson) {
    Map<String, Map<String, ServicePrice>> countriesWithServicesMap = new HashMap<>();
    for (String countryNumber : countriesJson.keySet()) {
      Map<String, ServicePrice> tmp = new HashMap<>();
      if (servicesJson.get(countryNumber.replaceAll("country_", "")) != null) {

        for (String service : servicesJson.get(countryNumber.replaceAll("country_", "")).getAsJsonObject().keySet()) {
          String servicePriceWithCurrency = servicesJson.get(countryNumber.replaceAll("country_", ""))
            .getAsJsonObject().get(service).getAsString();

          Matcher matcher = VALIDATION_REGEX.matcher(servicePriceWithCurrency);

          if (matcher.find()) {
            String validPrice = servicePriceWithCurrency.substring(matcher.start(), matcher.end());
            matcher = PRICE_REGEX.matcher(validPrice);

            if (matcher.find()) {
              ServicePrice priceAndCurrency = new ServicePrice(new BigDecimal(validPrice.substring(matcher.start(), matcher.end())),
                PRICE_REGEX.split(validPrice)[1]);

              tmp.put(service, priceAndCurrency);
            }
          }
        }
      }
      countriesWithServicesMap.put(countriesJson.get(countryNumber).getAsString(), tmp);
    }
    return countriesWithServicesMap;
  }

  @NotNull
  private static JsonObject getJsonDataFromSite() {
    URL onlineSimURL = null;
    try {
      onlineSimURL = new URL(url);
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    JsonParser jsonParser = new JsonParser();
    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(onlineSimURL,
      "No connection").openStream()))) {
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        stringBuilder.append(inputLine);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return jsonParser.parse(stringBuilder.toString()).getAsJsonObject();
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
      e.printStackTrace();
    }
  }
}
