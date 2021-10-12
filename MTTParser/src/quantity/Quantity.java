package quantity;

import org.jetbrains.annotations.NotNull;

public class Quantity {
  private int smsCount;
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

  @NotNull
  public Quantity subtract(@NotNull Quantity o) {
    return new Quantity(smsCount - o.getSmsCount(), secCount - o.getSecCount());
  }

  public void add(@NotNull Quantity o) {
    smsCount += o.getSmsCount();
    secCount += o.getSecCount();
  }

  @Override
  public String toString() {
    return "quantity.Quantity{" +
      "smsCount=" + smsCount +
      ", secCount=" + secCount +
      '}';
  }

  public String getStringForCsvFile() {
    if (smsCount == 0 && secCount != 0) {
      return secCount + " sec";
    } else if (smsCount != 0 && secCount == 0) {
      return smsCount + " sms";
    } else {
      return smsCount + " sms/" + secCount + " sec";
    }
  }
}