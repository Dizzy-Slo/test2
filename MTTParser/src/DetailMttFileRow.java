import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DetailMttFileRow {
  long phone;
  String date;
  List<Quantity> quantities;

  DetailMttFileRow(long phone, @NotNull String date, @NotNull List<Quantity> quantities) {
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
