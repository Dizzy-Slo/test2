package com.company;

import currencyExchanger.Currency;
import currencyExchanger.CurrencyExchanger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.zip.DataFormatException;

public class ServicePrice implements Comparable<ServicePrice> {
  private BigDecimal price;
  private final Currency currency1;
  private final String currency;

  public ServicePrice(@NotNull BigDecimal price, @NotNull String currency) {
    this.price = price;
    this.currency = currency;
    this.currency1 = Currency.getBySymbol(currency);
  }

  public BigDecimal getPrice() {
    return price;
  }

  public String getCurrency() {
    return currency;
  }

  @Nullable
  public Currency getCurrency1() {
    return currency1;
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
    return price + currency;
  }

  @Override
  public int compareTo(@NotNull ServicePrice o) {
    if(currency.equals(o.currency)){
      return price.compareTo(o.price);
    }else {
      return CurrencyExchanger.comparingCurrency(this, o);
    }
  }
}
