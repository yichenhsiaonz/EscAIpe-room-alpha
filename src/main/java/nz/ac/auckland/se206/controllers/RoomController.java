package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager;
import nz.ac.auckland.se206.SceneManager.AppUi;

/** Controller class for the room view. */
public class RoomController {

  @FXML private ImageView door;
  @FXML private ImageView doorGlow;
  @FXML private ImageView window;
  @FXML private ImageView windowGlow;
  @FXML private ImageView frameOpen;
  @FXML private ImageView frameClosed;
  @FXML private ImageView vase;
  @FXML private ImageView vaseGlow;
  @FXML private ImageView flower;
  @FXML private ProgressBar roomTimer;

  private boolean chatGenerated = false;

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
    // Either switch to the chat window or show a popup with different text depending on game state
    if (GameState.taskProgress == 0) {
      // switch to chat window
      App.showDialog(
          "Info",
          "You jiggle the door handle, but it's locked.",
          "A loud voice booms from the door: \"Solve this riddle to escape!\"");
      if (chatGenerated) {
        // chat window has already been generated, just switch to it
        App.setRoot(AppUi.DOOR_CHAT);
      } else {
        // chat window has not been generated, generate it and switch to it
        SceneManager.addUi(AppUi.DOOR_CHAT, App.loadFxml("chat"));
        chatGenerated = true;
        App.setRoot(AppUi.DOOR_CHAT);
      }

      return;
    } else if (GameState.taskProgress == 1) {
      // user has solved the riddle and must get the door key
      App.showDialog(
          "Info", "Find the key!", "You resolved the riddle, now you know where to go next.");
    } else {
      // user has the door key and wins the game
      GameState.taskProgress++;
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
    // show popup with different text depending on game state
    if (App.firstRiddleAnswer.equals("vase") && GameState.taskProgress == 1) {
      // put the flower in the vase and give user the door key
      flower.setVisible(true);
      App.showDialog(
          "Info",
          "You put the flower in the vase.",
          "A secret compartment opens with a key inside.");
      GameState.taskProgress++;
    } else if (App.firstRiddleAnswer.equals("vase") && GameState.taskProgress == 2) {
      // door key was in vase and user has door key
      App.showDialog(
          "Info", "There is nothing else in the compartment.", "You already found the key.");
    } else if (App.firstRiddleAnswer.equals("window") && GameState.taskProgress == 2) {
      // door key was in window and user has door key
      App.showDialog("Info", "Nothing happens.", "You already found the key.");
    } else {
      // user has not solved riddle yet
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
    try {
      System.out.println("window clicked");
      // show popup with different text depending on game state
      if (App.firstRiddleAnswer.equals("window") && GameState.taskProgress == 1) {
        // open the window and give user the door key
        frameClosed.setVisible(false);
        frameOpen.setVisible(true);
        App.showDialog(
            "Info",
            "You put the small key in the keyhole.",
            "It opens up just enough to lean out and grab a larger key on the windowsill outside.");
        GameState.taskProgress++;
      } else if (App.firstRiddleAnswer.equals("window") && GameState.taskProgress == 2) {
        // door key was in window and user has door key
        App.showDialog(
            "Info",
            "The opening isn't large enough to reach anything else.",
            "You already found the key.");
      } else if (App.firstRiddleAnswer.equals("vase") && GameState.taskProgress == 2) {
        // door key was in vase and user has door key
        App.showDialog("Info", "Nothing happens.", "You already found the key.");
      } else {
        // user has not solved riddle yet
        App.showDialog("Info", "Nothing happens.", " There is a small keyhole on the window.");
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }

  @FXML
  public void doorHovered(MouseEvent event) {
    doorGlow.setVisible(true);
  }

  @FXML
  public void doorUnhovered(MouseEvent event) {
    doorGlow.setVisible(false);
  }

  @FXML
  public void windowHovered(MouseEvent event) {
    windowGlow.setVisible(true);
  }

  @FXML
  public void windowUnhovered(MouseEvent event) {
    windowGlow.setVisible(false);
  }

  @FXML
  public void vaseHovered(MouseEvent event) {
    vaseGlow.setVisible(true);
  }

  @FXML
  public void vaseUnhovered(MouseEvent event) {
    vaseGlow.setVisible(false);
  }
}
