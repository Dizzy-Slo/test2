package currencyExchanger;

import org.jetbrains.annotations.NotNull;
import org.omg.CORBA.UNKNOWN;

public enum Currency {
  USD("USD", new String[]{"$"}),
  EUR("EUR", new String[]{"€"}),
  RUB("RUB", new String[]{"р", "p", "₽"}),
  UNKNOWN("UNKNOWN", null);

  private final String name;
  private final String[] symbols;

  Currency(String name, String[] symbol) {
    this.name = name;
    this.symbols = symbol;
  }

  public String getName() {
    return name;
  }

  @NotNull
  public static Currency getBySymbol(@NotNull String symbol){
    for(Currency currency : Currency.values()){
      for (String currencySymbol : currency.symbols){
        if(currencySymbol.equals(symbol)){
          return currency;
        }
      }
    }
    return UNKNOWN;
  }
}
