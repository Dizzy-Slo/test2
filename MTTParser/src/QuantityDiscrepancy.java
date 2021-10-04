import org.jetbrains.annotations.NotNull;

public class QuantityDiscrepancy {
  private final Quantity databaseQuantity;
  private final Quantity fileQuantity;

  public QuantityDiscrepancy(@NotNull Quantity databaseQuantity, @NotNull Quantity fileQuantity) {
    this.databaseQuantity = databaseQuantity;
    this.fileQuantity = fileQuantity;
  }

  public Quantity getDatabaseQuantity() {
    return databaseQuantity;
  }

  public Quantity getFileQuantity() {
    return fileQuantity;
  }
}
