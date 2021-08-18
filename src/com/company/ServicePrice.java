package com.company;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class ServicePrice {
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
}
