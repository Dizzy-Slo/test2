package com.company;

import currencyExchanger.Currency;
import currencyExchanger.CurrencyExchanger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.zip.DataFormatException;

public class ServicePrice implements Comparable<ServicePrice> {
  private BigDecimal price;
  private final Currency currency;
  private final String currencySymbol;

  public ServicePrice(@NotNull BigDecimal price, @NotNull String currencySymbol) {
    this.price = price;
    this.currencySymbol = currencySymbol;
    this.currency = Currency.getBySymbol(currencySymbol);
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  @Nullable
  public Currency getCurrency() {
    return currency;
  }

  public void setPrice(@NotNull BigDecimal newPrice) throws DataFormatException {
    if (BigDecimal.ZERO.compareTo(newPrice) < 0) {
      price = newPrice;
    } else {
      throw new DataFormatException();
    }
  }

  @Override
  public String toString() {
    return price + currencySymbol;
  }

  @Override
  public int compareTo(@NotNull ServicePrice o) {
    if (currencySymbol.equals(o.currencySymbol)) {
      return price.compareTo(o.price);
    } else {
      return CurrencyExchanger.comparingCurrency(this, o);
    }
  }
}
