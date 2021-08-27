package com.company;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

public class CurrencyExchanger {
  public static int comparingCurrency(BigDecimal service1, BigDecimal service2) {
    BigDecimal price1 = null;
    BigDecimal price2 = null;
    try {
      price1 = priceInRub(service1);
      price2 = priceInRub(service2);
    } catch (IOException e) {
      e.printStackTrace();
    }
    assert price1 != null;
    return price1.compareTo(price2);
  }

  public static BigDecimal priceInRub(BigDecimal price) throws IOException {
    JsonObject rate = stringRate();
    BigDecimal d = new BigDecimal(rate.get("quotes").getAsJsonObject().get("RUBUSD").getAsString());
    return d.multiply(price);
  }

  private static JsonObject stringRate() throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
    FileReader fileReader = new FileReader("CurrencyExchangeRate.txt");
    Scanner fileScanner = new Scanner(fileReader);
    while (fileScanner.hasNext()) {
      stringBuilder.append(fileScanner.next());
    }

    fileReader.close();
    return new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();
  }
}
