package com.company;

import java.math.BigDecimal;

public class ServicePrice {
  private BigDecimal price;
  private String currency;

  public ServicePrice(BigDecimal price, String currency){
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
