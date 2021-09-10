import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Agent {
  private final int id;
  private final BigDecimal balance;
  private BigDecimal sumIn;
  private BigDecimal sumOut;

  private BigDecimal balanceDiscrepancy;

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

  @Nullable
  public BigDecimal getBalanceDiscrepancy() {
    return balanceDiscrepancy;
  }

  public int compareByDiscrepancy(Agent comparableAgent) {
    if (balanceDiscrepancy == null && comparableAgent.getBalanceDiscrepancy() == null) {
      return 0;
    } else if (balanceDiscrepancy == null) {
      return -1;
    } else if (comparableAgent.getBalanceDiscrepancy() == null) {
      return 1;
    } else {
      return balanceDiscrepancy.compareTo(comparableAgent.getBalanceDiscrepancy()) * -1;
    }
  }

  public String getFullInfo() {
    return "id = " + id +
      "\t\t sumIn = " + sumIn +
      "\t\t sumOut = " + sumOut +
      "\t\t balance = " + balance +
      "\t\t discrepancy = " + balanceDiscrepancy + "\n";
  }

  public void addSums(@NotNull BigDecimal sumIn, @NotNull BigDecimal sumOut) {
    this.sumIn = this.sumIn.add(sumIn);
    this.sumOut = this.sumOut.add(sumOut);
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
