import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Agent {
  private final int id;
  private final BigDecimal balance;

  private BigDecimal sumIn;
  private BigDecimal sumOut;
  private BigDecimal balanceDiscrepancy = BigDecimal.ZERO;

  Agent(@NotNull ResultSet rs) throws SQLException {
    this.id = rs.getInt("agentId");
    this.sumIn = rs.getBigDecimal("sumIn");
    this.sumOut = rs.getBigDecimal("sumOut");
    this.balance = rs.getBigDecimal("balance");
  }

  public int getId() {
    return id;
  }

  @NotNull
  public BigDecimal getBalance() {
    return balance;
  }

  @NotNull
  public BigDecimal getSumIn() {
    return sumIn;
  }

  @NotNull
  public BigDecimal getSumOut() {
    return sumOut;
  }

  @NotNull
  public BigDecimal getBalanceDiscrepancy() {
    return balanceDiscrepancy;
  }

  public String getFullInfo() {
    return "id = " + id +
      "\t\t sumIn = " + sumIn +
      "\t\t sumOut = " + sumOut +
      "\t\t balance = " + balance +
      "\t\t discrepancy = " + balanceDiscrepancy + "\n";
  }

  public void addSums(@NotNull ResultSet resultSet) throws SQLException {
    this.sumIn = this.sumIn.add(resultSet.getBigDecimal("sumIn"));
    this.sumOut = this.sumOut.add(resultSet.getBigDecimal("sumOut"));
    calculateBalanceDiscrepancy();
  }

  private void calculateBalanceDiscrepancy() {
    BigDecimal sumSubtraction = sumIn.subtract(sumOut);
    balanceDiscrepancy = balance.subtract(sumSubtraction);
  }

  @Override
  public String toString() {
    return "Agent{" +
      "id=" + id +
      ", sumIn=" + sumIn +
      ", sumOut=" + sumOut +
      ", balance=" + balance +
      ", balanceDiscrepancy=" + balanceDiscrepancy +
      '}';
  }
}
