import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ParserMainTest extends Application {
  public static void main(String[] args) {
    launch();
  }

  public void start(Stage primaryStage) throws Exception {
    FXMLLoader loader = new FXMLLoader(ParserMainTest.class.getResource("MttParser.fxml"));
    Scene scene = new Scene(loader.load());
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
