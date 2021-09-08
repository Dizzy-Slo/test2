import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class AgentDiscrepancyLogger {
  private static Logger logger;

  static {
    try (FileInputStream inputStream = new FileInputStream("AgentDiscrepancy/src/LogConfig.config")) {
      LogManager.getLogManager().readConfiguration(inputStream);
      logger = Logger.getLogger("AgentDiscrepancyLogger");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void log(String message, String exception) {
    logger.log(Level.WARNING, message + "\nИсключение:\n" + exception);
  }
}
