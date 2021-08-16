package com.company;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserOnlineSim {
  private static Map<String, Map<String, String>> result;
  private static String resultJsonString;
  private static JsonObject resultJson;

  public static void parse() throws MalformedURLException {
    resultJson = new JsonObject();
    URL url = new URL("https://onlinesim.ru/price-list-data?type=receive");
    StringBuilder stringBuilder = new StringBuilder();
    Pattern regexCountryNumber = Pattern.compile("[0-9]{1,4}$");
    JsonParser jsonParser = new JsonParser();

    JsonObject parsedJSON = jsonParser.parse(urlToString(url)).getAsJsonObject();
    JsonObject countries = parsedJSON.get("text").getAsJsonObject();
    JsonObject prices = parsedJSON.get("list").getAsJsonObject();

    for (String s : countries.keySet()) {
      Matcher matcher = regexCountryNumber.matcher(s);
      if (matcher.find()) {
        resultJson.add(countries.get(s).getAsString(), prices.get(s.substring(matcher.start(), matcher.end())));
      }
    }

    for (String s : resultJson.keySet()) {
      stringBuilder.append("\"" + s + "\" : {\n");
      if (!resultJson.get(s).isJsonNull()) {
        for (String ss : resultJson.get(s).getAsJsonObject().keySet()) {
          stringBuilder.append("\t\"" + ss + "\" : " + resultJson.get(s).getAsJsonObject().get(ss) + "\n");
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

    resultJsonString = resultJson.toString();
    System.out.println(resultJsonString);

    Gson gson = new Gson();
    result = (Map<String, Map<String, String>>) gson.fromJson(resultJson, Map.class);
  }

  private static String urlToString(URL url) {
    StringBuilder stringBuilder = new StringBuilder();
    try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        stringBuilder.append(inputLine);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return stringBuilder.toString();
  }
}
