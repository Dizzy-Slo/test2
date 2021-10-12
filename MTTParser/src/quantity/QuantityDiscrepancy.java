package quantity;

import org.jetbrains.annotations.NotNull;

public class QuantityDiscrepancy {
  private final Quantity databaseQuantity;
  private final Quantity mttQuantity;

  public QuantityDiscrepancy(@NotNull Quantity databaseQuantity, @NotNull Quantity fileQuantity) {
    this.databaseQuantity = databaseQuantity;
    this.mttQuantity = fileQuantity;
  }

  @NotNull
  public Quantity getDatabaseQuantity() {
    return databaseQuantity;
  }

  @NotNull
  public Quantity getMttQuantity() {
    return mttQuantity;
  }

  @NotNull
  public Quantity subtractMttQuantityFromDatabaseQuantity(){
    return databaseQuantity.subtract(mttQuantity);
  }
}
