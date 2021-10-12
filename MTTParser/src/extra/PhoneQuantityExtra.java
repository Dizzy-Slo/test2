package extra;

import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import view.Writeable;
import view.PhoneQuantityInfo;

import java.time.LocalDate;

public class PhoneQuantityExtra implements Writeable {
  ObservableList<PhoneQuantityInfo> items;
  LocalDate date;

  public PhoneQuantityExtra(@NotNull ObservableList<PhoneQuantityInfo> items, @NotNull LocalDate date){
    this.items = items;
    this.date = date;
  }

  @Override
  public String getStringForMttReport() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(date).append(";\n");
    stringBuilder.append("PHONE;DATABASE_COUNT;MTT_COUNT;\n");
    for (PhoneQuantityInfo row : items) {
      stringBuilder.append(row.getPhone()).append(";")
        .append(row.getDatabaseCount()).append(";")
        .append(row.getMttCount()).append(";\n");
    }
    return stringBuilder.toString();
  }
}
