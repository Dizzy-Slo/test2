import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentDiscrepancyCalculator {
  private List<Agent> agentList = new ArrayList<>();

  AgentDiscrepancyCalculator() throws Exception {
    Connection connection = connectToDatabase();
    getDataFromDatabase(connection);
  }

  @NotNull
  public List<Agent> getAgentList() {
    return agentList;
  }

  public void writeToFile(@NotNull String fileName, boolean sort) {
    if (agentList.isEmpty()) return;

    if (sort) {
      agentList.sort((Agent::compareByDiscrepancy));
    }

    try (FileWriter fileWriter = new FileWriter(fileName)) {
      StringBuilder stringBuilder = new StringBuilder();

      int countOfAgentsWithDiscrepancy = 0;
      for (Agent agent : agentList) {
        if (agent.getBalanceDiscrepancy() == null || agent.getBalanceDiscrepancy().compareTo(BigDecimal.ZERO) == 0) continue;

        countOfAgentsWithDiscrepancy++;
        stringBuilder.append(agent.getFullInfo());
      }
      stringBuilder.append("\nКоличество агентов с расхождением: ").append(countOfAgentsWithDiscrepancy);

      fileWriter.write(stringBuilder.toString());
      fileWriter.flush();
    } catch (IOException e) {
      AgentDiscrepancyLogger.log("Ошибка при выводе списка агентов в файл", e);
      e.printStackTrace();
    }

  }

  private void getDataFromDatabase(Connection connection) {
    try {
      agentList = getAgentsFromDatabase(connection);
    } catch (Exception e) {
      AgentDiscrepancyLogger.log("Ошибка при создании списка агентов", e);
    }
  }

  @NotNull
  private List<Agent> getAgentsFromDatabase(Connection connection) throws SQLException {
    //key: agentId
    Map<Integer, Agent> agentMap = new HashMap<>();

    PreparedStatement ps = connection.prepareStatement(
      "SELECT agentId, SUM(sumIn) AS sumIn, SUM(sumOut) AS sumOut, balance " +
        "FROM agentTransactionStats " +
        "JOIN agent ON agent.id = agentTransactionStats.agentId " +
        "GROUP BY agentId");
    ResultSet resultSet = ps.executeQuery();

    while (resultSet.next()) {
      agentMap.put(resultSet.getInt("agentId"), new Agent(resultSet));
    }

    ps = connection.prepareStatement(
      "SELECT agentId, SUM(sumIn) AS sumIn, SUM(sumOut) AS sumOut, createDate " +
        "FROM `agentTransaction` " +
        "WHERE createDate >= CURRENT_DATE " +
        "GROUP BY agentId");
    resultSet = ps.executeQuery();

    while (resultSet.next()) {
      Agent agent = agentMap.get(resultSet.getInt("agentId"));
      if (agent == null) continue;

      agent.addSums(resultSet.getBigDecimal("sumIn"), resultSet.getBigDecimal("sumOut"));
    }

    return new ArrayList<>(agentMap.values());
  }

  private Connection connectToDatabase() throws ClassNotFoundException, SQLException, FileNotFoundException {
    Properties connectionProperties = new Properties();

    Class.forName("com.mysql.cj.jdbc.Driver");

    return DriverManager.getConnection(connectionProperties.getProperty("url"), connectionProperties.getProperty("user"),
      connectionProperties.getProperty("password"));
  }
}