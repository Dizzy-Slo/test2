package com.company;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;
import java.util.zip.DataFormatException;

public class MainPageController {
  @FXML
  private TextField countryTextField;
  @FXML
  private TextField setPriceTextField;
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

  ToggleGroup sort = new ToggleGroup();

  @FXML
  private void initialize() throws Exception {
    initializeSortedMap(ParserOnlineSim.parse(false));

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

    setPriceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d+[.]?\\d*")) {
        setPriceTextField.setText(newValue.replaceAll("[^\\d]$", ""));
      }
    });
  }

  @FXML
  private void updateCountriesWithServicesMap() {
    Task<Void> initializeSortedMapsTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        updateButton.setDisable(true);
        findProgressBar.setVisible(true);

        initializeSortedMap(ParserOnlineSim.parse(false));

        findProgressBar.setVisible(false);
        updateButton.setDisable(false);
        return null;
      }
    };

    initializeSortedMapsTask.setOnSucceeded(event -> {
      getServices();
      nameAscRadioButton.fire();
      nameAscRadioButton.requestFocus();
    });

    findProgressBar.progressProperty().bind(initializeSortedMapsTask.progressProperty());
    new Thread(initializeSortedMapsTask).start();
  }

  @FXML
  private void getServices() {
    String inputCountry = findEqualCountryFromMap(countryTextField.getText().trim());
    List<Service> servicesList = servicesComboBox.getItems();

    servicesList.clear();
    if (inputCountry != null) {
      Map<String, ServicePrice> servicePriceMap = currentSortedCountriesWithServicesMap.get(inputCountry);
      for (String service : servicePriceMap.keySet()) {
        servicesList.add(new Service(service, servicePriceMap.get(service)));
      }
      servicesComboBox.setValue(servicesList.get(0));
    }
  }

  @FXML
  private void setPrice() {
    Service service = servicesComboBox.getValue();
    String newPrice = setPriceTextField.getText();

    if (service != null && !newPrice.isEmpty()) {
      try {
        service.setPrice(new BigDecimal(newPrice));
      } catch (DataFormatException e) {
        AlertShower.showErrorAlert("Неправильное значение", "Введите корректное значение в поле");
      }
      sortServicesByPrice(currentSortedCountriesWithServicesMap);

      getServices();
      servicesComboBox.setValue(service);
    }
  }

  private void changeCurrentSort(Map<String, Map<String, ServicePrice>> countriesWithServicesMap) {
    currentSortedCountriesWithServicesMap = countriesWithServicesMap;
    getServices();
  }

  private void initializeSortedMap(Map<String, Map<String, ServicePrice>> countriesWithServicesMap) {
    sortServicesByName(countriesWithServicesMap);
    sortServicesByPrice(countriesWithServicesMap);
    currentSortedCountriesWithServicesMap = countriesWithServicesMapSortedByNameAsc;
  }

  private void sortServicesByPrice(Map<String, Map<String, ServicePrice>> countriesWithServicesMap) {
    countriesWithServicesMapSortedByPriceAsc = sortServicesByPrice(true, countriesWithServicesMap);
    countriesWithServicesMapSortedByPriceDesc = sortServicesByPrice(false, countriesWithServicesMap);
  }

  private void sortServicesByName(Map<String, Map<String, ServicePrice>> countriesWithServicesMap) {
    countriesWithServicesMapSortedByNameAsc = sortServicesByName(true, countriesWithServicesMap);
    countriesWithServicesMapSortedByNameDesc = sortServicesByName(false, countriesWithServicesMap);
  }

  @Nullable
  private String findEqualCountryFromMap(@NotNull String inputCountry) {
    for (String country : currentSortedCountriesWithServicesMap.keySet()) {
      if (country.equalsIgnoreCase(inputCountry)) {
        return country;
      }
    }
    return null;
  }

  @NotNull
  private Map<String, Map<String, ServicePrice>> sortServicesByPrice(boolean ascSort,
                                                                     @NotNull Map<String, Map<String, ServicePrice>> countriesWithServicesMap) {
    Map<String, Map<String, ServicePrice>> sortedCountriesWithServicesMap = new LinkedHashMap<>();
    for (String country : countriesWithServicesMap.keySet()) {
      Map<String, ServicePrice> sortedServicesMap = new LinkedHashMap<>();
      if (ascSort) {
        countriesWithServicesMap.get(country)
          .entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue())
          .forEach(serviceWithPriceEntry -> sortedServicesMap.put(serviceWithPriceEntry.getKey(), serviceWithPriceEntry.getValue()));
      } else {
        countriesWithServicesMap.get(country)
          .entrySet()
          .stream()
          .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
          .forEach(serviceWithPriceEntry -> sortedServicesMap.put(serviceWithPriceEntry.getKey(), serviceWithPriceEntry.getValue()));
      }
      sortedCountriesWithServicesMap.put(country, sortedServicesMap);
    }
    return sortedCountriesWithServicesMap;
  }

  @NotNull
  private Map<String, Map<String, ServicePrice>> sortServicesByName(boolean ascSort,
                                                                    @NotNull Map<String, Map<String, ServicePrice>> countriesWithServicesMap) {
    Map<String, Map<String, ServicePrice>> sortedCountriesWithServicesMap = new LinkedHashMap<>();
    for (String country : countriesWithServicesMap.keySet()) {
      Map<String, ServicePrice> sortedServicesMap;
      if (ascSort) {
        sortedServicesMap = new TreeMap<>(countriesWithServicesMap.get(country));
      } else {
        sortedServicesMap = new TreeMap<>(Collections.reverseOrder());
        sortedServicesMap.putAll(countriesWithServicesMap.get(country));
      }
      sortedCountriesWithServicesMap.put(country, sortedServicesMap);
    }
    return sortedCountriesWithServicesMap;
  }
}

