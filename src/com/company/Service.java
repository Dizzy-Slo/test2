package com.company;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.zip.DataFormatException;

public class Service {
  private final ServicePrice price;
  private final String name;

  public Service(@NotNull String name, @NotNull ServicePrice price) {
    this.name = name;
    this.price = price;
  }

  public String getName() {
    return name;
  }

  public ServicePrice getPrice() {
    return price;
  }

  public void setPrice(BigDecimal newPrice) throws DataFormatException {
    price.setPrice(newPrice);
  }

  @Override
  public String toString() {
    return name + " " +
      price.getPrice().toString() + price.getCurrency();
  }
}
