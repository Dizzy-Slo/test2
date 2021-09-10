import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Properties {
  //key: propertyName, value: propertyValue
  private final Map<String, String> propertiesMap = new HashMap<>();

  private static final String CONNECTION_CONFIG_PATH = "/ConnectionFiles/connection.cfg";

  Properties() throws FileNotFoundException {
    readProperties();
  }

  public void readProperties() throws FileNotFoundException {
    String projectName = System.getProperty("user.dir");
    FileReader fileReader = new FileReader(projectName + CONNECTION_CONFIG_PATH);
    Scanner scanner = new Scanner(fileReader);

    while (scanner.hasNext()) {
      String[] readProperty = scanner.nextLine().split("=");
      propertiesMap.put(readProperty[0], readProperty[1]);
    }

  }

  public String getProperty(@NotNull String key) {
    return propertiesMap.get(key);
  }
}
