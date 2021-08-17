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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserOnlineSim {
  private static Map<String, Map<String, String>> countriesWithServicesMap;
  private static String countriesWithServicesJsonString;
  private static JsonObject countriesWithServicesJson;

  private final static Pattern countryNumberRegex = Pattern.compile("[0-9]{1,4}$");
  private final static Pattern priceRegex = Pattern.compile("^[0-9]*[,.]?[0-9]*");
  private final static Pattern validationRegex = Pattern.compile("[0-9]*[,.]?[0-9]*.");

  private static URL url;

  static {
    try {
      url = new URL("https://onlinesim.ru/price-list-data?type=receive");
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  @Nullable
  public static String getCountriesWithServicesJsonString(){
    return countriesWithServicesJsonString;
  }

  @Nullable
  public static  Map<String, Map<String, String>> getCountriesWithServicesMap(){
    if(countriesWithServicesMap == null){
      return null;
    }
    else {
      return countriesWithServicesMap;
    }
  }

  @Nullable
  public static  JsonObject getCountriesWithServicesJson(){
    if(countriesWithServicesJson == null){
      return null;
    }
    else {
      return countriesWithServicesJson;
    }
  }

  public static void parse(){
    countriesWithServicesJson = new JsonObject();

    JsonObject parsedJSON = getJsonDataFromSite(url);
    JsonObject countries = parsedJSON.get("text").getAsJsonObject();
    JsonObject servicesPrices = getServicePricesJson(parsedJSON.get("list").getAsJsonObject());

    for (String country : countries.keySet()) {
      Matcher matcher = countryNumberRegex.matcher(country);
      if (matcher.find()) {
        countriesWithServicesJson.add(countries.get(country).getAsString(), servicesPrices.get(country.substring(matcher.start(), matcher.end())));
      }
    }

    writeToFile();

    Gson gson = new Gson();
    countriesWithServicesMap = (Map<String, Map<String, String>>) gson.fromJson(countriesWithServicesJson, Map.class);
    countriesWithServicesJsonString = countriesWithServicesJson.toString();
  }

  @NotNull
  private static JsonObject getServicePricesJson(@NotNull JsonObject servicesJson){
    JsonObject servicePrice = new JsonObject();
    for(String countryNumber : servicesJson.keySet()){
      JsonObject tmp = new JsonObject();
      for (String service : servicesJson.get(countryNumber).getAsJsonObject().keySet()){

        JsonObject priceAndCurrency = new JsonObject();
        String servicePriceWithCurrency = servicesJson.get(countryNumber).getAsJsonObject().get(service).getAsString();
        Matcher matcher = validationRegex.matcher(servicePriceWithCurrency);

        if(matcher.find()){
          String validPrice = servicePriceWithCurrency.substring(matcher.start(), matcher.end());
          matcher = priceRegex.matcher(validPrice);
          if(matcher.find()){
            priceAndCurrency.add("price", new JsonPrimitive(new BigDecimal(validPrice.substring(matcher.start(), matcher.end()))));
            priceAndCurrency.add("currency", new JsonPrimitive(priceRegex.split(validPrice)[1]));
          }
        }
        tmp.add(service, priceAndCurrency);
      }
      servicePrice.add(countryNumber, tmp);
    }
    return servicePrice;
  }

  @NotNull
  private static JsonObject getJsonDataFromSite(@NotNull URL url) {
    JsonParser jsonParser = new JsonParser();
    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        stringBuilder.append(inputLine);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return jsonParser.parse(stringBuilder.toString()).getAsJsonObject();
  }

  private static void writeToFile(){
    StringBuilder stringBuilder = new StringBuilder();
    for (String country : countriesWithServicesJson.keySet()) {
      stringBuilder.append("\"");
      stringBuilder.append(country);
      stringBuilder.append("\" : {\n");
      if (!countriesWithServicesJson.get(country).isJsonNull()) {
        for (String serviceName : countriesWithServicesJson.get(country).getAsJsonObject().keySet()) {
          stringBuilder.append("\t\"");
          stringBuilder.append(serviceName);
          stringBuilder.append("\" : ");
          stringBuilder.append(countriesWithServicesJson.get(country).getAsJsonObject().get(serviceName).getAsJsonObject().get("price"));
          stringBuilder.append(countriesWithServicesJson.get(country).getAsJsonObject().get(serviceName).getAsJsonObject().get("currency").getAsString());
          stringBuilder.append("\n");
        }
      } else {
        stringBuilder.append("Нет опций\n");
      }
      stringBuilder.append("},\n");
    }

    try (FileWriter fileWriter = new FileWriter("Result.txt")) {
      fileWriter.write(stringBuilder.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
