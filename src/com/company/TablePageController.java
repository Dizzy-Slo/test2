package com.company;

import com.sun.javafx.collections.ObservableListWrapper;
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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.DataFormatException;

public class TablePageController {
  @FXML
  public TableColumn<Service, String> nameCol;
  @FXML
  public TableColumn<Service, String> priceCol;
  @FXML
  public TableColumn<Service, String> currencyCol;
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
        services.forEach((service, price) -> servicesList.add(new Service(service, price)));
        countriesWithServicesMap.put(country, new ObservableListWrapper<>(servicesList));
      }
    });

    priceCol.setCellValueFactory(serviceCellData ->
      new SimpleObjectProperty<>(serviceCellData
        .getValue()
        .getServicePrice()
        .getPrice()
        .toString()));

    priceCol.setCellFactory(TextFieldTableCell.forTableColumn());

    priceCol.setOnEditCommit((TableColumn.CellEditEvent<Service, String> event) -> {
      int rowIndex = event.getTablePosition().getRow();

      if (event.getNewValue().matches("\\d+[.]?\\d*")) {
        BigDecimal newPrice = new BigDecimal(event.getNewValue());
        Service currentService = event.getTableView().getItems().get(rowIndex);

        try {
          currentService.setServicePrice(newPrice);
        } catch (DataFormatException e) {
          showIncorrectAlert(rowIndex, event.getRowValue());
        }
      } else {
        showIncorrectAlert(rowIndex, event.getRowValue());
      }
    });

    currencyCol.setCellValueFactory(serviceCellData ->
      new SimpleObjectProperty<>(serviceCellData
        .getValue()
        .getServicePrice()
        .getCurrency()));

    countriesComboBox.getItems().addAll(countriesWithServicesMap.keySet());
    countriesComboBox.setValue(countriesComboBox.getItems().get(0));
    getServicesByCountry();

    countriesComboBox.setOnAction(event -> getServicesByCountry());

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

  private void showIncorrectAlert(int rowIndex, Service oldValue) {
    AlertShower.showErrorAlert("Неправильное значение", "Введите корректное положительное значение вида: dd.dd", false);
    servicesTableView.getItems().set(rowIndex, oldValue);
  }
}
