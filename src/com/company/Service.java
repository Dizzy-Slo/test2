package com.company;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Random;
import java.util.zip.DataFormatException;

public class Service {
  private final ServicePrice servicePrice;
  private final String name;
  private int quantity;

  public Service(@NotNull String name, @NotNull ServicePrice price) {
    this.name = name;
    this.servicePrice = price;

    Random random = new Random();
    quantity = random.nextInt(5000);
  }

  public String getName() {
    return name;
  }

  public ServicePrice getServicePrice() {
    return servicePrice;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity() {
    if (quantity > 0) {
      quantity--;
    }
  }

  public void setServicePrice(@NotNull BigDecimal newPrice) throws DataFormatException {
    servicePrice.setPrice(newPrice);
  }

  @Override
  public String toString() {
    return name + " \t"
      + servicePrice.getPrice().toString()
      + servicePrice.getCurrency() + " \t"
      + quantity + " шт";
  }
}
