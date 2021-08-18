package com.company;

import java.math.BigDecimal;

public class ServicePrice {
  private final BigDecimal price;
  private final String currency;

  public ServicePrice(BigDecimal price, String currency) {
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
