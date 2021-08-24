package com.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;

public class Main extends Application {

  public static void main(String[] args) {
    Application.launch();
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      if (ParserOnlineSim.parse(false) == null) {
        ParserOnlineSim.getLogger().log(Level.WARNING, "Failed to parse OnlineSim");
        AlertShower.showErrorAlert("Не удалось спарсить OnlineSim", null);
      } else {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        //primaryStage.setMinHeight(300);
        //primaryStage.setMinWidth(300);
        //primaryStage.setResizable(false);
        primaryStage.show();
      }
    } catch (Exception e) {
      AlertShower.showErrorAlert("Что-то пошло не так", e.getMessage());
      e.printStackTrace();
    }
  }
}
