package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;

/** Controller class for the room view. */
public class RoomController {

  @FXML private Rectangle door;
  @FXML private Rectangle window;
  @FXML private Rectangle vase;

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // Initialization code goes here
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

    if (GameState.taskprogress == 0) {
      showDialog(
          "Info",
          "A riddle is written on a note taped to the door.",
          "You need to resolve the riddle!");
      App.setRoot(AppUi.DOOR_CHAT);
      return;
    } else if (GameState.taskprogress == 1) {
      showDialog(
          "Info", "Find the key!", "You resolved the riddle, now you know where to go next.");
    } else if (GameState.taskprogress == 2) {
      showDialog("Info", "You've solved the second riddle!", "Now you know where the key is.");
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
  public void clickVase(MouseEvent event) throws IOException {
    System.out.println("vase clicked");
    if (GameState.hasFlower && GameState.taskprogress == 1) {
      showDialog(
          "Info",
          "You put the flower in the vase.",
          "A secret compartment opens with another riddle inside.");
      App.setRoot(AppUi.SECOND_CHAT);
    } else if (App.riddleAnswer.equals("vase") && GameState.taskprogress == 2) {
      showDialog(
          "Info",
          "You put the flower in the vase.",
          "A secret compartment opens with a larger key inside.");
      GameState.taskprogress++;
    } else if (App.firstRiddleAnswer.equals("vase") && GameState.taskprogress == 2) {
      showDialog(
          "Info", "There is nothing else in the compartment.", "You already have the small key.");
    } else if (GameState.taskprogress == 3) {
      showDialog("Info", "There is nothing else in the compartment.", "You already found the key.");
    } else {
      showDialog("Info", "Nothing happens.", " The vase is empty.");
    }
  }

  /**
   * Handles the click event on the window.
   *
   * @param event the mouse event
   */
  @FXML
  public void clickWindow(MouseEvent event) throws IOException {
    System.out.println("window clicked");
    if (GameState.hasWindowKey && GameState.taskprogress == 1) {
      showDialog(
          "Info",
          "You put the small key in the keyhole.",
          "It opens up just enough to lean out and read another riddle on the windowsill outside");
      App.setRoot(AppUi.SECOND_CHAT);
    } else if (App.riddleAnswer.equals("window") && GameState.taskprogress == 2) {
      showDialog(
          "Info",
          "You put the small key in the keyhole.",
          "It opens up just enough to lean out and grab a larger key on the windowsill outside.");
      GameState.taskprogress++;
    } else if (App.firstRiddleAnswer.equals("window") && GameState.taskprogress == 2) {
      showDialog(
          "Info",
          "The opening isn't large enough to reach anything else.",
          "You already have the flower.");
    } else if (GameState.taskprogress == 3) {
      showDialog(
          "Info",
          "The opening isn't large enough to reach anything else.",
          "You already found the key.");
    } else {
      showDialog("Info", "Nothing happens.", " There is a small keyhole on the window.");
    }
  }
}
