package com.company;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class MainPageController {
  public TextField countryTextField;
  public ComboBox<Service> servicesComboBox;

  public MainPageController() {
    servicesComboBox = new ComboBox<>();
    countryTextField = new TextField();
  }

  public void findServices() {
    if (ParserOnlineSim.getCountriesWithServicesMap().containsKey(countryTextField.getText())) {
      for (String s : ParserOnlineSim.getCountriesWithServicesMap().get(countryTextField.getText()).keySet()){
        servicesComboBox.getItems().add(new Service(s, ParserOnlineSim.getCountriesWithServicesMap().get(countryTextField.getText()).get(s)));
      }
      servicesComboBox.setValue(servicesComboBox.getItems().get(0));
    }
    else {
      servicesComboBox.setItems(null);
    }
  }
}
