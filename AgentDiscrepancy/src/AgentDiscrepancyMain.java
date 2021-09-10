import java.sql.SQLException;

public class AgentDiscrepancyMain {
  public static void main(String[] args) {
    try {
      AgentDiscrepancyCalculator agentDiscrepancyCalculator = new AgentDiscrepancyCalculator();
      agentDiscrepancyCalculator.writeToFile("AgentsDiscrepancy", true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}