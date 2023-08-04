package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

/** Controller class for the room view. */
public class RoomController {

  @FXML private Rectangle door;
  @FXML private Rectangle window;
  @FXML private Rectangle vase;
  @FXML public ProgressBar roomTimer;

  private boolean doorGenerated = false;
  private boolean secondGenerated = false;
  public static String currentRiddleAnswer;

  /** Initializes the room view, it is called when the room loads. */
  public void initialize() {
    // Initialization code goes here
    App.showDialog(
        "Info",
        "You must escape the room!",
        "Maybe they left the door unlocked. Try clicking on it.");
    new Thread(App.timerTask).start();
    roomTimer.progressProperty().bind(App.timerTask.progressProperty());
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
   * Handles the click event on the door.
   *
   * @param event the mouse event
   * @throws IOException if there is an error loading the chat view
   */
  @FXML
  public void clickDoor(MouseEvent event) throws IOException {
    System.out.println("door clicked");

    if (GameState.taskProgress == 0) {
      App.showDialog(
          "Info",
          "A riddle is written on a note taped to the door.",
          "You need to resolve the riddle!");
      if (doorGenerated) {
        App.setRoot(AppUi.DOOR_CHAT);
      } else {
        currentRiddleAnswer = App.firstRiddleAnswer;
        SceneManager.addUi(AppUi.DOOR_CHAT, App.loadFxml("chat"));
        doorGenerated = true;
        App.setRoot(AppUi.DOOR_CHAT);
      }

      return;
    } else if (GameState.taskProgress == 1) {
      App.showDialog(
          "Info", "Find the key!", "You resolved the riddle, now you know where to go next.");
    } else if (GameState.taskProgress == 2) {
      App.showDialog("Info", "You've solved the second riddle!", "Now you know where the key is.");
    } else {
      GameState.taskProgress++;
      App.showDialog("Info", "You Won!", "Good Job!");
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
    if (GameState.hasFlower && GameState.taskProgress == 1) {
      App.showDialog(
          "Info",
          "You put the flower in the vase.",
          "A secret compartment opens with another riddle inside.");
      if (secondGenerated) {
        App.setRoot(AppUi.SECOND_CHAT);
      } else {
        currentRiddleAnswer = App.secondRiddleAnswer;
        SceneManager.addUi(AppUi.SECOND_CHAT, App.loadFxml("chat"));
        secondGenerated = true;
        App.setRoot(AppUi.SECOND_CHAT);
      }
    } else if (App.secondRiddleAnswer.equals("vase") && GameState.taskProgress == 2) {
      App.showDialog(
          "Info",
          "You put the flower in the vase.",
          "A secret compartment opens with a larger key inside.");
      GameState.taskProgress++;
    } else if (App.firstRiddleAnswer.equals("vase") && GameState.taskProgress == 2) {
      App.showDialog(
          "Info", "There is nothing else in the compartment.", "You already have the small key.");
    } else if (GameState.taskProgress == 3) {
      App.showDialog("Info", "There is nothing else in the compartment.", "You already found the key.");
    } else {
      App.showDialog("Info", "Nothing happens.", " The vase is empty.");
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
    if (GameState.hasWindowKey && GameState.taskProgress == 1) {
      App.showDialog(
          "Info",
          "You put the small key in the keyhole.",
          "It opens up just enough to lean out and read another riddle on the windowsill outside");
      if (secondGenerated) {
        App.setRoot(AppUi.SECOND_CHAT);
      } else {
        currentRiddleAnswer = App.secondRiddleAnswer;
        SceneManager.addUi(AppUi.SECOND_CHAT, App.loadFxml("chat"));
        secondGenerated = true;
        App.setRoot(AppUi.SECOND_CHAT);
      }
    } else if (App.secondRiddleAnswer.equals("window") && GameState.taskProgress == 2) {
      App.showDialog(
          "Info",
          "You put the small key in the keyhole.",
          "It opens up just enough to lean out and grab a larger key on the windowsill outside.");
      GameState.taskProgress++;
    } else if (App.firstRiddleAnswer.equals("window") && GameState.taskProgress == 2) {
      App.showDialog(
          "Info",
          "The opening isn't large enough to reach anything else.",
          "You already have the flower.");
    } else if (GameState.taskProgress == 3) {
      App.showDialog(
          "Info",
          "The opening isn't large enough to reach anything else.",
          "You already found the key.");
    } else {
      App.showDialog("Info", "Nothing happens.", " There is a small keyhole on the window.");
    }
  }
}
