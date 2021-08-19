package com.company;

public class Service {
  private final String name;
  private final ServicePrice price;

  public Service(String name, ServicePrice price) {
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
