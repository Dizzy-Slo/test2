package com.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
      parsedCountriesWithServicesMap = ParserOnlineSim.parse(false);
      if (parsedCountriesWithServicesMap == null) {
        ParserOnlineSim.getLogger().log(Level.WARNING, "Failed to parse OnlineSim");
        AlertShower.showErrorAlert("Не удалось спарсить OnlineSim", null, false);
      } else {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainPage.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        //primaryStage.setResizable(false);
        primaryStage.show();
      }
    } catch (Exception e) {
      AlertShower.showErrorAlert("Что-то пошло не так", e.getMessage(), false);
      e.printStackTrace();
    }
  }

  public static Map<String, Map<String, ServicePrice>> getParsedCountriesWithServicesMap(){
    return parsedCountriesWithServicesMap;
  }
}
