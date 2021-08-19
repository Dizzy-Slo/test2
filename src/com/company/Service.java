package com.company;

import org.jetbrains.annotations.NotNull;

public class Service {
  private final String name;
  private final ServicePrice price;

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

  @Override
  public String toString() {
    return name + " " +
      price.getPrice().toString()+price.getCurrency();
  }
}
