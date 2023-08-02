package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager.AppUi;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static Scene scene;
  public static String riddleAnswer;
  public static String firstRiddleAnswer;

  public static void main(final String[] args) {
    launch();
  }

  public static void setRoot(AppUi window) throws IOException {
    scene.setRoot(SceneManager.getUiRoot(window));
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    List<String> keyLocationList = new ArrayList<String>();
    keyLocationList.add("vase");
    keyLocationList.add("window");
    Collections.shuffle(keyLocationList);
    SceneManager.addUi(AppUi.ROOM, loadFxml("room"));
    riddleAnswer = keyLocationList.get(0);
    firstRiddleAnswer = keyLocationList.get(0);
    SceneManager.addUi(AppUi.DOOR_CHAT, loadFxml("chat"));
    riddleAnswer = keyLocationList.get(1);
    SceneManager.addUi(AppUi.SECOND_CHAT, loadFxml("chat"));

    scene = new Scene(SceneManager.getUiRoot(AppUi.ROOM), 600, 470);
    stage.setScene(scene);
    stage.show();
    SceneManager.getUiRoot(AppUi.ROOM).requestFocus();
  }
}
