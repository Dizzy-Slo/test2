package com.company;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AlertShower {
  public static void showErrorAlert(@NotNull String message, @Nullable String description, boolean closeApp) {
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

    if (closeApp) {
      ButtonType accept = new ButtonType("Окей");
      alert.getButtonTypes().clear();
      alert.getButtonTypes().addAll(accept);
      Optional<ButtonType> option = alert.showAndWait();
      if (option.filter(buttonType -> buttonType == accept).isPresent()) {
        System.exit(0);
      }
    } else {
      alert.showAndWait();
    }
  }

  public static boolean showChangeAlert(@NotNull String field, @NotNull String oldValue, @NotNull String newValue) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Изменения");
    alert.setHeaderText("Принять изменения?");
    VBox alertDescriptionVBox = new VBox();

    String changeInfo = "Изменение значения в поле:\t" + field +
      "\nСтарое значение:\t" + oldValue +
      "\nНовое значение:\t" + newValue;
    TextArea descriptionTextArea = new TextArea();
    descriptionTextArea.setText(changeInfo);
    descriptionTextArea.setEditable(false);

    alertDescriptionVBox.getChildren().add(descriptionTextArea);
    alert.getDialogPane().setContent(alertDescriptionVBox);

    ButtonType accept = new ButtonType("Принять");
    ButtonType cancel = new ButtonType("Отменить");
    alert.getButtonTypes().clear();
    alert.getButtonTypes().addAll(accept, cancel);

    Optional<ButtonType> option = alert.showAndWait();
    return option.filter(buttonType -> buttonType == accept).isPresent();
  }
}
