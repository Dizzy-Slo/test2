import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Properties {
  private final Map<String, String> hubPropertiesMap = new HashMap<>();
  private final Map<String, String> activatePropertiesMap = new HashMap<>();

  private static final String CONNECTION_HUB_CONFIG_PATH = "/ConnectionFiles/connection_hub.cfg";
  private static final String CONNECTION_ACTIVATE_CONFIG_PATH = "/ConnectionFiles/connection_activate.cfg";

  Properties() throws FileNotFoundException {
    readProperties(CONNECTION_ACTIVATE_CONFIG_PATH, activatePropertiesMap);
    readProperties(CONNECTION_HUB_CONFIG_PATH, hubPropertiesMap);
  }

  public void readProperties(@NotNull String pathToFile, @NotNull Map<String, String> propertiesMap) throws FileNotFoundException {
    String projectName = System.getProperty("user.dir");
    FileReader fileReader = new FileReader(projectName + pathToFile);
    Scanner scanner = new Scanner(fileReader);

    while (scanner.hasNext()) {
      String[] readProperty = scanner.nextLine().split("=");
      propertiesMap.put(readProperty[0], readProperty[1]);
    }
  }

  public String getHubProperty(@NotNull String key) {
    return hubPropertiesMap.get(key);
  }

  public String getActivateProperty(@NotNull String key) {
    return activatePropertiesMap.get(key);
  }
}
