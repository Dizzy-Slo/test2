import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class AgentDiscrepancyCalculator {
  private final static String USER = "";
  private final static String PASSWORD = "";
  private final static String DATABASE_URL = "jdbc:mysql://88.99.239.234/smshub";

  private Connection connection;
  private List<Agent> agentList = new ArrayList<>();

  AgentDiscrepancyCalculator() {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
      agentList = getAgentsFromDatabase();
    } catch (Exception e) {
      AgentDiscrepancyLogger.log("Ошибка при подключении к бд", e.getMessage());
    }
  }

  @NotNull
  public List<Agent> getAgentList() {
    return agentList;
  }

  public void writeToFile(@NotNull String fileName) {
    if (!agentList.isEmpty()) {
      agentList.sort(Comparator.comparing(Agent::getBalanceDiscrepancy).reversed());

      try (FileWriter fileWriter = new FileWriter(fileName)) {
        StringBuilder stringBuilder = new StringBuilder();
        int countOfAgentsWithDiscrepancy = 0;

        for (Agent agent : agentList) {
          if (agent.getBalanceDiscrepancy().compareTo(BigDecimal.ZERO) != 0) {
            countOfAgentsWithDiscrepancy++;
          }
          stringBuilder.append(agent.getFullInfo());
        }
        stringBuilder.append("\nКоличество агентов с расхождением: ").append(countOfAgentsWithDiscrepancy);
        fileWriter.write(stringBuilder.toString());
        fileWriter.flush();
      } catch (IOException e) {
        AgentDiscrepancyLogger.log("Ошибка при выводе списка агентов в файл", e.getMessage());
        e.printStackTrace();
      }
    }
  }

  @NotNull
  private List<Agent> getAgentsFromDatabase() throws SQLException {
    Map<Integer, Agent> agentMap = new HashMap<>();

    PreparedStatement ps = connection.prepareStatement(
      "SELECT agentId, SUM(sumIn) AS sumIn, SUM(sumOut) AS sumOut, balance " +
        "FROM `agentTransactionStats` " +
        "JOIN `agent` " +
        "ON agent.id = agentTransactionStats.agentId " +
        "GROUP BY `agentId`");
    ResultSet resultSet = ps.executeQuery();

    while (resultSet.next()) {
      agentMap.put(resultSet.getInt("agentId"), new Agent(resultSet));
    }

    ps = connection.prepareStatement(
      "SELECT agentId, SUM(sumIn) AS sumIn, SUM(sumOut) AS sumOut, createDate " +
        "FROM `agentTransaction` " +
        "WHERE createDate > CURRENT_DATE " +
        "GROUP by agentId");
    resultSet = ps.executeQuery();

    while (resultSet.next()) {
      int agentId = resultSet.getInt("agentId");

      if (agentMap.containsKey(agentId)) {
        agentMap.get(agentId).addSums(resultSet);
      }
    }

    List<Agent> agentList = new ArrayList<>();
    agentMap.forEach((integer, agent) -> agentList.add(agent));
    return agentList;
  }
}