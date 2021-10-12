package view;

public class PhoneQuantityInfo {
  private final long phone;
  private final int mttCount;
  private final int databaseCount;

  PhoneQuantityInfo(long phone, int mttCount, int databaseCount) {
    this.phone = phone;
    this.mttCount = mttCount;
    this.databaseCount = databaseCount;
  }

  public long getPhone() {
    return phone;
  }

  public int getMttCount() {
    return mttCount;
  }

  public int getDatabaseCount() {
    return databaseCount;
  }
}
