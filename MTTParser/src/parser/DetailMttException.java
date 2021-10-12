package parser;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public class DetailMttException extends Exception {
  public DetailMttException(@NotNull LocalDate date, @NotNull DetailMttFileRowInfo row) {
    super(date + ";" + row.getStringForException());
  }
}
