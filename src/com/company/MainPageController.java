package com.company;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class MainPageController {
  @FXML
  private TextField countryTextField;
  @FXML
  private ComboBox<Service> servicesComboBox;
  @FXML
  private ProgressBar findProgressBar;
  @FXML
  private Button updateButton;
  @FXML
  private RadioButton nameAscRadioButton;
  @FXML
  private RadioButton nameDescRadioButton;
  @FXML
  private RadioButton priceAscRadioButton;
  @FXML
  private RadioButton priceDescRadioButton;

  private Map<String, Map<String, ServicePrice>> countriesWithServicesPriceMap;

  @FXML
  private void initialize() throws Exception {
    countriesWithServicesPriceMap = ParserOnlineSim.parse(false);

    ToggleGroup sort = new ToggleGroup();
    nameAscRadioButton.setToggleGroup(sort);
    nameDescRadioButton.setToggleGroup(sort);
    priceAscRadioButton.setToggleGroup(sort);
    priceDescRadioButton.setToggleGroup(sort);

    sort.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      switch (newValue.getUserData().toString()) {
        case "nameAsc": {
          sortServicesByName(true);
          return;
        }
        case "nameDesc": {
          sortServicesByName(false);
          return;
        }
        case "priceAsc": {
          sortServicesByPrice(true);
          return;
        }
        case "priceDesc": {
          sortServicesByPrice(false);
        }
      }
    });
  }

  @FXML
  private void findServices() throws Exception {
    updateButton.setDisable(true);
    findProgressBar.setVisible(true);

    countriesWithServicesPriceMap = ParserOnlineSim.parse(false);
    AlertShower.showWaitingAlert("Обновление прошло успешно");

    findProgressBar.setVisible(false);
    updateButton.setDisable(false);
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

  private void sortServicesByPrice(boolean ascSort) {
    for (String country : countriesWithServicesPriceMap.keySet()) {
      Map<String, ServicePrice> sortedServicesMap = new LinkedHashMap<>();
      if (ascSort) {
        countriesWithServicesPriceMap.get(country).entrySet().stream()
          .sorted(Map.Entry.comparingByValue()).forEach(e -> sortedServicesMap.put(e.getKey(), e.getValue()));
      } else {
        countriesWithServicesPriceMap.get(country).entrySet().stream()
          .sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).forEach(e -> sortedServicesMap.put(e.getKey(), e.getValue()));
      }
      countriesWithServicesPriceMap.replace(country, sortedServicesMap);
    }
    getServices();
  }

  private void sortServicesByName(boolean ascSort) {
    for (String country : countriesWithServicesPriceMap.keySet()) {
      Map<String, ServicePrice> sortedServicesMap;
      if (ascSort) {
        sortedServicesMap = new TreeMap<>(countriesWithServicesPriceMap.get(country));
      } else {
        sortedServicesMap = new TreeMap<>(Collections.reverseOrder());
        sortedServicesMap.putAll(countriesWithServicesPriceMap.get(country));
      }
      countriesWithServicesPriceMap.replace(country, sortedServicesMap);
    }
    getServices();
  }
}

