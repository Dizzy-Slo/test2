import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

class MttException extends Exception {
  public MttException(@NotNull LocalDate date, @NotNull DetailMttFileRow row) {
    super(date + ";" + row.getStringForException());
  }
}
