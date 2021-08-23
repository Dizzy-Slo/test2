package com.company;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlertShower {
  public static void showErrorAlert(@NotNull String message, @Nullable String description) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(message);

    if (description != null) {
      VBox alertDescriptionVBox = new VBox();

      Label descriptionLabel = new Label("Описание:");
      TextArea descriptionTextArea = new TextArea();
      descriptionTextArea.setText(description);
      descriptionTextArea.setEditable(false);

      alertDescriptionVBox.getChildren().addAll(descriptionLabel, descriptionTextArea);
      alert.getDialogPane().setContent(alertDescriptionVBox);
    }

    alert.showAndWait();
  }

  public static void showWaitingAlert(@NotNull String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Info");
    alert.setHeaderText(message);

    alert.show();
  }
}
