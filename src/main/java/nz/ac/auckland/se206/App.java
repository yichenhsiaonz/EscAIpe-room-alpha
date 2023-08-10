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

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static Scene scene;
  public static String firstRiddleAnswer;
  public static javafx.concurrent.Task<Void> timerTask;
  private static boolean won = false;

  public static void main(final String[] args) {
    List<String> keyLocationList = new ArrayList<String>();
    keyLocationList.add("vase");
    keyLocationList.add("window");
    Collections.shuffle(keyLocationList);
    firstRiddleAnswer = keyLocationList.get(0);
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
    try {
      return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
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
  }

  public static boolean getWon() {
    return won;
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   */
  @Override
  public void start(final Stage stage) throws IOException {
    try {

      // create timer task to run in background persistently
      timerTask =
          new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
              String popupTitle;
              String popupBody;

              // set time to 120 seconds, update progress bar and decrease timer by one second every
              // second.
              Integer timer = 120;
              updateMessage("2:00");
              updateProgress(120, 120);

              // end upon victory or time up

              while (timer != 0 && GameState.taskProgress != 3) {
                Thread.sleep(1000);
                timer--;
                int minutes = timer / 60;
                int seconds = timer % 60;
                updateMessage(minutes + ":" + String.format("%02d", seconds));
                updateProgress(timer, 120);
              }

              if (GameState.taskProgress == 3) {
                // victory text
                popupTitle = "You have escaped the room!";
                popupBody = "Congratulations! You have escaped the room!";
                won = true;

              } else {
                // failure text
                popupTitle = "You ran out of time!";
                popupBody = "You have failed to escape the room in time. Better luck next time!";
              }
              // create alert on main thread
              Runnable endPopup = () -> showDialog("Game Over", popupTitle, popupBody);
              // create end window instance and switch to it on main thread
              Runnable endWindow =
                  () -> {
                    try {
                      SceneManager.addUi(AppUi.END, loadFxml("end"));
                      setRoot(AppUi.END);
                    } catch (IOException e) {
                      // TODO Auto-generated catch block
                      e.printStackTrace();
                    }
                  };
              Platform.runLater(endPopup);
              Platform.runLater(endWindow);
              return null;
            }
          };

      // generate the room and add it to the scene manager to allow for switching between chat
      // window and room without resetting
      SceneManager.addUi(AppUi.ROOM, loadFxml("room"));
      scene = new Scene(SceneManager.getUiRoot(AppUi.ROOM), 850, 470);
      stage.setScene(scene);
      stage.setResizable(false);
      stage.show();
      // end the program fully upon closing window
      stage.setOnCloseRequest(
          request -> {
            System.exit(0);
          });
      SceneManager.getUiRoot(AppUi.ROOM).requestFocus();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
