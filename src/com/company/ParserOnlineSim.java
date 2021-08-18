package com.company;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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
  private static final Pattern COUNTRY_NUMBER_REGEX = Pattern.compile("[0-9]{1,4}$");
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
    JsonObject countries = parsedJSON.get("text").getAsJsonObject();
    JsonObject servicesPrices = getServicePricesJson(parsedJSON.get("list").getAsJsonObject());

    Gson gson = new Gson();
    countriesWithServicesMap = new HashMap<>();
    Type countriesWithServicesMapType = TypeToken.getParameterized(Map.class, String.class, ServicePrice.class).getType();
    for (String country : countries.keySet()) {
      Matcher matcher = COUNTRY_NUMBER_REGEX.matcher(country);
      if (matcher.find()) {
        countriesWithServicesMap.put(countries.get(country).getAsString(), gson.fromJson(servicesPrices.get(country.substring(matcher.start(), matcher.end())), countriesWithServicesMapType));
      }
    }

    writeToFile();
    countriesWithServicesJsonString = gson.toJson(countriesWithServicesMap);
  }

  @NotNull
  private static JsonObject getServicePricesJson(@NotNull JsonObject servicesJson) {
    JsonObject servicePrice = new JsonObject();
    for (String countryNumber : servicesJson.keySet()) {
      JsonObject tmp = new JsonObject();
      for (String service : servicesJson.get(countryNumber).getAsJsonObject().keySet()) {

        JsonObject priceAndCurrency = new JsonObject();
        String servicePriceWithCurrency = servicesJson.get(countryNumber).getAsJsonObject().get(service).getAsString();
        Matcher matcher = VALIDATION_REGEX.matcher(servicePriceWithCurrency);

        if (matcher.find()) {
          String validPrice = servicePriceWithCurrency.substring(matcher.start(), matcher.end());
          matcher = PRICE_REGEX.matcher(validPrice);
          if (matcher.find()) {
            priceAndCurrency.addProperty("price", new BigDecimal(validPrice.substring(matcher.start(), matcher.end())));
            priceAndCurrency.addProperty("currency", PRICE_REGEX.split(validPrice)[1]);
          }
        }
        tmp.add(service, priceAndCurrency);
      }
      servicePrice.add(countryNumber, tmp);
    }
    return servicePrice;
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
    try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(onlineSimURL, "No connection").openStream()))) {
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
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
