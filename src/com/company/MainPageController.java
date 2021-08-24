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

  private Map<String, Map<String, ServicePrice>> countriesWithServicesMapSortedByNameAsc;
  private Map<String, Map<String, ServicePrice>> countriesWithServicesMapSortedByNameDesc;
  private Map<String, Map<String, ServicePrice>> countriesWithServicesMapSortedByPriceAsc;
  private Map<String, Map<String, ServicePrice>> countriesWithServicesMapSortedByPriceDesc;
  private Map<String, Map<String, ServicePrice>> currentSortedCountriesWithServicesMap;

  @FXML
  private void initialize() throws Exception {
    initializeSortedMap(ParserOnlineSim.parse(false));

    ToggleGroup sort = new ToggleGroup();
    nameAscRadioButton.setToggleGroup(sort);
    nameDescRadioButton.setToggleGroup(sort);
    priceAscRadioButton.setToggleGroup(sort);
    priceDescRadioButton.setToggleGroup(sort);

    sort.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      switch (newValue.getUserData().toString()) {
        case "nameAsc": {
          changeCurrentSort(countriesWithServicesMapSortedByNameAsc);
          return;
        }
        case "nameDesc": {
          changeCurrentSort(countriesWithServicesMapSortedByNameDesc);
          return;
        }
        case "priceAsc": {
          changeCurrentSort(countriesWithServicesMapSortedByPriceAsc);
          return;
        }
        case "priceDesc": {
          changeCurrentSort(countriesWithServicesMapSortedByPriceDesc);
        }
      }
    });
    nameAscRadioButton.fire();
  }

  @FXML
  private void findServices() throws Exception {
    updateButton.setDisable(true);
    findProgressBar.setVisible(true);

    initializeSortedMap(ParserOnlineSim.parse(false));
    AlertShower.showWaitingAlert("Обновление прошло успешно");

    findProgressBar.setVisible(false);
    updateButton.setDisable(false);
  }

  @FXML
  private void getServices(){
    servicesComboBox.getItems().clear();
    String inputCountry = findEqualCountryFromMap(countryTextField.getText().trim());
    if (inputCountry != null) {
      Map<String, ServicePrice> servicePriceMap = currentSortedCountriesWithServicesMap.get(inputCountry);
      for (String service : servicePriceMap.keySet()) {
        servicesComboBox.getItems().add(new Service(service, servicePriceMap.get(service)));
      }
      servicesComboBox.setValue(servicesComboBox.getItems().get(0));
    }
  }

  @FXML
  private void changeCurrentSort(Map<String, Map<String, ServicePrice>> countriesWithServicesMap) {
    currentSortedCountriesWithServicesMap = countriesWithServicesMap;
    getServices();
  }

  @Nullable
  private String findEqualCountryFromMap(@NotNull String inputCountry) {
    for (String country : countriesWithServicesMapSortedByNameAsc.keySet()) {
      if (country.equalsIgnoreCase(inputCountry)) {
        return country;
      }
    }
    return null;
  }

  private void initializeSortedMap(Map<String, Map<String, ServicePrice>> countriesWithServicesPriceMap){
    countriesWithServicesMapSortedByNameAsc = sortServicesByName(true, countriesWithServicesPriceMap);
    countriesWithServicesMapSortedByNameDesc = sortServicesByName(false, countriesWithServicesPriceMap);
    countriesWithServicesMapSortedByPriceAsc = sortServicesByPrice(true, countriesWithServicesPriceMap);
    countriesWithServicesMapSortedByPriceDesc = sortServicesByPrice(false, countriesWithServicesPriceMap);
    currentSortedCountriesWithServicesMap = countriesWithServicesMapSortedByNameAsc;
  }

  @NotNull
  private Map<String, Map<String, ServicePrice>> sortServicesByPrice(boolean ascSort,
                                                                     @NotNull Map<String, Map<String, ServicePrice>> countriesWithServicesPriceMap) {
    Map<String, Map<String, ServicePrice>> countriesWithServicesMap = new LinkedHashMap<>();
    for (String country : countriesWithServicesPriceMap.keySet()) {
      Map<String, ServicePrice> sortedServicesMap = new LinkedHashMap<>();
      if (ascSort) {
        countriesWithServicesPriceMap.get(country)
          .entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue())
          .forEach(serviceWithPriceEntry -> sortedServicesMap.put(serviceWithPriceEntry.getKey(), serviceWithPriceEntry.getValue()));
      } else {
        countriesWithServicesPriceMap.get(country)
          .entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
          .forEach(e -> sortedServicesMap.put(e.getKey(), e.getValue()));
      }
      countriesWithServicesMap.put(country, sortedServicesMap);
    }
    return countriesWithServicesMap;
  }
  @NotNull
  private Map<String, Map<String, ServicePrice>> sortServicesByName(boolean ascSort,
                                                                    @NotNull Map<String, Map<String, ServicePrice>> countriesWithServicesPriceMap) {
    Map<String, Map<String, ServicePrice>> countriesWithServicesMap = new LinkedHashMap<>();
    for (String country : countriesWithServicesPriceMap.keySet()) {
      Map<String, ServicePrice> sortedServicesMap;
      if (ascSort) {
        sortedServicesMap = new TreeMap<>(countriesWithServicesPriceMap.get(country));
      } else {
        sortedServicesMap = new TreeMap<>(Collections.reverseOrder());
        sortedServicesMap.putAll(countriesWithServicesPriceMap.get(country));
      }
      countriesWithServicesMap.put(country, sortedServicesMap);
    }
    return countriesWithServicesMap;
  }
}

