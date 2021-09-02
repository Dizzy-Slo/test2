package com.company;

import com.sun.javafx.collections.ObservableListWrapper;
import customTableView.HighlightRow;
import customTableView.PriceCell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class TablePageController {
  private final Map<String, ObservableList<Service>> countriesWithServicesMap = new TreeMap<>();
  @FXML
  public TableColumn<Service, String> nameCol;
  @FXML
  public TableColumn<Service, ServicePrice> priceCol;
  @FXML
  private TableView<Service> servicesTableView;
  @FXML
  private ComboBox<String> countriesComboBox;
  @FXML
  private Button mainViewButton;
  @FXML
  private Button updateButton;
  @FXML
  private ProgressBar updateProgressBar;

  @FXML
  private void initialize() {
    setCountriesWithServicesMap(Main.getParsedCountriesWithServicesMap());

    servicesTableView.setRowFactory(param -> new HighlightRow());

    priceCol.setCellFactory(param -> new PriceCell());
    priceCol.setCellValueFactory(serviceCellData ->
      new SimpleObjectProperty<>(serviceCellData
        .getValue()
        .getServicePrice()));

    ObservableList<String> countriesList = countriesComboBox.getItems();
    countriesList.addAll(countriesWithServicesMap.keySet());

    countriesComboBox.setOnAction(event -> getServicesByCountry());
    countriesComboBox.setValue(countriesList.get(0));
    getServicesByCountry();

    servicesTableView.getSortOrder().add(nameCol);

    mainViewButton.setOnAction(event -> {
      ((Node) (event.getSource())).getScene().getWindow().hide();

      FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
      Stage tableStage = new Stage();

      try {
        tableStage.setScene(new Scene(loader.load()));
      } catch (IOException e) {
        e.printStackTrace();
      }
      tableStage.show();
    });
  }

  @FXML
  private void updateServices() {
    Task<Void> initializeSortedMapsTask = new Task<Void>() {
      @Override
      @Nullable
      protected Void call() {
        updateButton.setDisable(true);
        updateProgressBar.setVisible(true);

        setCountriesWithServicesMap(Main.updateCountryWithServicesMapList());
        getServicesByCountry();

        updateProgressBar.setVisible(false);
        updateButton.setDisable(false);
        return null;
      }
    };

    updateProgressBar.progressProperty().bind(initializeSortedMapsTask.progressProperty());
    new Thread(initializeSortedMapsTask).start();
  }

  /*private void setCountriesWithServicesMap(@NotNull Map<String, Map<String, ServicePrice>> newCountriesWithServicesMap) {
    countriesWithServicesMap.clear();
    newCountriesWithServicesMap.forEach((country, services) -> {
      List<Service> servicesList = new LinkedList<>();
      if (services != null) {
        services.forEach((service, price) -> {
          String serviceName = service.substring(0, 1).toUpperCase(Locale.ROOT) + service.substring(1);

          servicesList.add(new Service(serviceName, price));
        });
        countriesWithServicesMap.put(country, new ObservableListWrapper<>(servicesList));
      }
    });
  }*/

  private void setCountriesWithServicesMap(@NotNull Map<String, List<Service>> newCountriesWithServicesMap) {
    countriesWithServicesMap.clear();
    newCountriesWithServicesMap.forEach((country, services) -> {
      List<Service> servicesList = new LinkedList<>();
      if (services != null) {
        services.forEach(service -> {
          String serviceName = service.getName().substring(0, 1).toUpperCase(Locale.ROOT) + service.getName().substring(1);

          servicesList.add(new Service(serviceName, service.getServicePrice(), service.getQuantity()));
        });
        countriesWithServicesMap.put(country, new ObservableListWrapper<>(servicesList));
      }
    });
  }

  private void getServicesByCountry() {
    servicesTableView.setItems(countriesWithServicesMap.get(countriesComboBox.getValue()));
  }
}