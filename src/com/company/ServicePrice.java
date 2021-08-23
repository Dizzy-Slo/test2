package com.company;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class ServicePrice implements Comparable<ServicePrice> {
  private final BigDecimal price;
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
