package com.company;

public class Main {

  public static void main(String[] args) {
    System.out.println(ParserOnlineSim.getCountriesWithServicesMap());
    System.out.println(ParserOnlineSim.getCountriesWithServicesJsonString());
    ParserOnlineSim.parse();
    System.out.println(ParserOnlineSim.getCountriesWithServicesJsonString());
  }
}
