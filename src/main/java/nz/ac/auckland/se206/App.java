package nz.ac.auckland.se206;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.speech.TextToSpeech;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static Scene scene;
  public static String firstRiddleAnswer;
  public static String secondRiddleAnswer;
  public static javafx.concurrent.Task<Void> timerTask;
  public static TextToSpeech voice = new TextToSpeech();
  private static boolean gameOver = false;

  public static void main(final String[] args) {
    List<String> keyLocationList = new ArrayList<String>();
    keyLocationList.add("vase");
    keyLocationList.add("window");
    Collections.shuffle(keyLocationList);
    firstRiddleAnswer = keyLocationList.get(0);
    secondRiddleAnswer = keyLocationList.get(1);
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
  public static Parent loadFxml(String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * Displays a dialog box with the given title, header text, and message.
   *
   * @param title the title of the dialog box
   * @param headerText the header text of the dialog box
   * @param message the message content of the dialog box
   */
  public static void showDialog(String title, String headerText, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
    if (gameOver) {
      System.exit(0);
    }
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {

    timerTask =
        new javafx.concurrent.Task<>() {
          @Override
          protected Void call() throws Exception {
            String popupTitle;
            String popupBody;
            int timer = 120;
            updateProgress(120, 120);

            while (timer != 0 && GameState.taskProgress != 4) {
              Thread.sleep(1000);
              timer--;
              updateProgress(timer, 120);
            }
            if (GameState.taskProgress == 4) {
              popupTitle = "You have escaped the room!";
              popupBody = "Congratulations! You have escaped the room!";

            } else {
              popupTitle = "You ran out of time!";
              popupBody = "You have failed to escape the room in time. Better luck next time!";
            }
            gameOver = true;
            Runnable endPopup = () -> App.showDialog("Game Over", popupTitle, popupBody);
            Platform.runLater(endPopup);
            return null;
          }
        };
    SceneManager.addUi(AppUi.ROOM, loadFxml("room"));
    scene = new Scene(SceneManager.getUiRoot(AppUi.ROOM), 600, 470);
    stage.setScene(scene);
    stage.show();
    SceneManager.getUiRoot(AppUi.ROOM).requestFocus();
  }
}
