package customTableView;

import com.company.AlertShower;
import com.company.Service;
import com.company.ServicePrice;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.zip.DataFormatException;

public class PriceCell extends TableCell<Service, ServicePrice> {
  private TextField textField;

  @Override
  public void updateItem(ServicePrice item, boolean empty) {
    super.updateItem(item, empty);
    if (empty || item == null) {
      setText(null);
      setGraphic(null);
    } else {
      if (isEditing()) {
        if (textField != null) {
          textField.setText(getString(false));
        }
        setGraphic(textField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      } else {
        setText(getString(true));
        setContentDisplay(ContentDisplay.TEXT_ONLY);
      }
    }
  }

  @Override
  public void startEdit() {
    super.startEdit();
    if (textField == null) {
      createTextField();
    }
    setGraphic(textField);
    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    textField.requestFocus();
  }

  @Override
  public void cancelEdit() {
    super.cancelEdit();
    setText(getString(true));
    setContentDisplay(ContentDisplay.TEXT_ONLY);
  }

  @Override
  public void commitEdit(@NotNull ServicePrice newValue) {
    super.commitEdit(newValue);
    int rowIndex = getTableRow().getIndex();
    Service currentService = getTableView().getItems().get(rowIndex);
    try {
      currentService.setServicePrice(newValue.getPrice());
      setText(getItem().toString());
      setContentDisplay(ContentDisplay.TEXT_ONLY);
    } catch (DataFormatException e) {
      showIncorrectAlert(rowIndex, currentService);
    }
  }

  private void createTextField() {
    textField = new TextField(getString(false));

    textField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue.matches("\\d+[.]?\\d*")) {
        textField.setText(newValue.replaceAll("[^\\d]$", ""));
      }
    });

    textField.setOnAction((event ->
      commitEdit(new ServicePrice(new BigDecimal(textField.getText()), getItem().getCurrencySymbol()))));

    textField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ESCAPE) {
        cancelEdit();
      }
    });
  }

  private String getString(boolean withCurrency) {
    if (getItem() != null) {
      if (withCurrency) {
        return getItem().toString();
      } else {
        return getItem().getPrice().toString();
      }
    } else
      return "";
  }

  private void showIncorrectAlert(int rowIndex, Service oldValue) {
    AlertShower.showErrorAlert("Неправильное значение", "Введите корректное положительное значение вида: dd.dd", false);
    getTableView().getItems().set(rowIndex, oldValue);
  }
}
