package com.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Level;

public class Main extends Application {
  private static Map<String, Map<String, ServicePrice>> parsedCountriesWithServicesMap;

  public static void main(String[] args) {
    Application.launch();
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      parsedCountriesWithServicesMap = updateCountryWithServicesMap();
      parsedCountriesWithServicesMap.get("Австрия").put("2Erety", new ServicePrice(new BigDecimal("2"), "$"));
      parsedCountriesWithServicesMap.get("Австрия").put("1Erety", new ServicePrice(new BigDecimal("1"), "$"));
      parsedCountriesWithServicesMap.get("Австрия").put("5Erety", new ServicePrice(new BigDecimal("5"), "$"));
      if (parsedCountriesWithServicesMap == null) {
        ParserOnlineSim.getLogger().log(Level.WARNING, "Failed to parse OnlineSim");
        AlertShower.showErrorAlert("Не удалось спарсить OnlineSim", null, false);
      } else {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("TablePage.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.show();
      }
    } catch (Exception e) {
      AlertShower.showErrorAlert("Что-то пошло не так", e.getMessage(), false);
      e.printStackTrace();
    }
  }

  public static Map<String, Map<String, ServicePrice>> getParsedCountriesWithServicesMap() {
    return parsedCountriesWithServicesMap;
  }

  public static Map<String, Map<String, ServicePrice>> updateCountryWithServicesMap() throws Exception {
    parsedCountriesWithServicesMap = ParserOnlineSim.parse(false);
    return parsedCountriesWithServicesMap;
  }
}
