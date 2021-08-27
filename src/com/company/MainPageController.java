package com.company;

import javafx.application.Platform;
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
  private ComboBox<String> countriesComboBox;
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

  private String currentCountry;
  private Map<String, Map<String, ServicePrice>> currentSortedCountriesWithServicesMap;
  private final ToggleGroup sort = new ToggleGroup();

  @FXML
  private void initialize() throws Exception {
    currentSortedCountriesWithServicesMap = ParserOnlineSim.parse(false);

    countriesComboBox.getItems().addAll(currentSortedCountriesWithServicesMap.keySet());

    nameAscRadioButton.setToggleGroup(sort);
    nameDescRadioButton.setToggleGroup(sort);
    priceAscRadioButton.setToggleGroup(sort);
    priceDescRadioButton.setToggleGroup(sort);

    sort.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
      if (currentCountry != null) {
        switch (newValue.getUserData().toString()) {
          case "nameAsc": {
            changeCurrentSort(currentCountry, true, true);
            return;
          }
          case "nameDesc": {
            changeCurrentSort(currentCountry, true, false);
            return;
          }
          case "priceAsc": {
            changeCurrentSort(currentCountry, false, true);
            return;
          }
          case "priceDesc": {
            changeCurrentSort(currentCountry, false, false);
          }
        }
      }
    });

    setPriceTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d+[.]?\\d*")) {
        setPriceTextField.setText(newValue.replaceAll("[^\\d]$", ""));
      }
    });

    countryTextField.setOnAction((event) -> {
      String inputCountry = findEqualCountryFromMap(countryTextField.getText().trim());
      if (inputCountry != null) {
        changeCurrentCountry(inputCountry, true);
      }
    });

    countriesComboBox.setOnAction((event) -> changeCurrentCountry(countriesComboBox.getValue(), false));

    servicesComboBox.setOnAction((event) ->
      Platform.runLater(() ->
        setPriceTextField.setText(servicesComboBox.getValue().getServicePrice().getPrice().toString())));
  }

  @FXML
  private void updateCountriesWithServicesMap() {
    Task<Void> initializeSortedMapsTask = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        updateButton.setDisable(true);
        findProgressBar.setVisible(true);

        currentSortedCountriesWithServicesMap = ParserOnlineSim.parse(false);

        findProgressBar.setVisible(false);
        updateButton.setDisable(false);

        Platform.runLater(() -> {
          nameAscRadioButton.fire();
          nameAscRadioButton.requestFocus();
        });
        return null;
      }
    };

    findProgressBar.progressProperty().bind(initializeSortedMapsTask.progressProperty());
    new Thread(initializeSortedMapsTask).start();
  }

  @FXML
  private void setServicesComboBox(boolean ascSort) {
    List<Service> servicesList = servicesComboBox.getItems();

    if (currentCountry != null) {
      servicesList.clear();
      Map<String, ServicePrice> servicePriceMap = currentSortedCountriesWithServicesMap.get(currentCountry);
      List<String> services = new LinkedList<>(servicePriceMap.keySet());

      if (!ascSort) {
        Collections.reverse(services);
      }
      for (String service : services) {
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
      if (AlertShower.showChangeAlert(service.getName(), service.getServicePrice().getPrice().toString(), newPrice)) {
        List<Service> services = servicesComboBox.getItems();
        int index = services.indexOf(service);

        try {
          service.setPrice(new BigDecimal(newPrice));
          services.remove(index);
          services.add(index, service);

          servicesComboBox.setValue(service);
        } catch (DataFormatException e) {
          AlertShower.showErrorAlert("Неправильное значение", "Введите корректное значение в поле");
        }
      }
    }
  }

  @Nullable
  private String findEqualCountryFromMap(@NotNull String inputCountry) {
    if (currentSortedCountriesWithServicesMap.containsKey(inputCountry)) {
      return inputCountry;
    } else {
      return null;
    }
  }

  private void changeCurrentCountry(@NotNull String inputCountry, boolean fromTextField) {
    currentCountry = inputCountry;

    if (fromTextField) {
      countriesComboBox.setValue(currentCountry);
    } else {
      countryTextField.setText(currentCountry);
    }

    if (nameAscRadioButton.isSelected()) {
      setServicesComboBox(true);
    } else {
      nameAscRadioButton.fire();
    }
  }

  private void changeCurrentSort(@NotNull String inputCountry, boolean byName, boolean ascSort) {
    if (byName) {
      sortCountryServicesByName(inputCountry);
    } else {
      sortCountryServicesByPrice(inputCountry);
    }
    setServicesComboBox(ascSort);
  }

  private void sortCountryServicesByPrice(@NotNull String inputCountry) {
    Map<String, ServicePrice> sortedServicesMap = new LinkedHashMap<>();
    currentSortedCountriesWithServicesMap.get(inputCountry)
      .entrySet()
      .stream()
      .sorted(Map.Entry.comparingByValue())
      .forEach(serviceWithPriceEntry -> sortedServicesMap.put(serviceWithPriceEntry.getKey(), serviceWithPriceEntry.getValue()));

    currentSortedCountriesWithServicesMap.remove(inputCountry);
    currentSortedCountriesWithServicesMap.put(inputCountry, sortedServicesMap);
  }

  private void sortCountryServicesByName(@NotNull String inputCountry) {
    Map<String, ServicePrice> sortedServicesMap = new LinkedHashMap<>();
    currentSortedCountriesWithServicesMap.get(inputCountry)
      .entrySet()
      .stream()
      .sorted(Map.Entry.comparingByKey())
      .forEach(serviceWithPriceEntry -> sortedServicesMap.put(serviceWithPriceEntry.getKey(), serviceWithPriceEntry.getValue()));

    currentSortedCountriesWithServicesMap.remove(inputCountry);
    currentSortedCountriesWithServicesMap.put(inputCountry, sortedServicesMap);
  }
}

