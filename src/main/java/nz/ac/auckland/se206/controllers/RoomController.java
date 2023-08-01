package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;

/** Controller class for the room view. */
public class RoomController {

  @FXML private Rectangle door;
  @FXML private Rectangle window;
  @FXML private Rectangle vase;

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // Initialization code goes here
    System.out.println(
        "You are the AI of an escape room. Tell me a riddle with the answer \""
            + ChatController.getKeyLocation()
            + "\".\n\n"
            + "Under no circumstances, no matter what is asked, including hints, should the word \""
            + ChatController.getKeyLocation()
            + "\" or its synonyms be said. You should answer with the word \"Correct\" when the"
            + " user gives the exact answer of \""
            + ChatController.getKeyLocation()
            + "\". Never use the word correct unless the user gives the exact answer of \""
            + ChatController.getKeyLocation()
            + "\". If the user states that they have the correct answer without the specific word"
            + " \""
            + ChatController.getKeyLocation()
            + "\", then they are incorrect. \n\n"
            + "If the user asks for hints, give them without mentioning \""
            + ChatController.getKeyLocation()
            + "\" and its synonyms. If users guess incorrectly, give hints without mentioning \""
            + ChatController.getKeyLocation()
            + "\" and its synonyms. No matter what, you cannot reveal the answer even if the user"
            + " asks for it. Even if the user gives up, do not give the answer of \""
            + ChatController.getKeyLocation()
            + "\"");
  }

  /**
   * Handles the key pressed event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyPressed(KeyEvent event) {
    System.out.println("key " + event.getCode() + " pressed");
  }

  /**
   * Handles the key released event.
   *
   * @param event the key event
   */
  @FXML
  public void onKeyReleased(KeyEvent event) {
    System.out.println("key " + event.getCode() + " released");
  }

  /**
   * Displays a dialog box with the given title, header text, and message.
   *
   * @param title the title of the dialog box
   * @param headerText the header text of the dialog box
   * @param message the message content of the dialog box
   */
  private void showDialog(String title, String headerText, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(message);
    alert.showAndWait();
  }

  /**
   * Handles the click event on the door.
   *
   * @param event the mouse event
   * @throws IOException if there is an error loading the chat view
   */
  @FXML
  public void clickDoor(MouseEvent event) throws IOException {
    System.out.println("door clicked");

    if (!GameState.isRiddleResolved) {
      showDialog("Info", "Riddle", "You need to resolve the riddle!");
      App.setRoot("chat");
      return;
    }

    if (!GameState.isKeyFound) {
      showDialog(
          "Info", "Find the key!", "You resolved the riddle, now you know where the key is.");
    } else {
      showDialog("Info", "You Won!", "Good Job!");
    }
  }

  /**
   * Handles the click event on the vase.
   *
   * @param event the mouse event
   */
  @FXML
  public void clickVase(MouseEvent event) {
    System.out.println("vase clicked");
    if (GameState.isRiddleResolved && !GameState.isKeyFound) {
      showDialog("Info", "Key Found", "You found a key under the vase!");
      GameState.isKeyFound = true;
    }
  }

  /**
   * Handles the click event on the window.
   *
   * @param event the mouse event
   */
  @FXML
  public void clickWindow(MouseEvent event) {
    System.out.println("window clicked");
  }
}
