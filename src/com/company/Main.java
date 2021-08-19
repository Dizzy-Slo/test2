package com.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  public static void main(String[] args) {
    ParserOnlineSim.parse();
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
    primaryStage.setScene(new Scene(loader.load()));
    primaryStage.setMinHeight(300);
    primaryStage.setMinWidth(500);
    primaryStage.show();
  }
}
