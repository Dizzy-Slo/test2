package com.company;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MainPageController {
  @FXML
  private TextField countryTextField;
  @FXML
  private ComboBox<Service> servicesComboBox;

  private Map<String, Map<String, ServicePrice>> countriesWithServicesPriceMap;

  public MainPageController() throws Exception {
    findServices();
  }

  @FXML
  private void findServices() throws Exception {
    countriesWithServicesPriceMap = ParserOnlineSim.parse(false);
  }

  @FXML
  private void getServices() {
    servicesComboBox.getItems().clear();
    String inputCountry = findEqualCountryFromMap(countryTextField.getText().trim());
    if (inputCountry != null) {
      Map<String, ServicePrice> servicePriceMap = countriesWithServicesPriceMap.get(inputCountry);
      for (String service : servicePriceMap.keySet()) {
        servicesComboBox.getItems().add(new Service(service, servicePriceMap.get(service)));
      }
      servicesComboBox.setValue(servicesComboBox.getItems().get(0));
    }
  }

  @Nullable
  private String findEqualCountryFromMap(@NotNull String inputCountry) {
    for (String country : countriesWithServicesPriceMap.keySet()) {
      if (country.equalsIgnoreCase(inputCountry)) {
        return country;
      }
    }
    return null;
  }
}

