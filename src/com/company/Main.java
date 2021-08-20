package com.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.logging.Level;

public class Main extends Application {

  public static void main(String[] args) throws Exception {
    if (ParserOnlineSim.parse(false) == null) {
      ParserOnlineSim.getLogger().log(Level.WARNING, "Can't parse OnlineSim");
      return;
    }
    Application.launch();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
    primaryStage.setScene(new Scene(loader.load()));
    primaryStage.setMinHeight(300);
    primaryStage.setMinWidth(300);
    primaryStage.setResizable(false);
    primaryStage.show();
  }
}
