package com.company;

import javafx.util.Pair;

import java.math.BigDecimal;
import java.util.Map;

public class Main {

  public static void main(String[] args) {
    System.out.println(ParserOnlineSim.getCountriesWithServicesMap());
    System.out.println(ParserOnlineSim.getCountriesWithServicesJson());
    System.out.println(ParserOnlineSim.getCountriesWithServicesJsonString());
    ParserOnlineSim.parse();
    //System.out.println(ParserOnlineSim.getCountriesWithServicesJsonString());
  }
}
