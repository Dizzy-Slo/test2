package parser;

import org.jetbrains.annotations.NotNull;
import quantity.Quantity;

import java.util.List;

public class DetailMttFileRowInfo {
  private final long phone;
  private final String date;
  private final List<Quantity> quantities;

  public DetailMttFileRowInfo(long phone, @NotNull String date, @NotNull List<Quantity> quantities) {
    this.phone = phone;
    this.date = date;
    this.quantities = quantities;
  }

  public long getPhone() {
    return phone;
  }

  @NotNull
  public String getDate() {
    return date;
  }

  @NotNull
  public List<Quantity> getQuantities() {
    return quantities;
  }

  @NotNull
  public String getStringForException() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(phone).append(";");

    int quantityCount = 0;
    for (Quantity quantity : quantities) {
      stringBuilder.append(quantity.getStringForCsvFile()).append(";");
      if (quantityCount % 4 == 0) {
        stringBuilder.append("\n");
      }
      quantityCount++;
    }
    return stringBuilder.toString();
  }
}
