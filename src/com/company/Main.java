package com.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Main extends Application {
  //private static Map<String, Map<String, ServicePrice>> parsedCountriesWithServicesMap;
  private static Map<String, List<Service>> parsedCountriesWithServicesMap;

  public static void main(String[] args) {
    Application.launch();
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      //parsedCountriesWithServicesMap = updateCountryWithServicesMap();
      parsedCountriesWithServicesMap = updateCountryWithServicesMapList();
      parsedCountriesWithServicesMap.get("Австрия").add(new Service("2Erety", new ServicePrice(new BigDecimal("2"), "$")));
      parsedCountriesWithServicesMap.get("Австрия").add(new Service("1Erety", new ServicePrice(new BigDecimal("1"), "$")));
      parsedCountriesWithServicesMap.get("Австрия").add(new Service("5Erety", new ServicePrice(new BigDecimal("5"), "$")));
      if (parsedCountriesWithServicesMap != null) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TablePage.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.show();
      }
    } catch (Exception e) {
      AlertShower.showErrorAlert("Что-то пошло не так", e.getMessage(), false);
      e.printStackTrace();
    }
  }

  /*public static Map<String, Map<String, ServicePrice>> getParsedCountriesWithServicesMap() {
    return parsedCountriesWithServicesMap;
  }
  public static Map<String, Map<String, ServicePrice>> updateCountryWithServicesMap() {
    try {
      parsedCountriesWithServicesMap = ParserOnlineSim.parse(false);
    } catch (Exception e) {
      ParserOnlineSim.getLogger().log(Level.WARNING, "Failed to parse OnlineSim");
      AlertShower.showErrorAlert("Не удалось спарсить OnlineSim", null, false);
    }
    return parsedCountriesWithServicesMap;
  }*/

  public static Map<String, List<Service>> getParsedCountriesWithServicesMap() {
    return parsedCountriesWithServicesMap;
  }

  public static Map<String, List<Service>> updateCountryWithServicesMapList() {
    try {
      parsedCountriesWithServicesMap = ParserOnlineSim.parseMapList(false);
    } catch (Exception e) {
      ParserOnlineSim.getLogger().log(Level.WARNING, "Failed to parse OnlineSim");
      AlertShower.showErrorAlert("Не удалось спарсить OnlineSim", null, false);
    }
    return parsedCountriesWithServicesMap;
  }
}
