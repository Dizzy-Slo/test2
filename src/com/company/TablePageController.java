package com.company;

import com.sun.javafx.collections.ObservableListWrapper;
import customTableView.HighlightRow;
import customTableView.PriceCell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class TablePageController {
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

  private final Map<String, ObservableList<Service>> countriesWithServicesMap = new TreeMap<>();

  @FXML
  private void initialize() {
    Main.getParsedCountriesWithServicesMap().forEach((country, services) -> {
      List<Service> servicesList = new LinkedList<>();
      if (services != null) {
        services.forEach((service, price) -> {
          String serviceName = service.substring(0, 1).toUpperCase(Locale.ROOT) + service.substring(1);

          servicesList.add(new Service(serviceName, price));
        });
        countriesWithServicesMap.put(country, new ObservableListWrapper<>(servicesList));
      }
    });

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

  private void getServicesByCountry() {
    servicesTableView.setItems(countriesWithServicesMap.get(countriesComboBox.getValue()));
  }
}