package database;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
  public static Connection getConnection() throws ClassNotFoundException, SQLException, FileNotFoundException {
    Properties connectionProperties = new Properties();

    Class.forName("com.mysql.cj.jdbc.Driver");

    return DriverManager.getConnection(connectionProperties.getActivateProperty("url"), connectionProperties.getActivateProperty("user"),
      connectionProperties.getActivateProperty("password"));
  }
}
