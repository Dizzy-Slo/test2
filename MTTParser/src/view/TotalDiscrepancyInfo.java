package view;

import org.jetbrains.annotations.NotNull;
import quantity.Quantity;

import java.time.LocalDate;

public class TotalDiscrepancyInfo {
  private final LocalDate date;
  private final Quantity databaseQuantity;
  private final Quantity mttQuantity;
  private final int residualSms;
  private final int residualSec;

  public TotalDiscrepancyInfo(@NotNull LocalDate date, @NotNull Quantity databaseQuantity, @NotNull Quantity mttQuantity) {
    this.date = date;
    this.databaseQuantity = databaseQuantity;
    this.mttQuantity = mttQuantity;
    residualSec = databaseQuantity.getSecCount() - mttQuantity.getSecCount();
    residualSms = databaseQuantity.getSmsCount() - mttQuantity.getSmsCount();
  }

  public int getResidualSec() {
    return residualSec;
  }

  public int getResidualSms() {
    return residualSms;
  }

  public LocalDate getDate() {
    return date;
  }

  public Quantity getDatabaseQuantity() {
    return databaseQuantity;
  }

  public Quantity getMttQuantity() {
    return mttQuantity;
  }
}
