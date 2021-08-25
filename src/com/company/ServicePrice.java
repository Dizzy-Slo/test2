package com.company;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.zip.DataFormatException;

public class ServicePrice implements Comparable<ServicePrice> {
  private BigDecimal price;
  private final String currency;

  public ServicePrice(@NotNull BigDecimal price, @NotNull String currency) {
    this.price = price;
    this.currency = currency;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getCurrency() {
    return currency;
  }

  public void setPrice(BigDecimal newPrice) throws DataFormatException {
    if (BigDecimal.ZERO.compareTo(newPrice) < 0) {
      price = newPrice;
    } else {
      throw new DataFormatException();
    }
  }

  @Override
  public String toString() {
    return ":{" +
      "\"price\":" + price +
      ", \"currency\":" + currency +
      '}';
  }

  @Override
  public int compareTo(@NotNull ServicePrice o) {
    return price.compareTo(o.price);
  }
}
