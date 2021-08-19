package com.company;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.Objects;

public class MainPageController {
  @FXML
  private TextField countryTextField;
  @FXML
  private ComboBox<Service> servicesComboBox;

  public MainPageController() {
    servicesComboBox = new ComboBox<>();
    countryTextField = new TextField();
  }

  @FXML
  private void findServices() {
    if (Objects.requireNonNull(ParserOnlineSim.getCountriesWithServicesMap()).containsKey(countryTextField.getText())) {
      servicesComboBox.getItems().clear();
      for (String s : ParserOnlineSim.getCountriesWithServicesMap().get(countryTextField.getText()).keySet()) {
        servicesComboBox.getItems().add(new Service(s, ParserOnlineSim.getCountriesWithServicesMap().get(countryTextField.getText()).get(s)));
      }
      servicesComboBox.setValue(servicesComboBox.getItems().get(0));
    } else {
      servicesComboBox.getItems().clear();
    }
  }
}
