package quantity;

import org.jetbrains.annotations.NotNull;
import view.Writeable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class QuantityWriter {
  public static void writeToFile(@NotNull File file, @NotNull Writeable writeable) throws IOException {
    try (FileWriter fileWriter = new FileWriter(file)) {
      String resultString = writeable.getStringForMttReport();
      fileWriter.write(resultString);
      fileWriter.flush();
    }
  }
}
