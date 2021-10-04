import org.jetbrains.annotations.NotNull;

public class Quantity {
  private final int smsCount;
  private int secCount;

  public Quantity(@NotNull String smsCount, @NotNull String secCount) {
    this.secCount = Integer.parseInt(secCount);
    this.smsCount = Integer.parseInt(smsCount);
  }

  public Quantity(int smsCount, int secCount) {
    this.secCount = secCount;
    this.smsCount = smsCount;
  }

  public int getSecCount() {
    return secCount;
  }

  public int getSmsCount() {
    return smsCount;
  }

  public void addSecCount(int secCount) {
    this.secCount += secCount;
  }

  public boolean isEquals(@NotNull Quantity o) {
    return secCount == o.getSecCount() && smsCount == o.getSmsCount();
  }

  public boolean isEmpty() {
    return secCount == 0 && smsCount == 0;
  }

  @Override
  public String toString() {
    return "Quantity{" +
      "smsCount=" + smsCount +
      ", secCount=" + secCount +
      '}';
  }

  public String getStringForCsvFile() {
    return secCount + " sec/" + smsCount + " count_sms";
  }
}