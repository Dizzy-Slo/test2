package currencyExchanger;

import com.company.ServicePrice;

import java.math.BigDecimal;

public class Test {
  public static void main(String[] args) {
    ServicePrice s1 = new ServicePrice(new BigDecimal(2), "$");
    ServicePrice s2 = new ServicePrice(new BigDecimal(41), "p");
    ServicePrice s3 = new ServicePrice(new BigDecimal(2), "€");
    System.out.println(s1.compareTo(s2));
    System.out.println(s1.compareTo(s3));
    System.out.println(s3.compareTo(s1));
  }
}
