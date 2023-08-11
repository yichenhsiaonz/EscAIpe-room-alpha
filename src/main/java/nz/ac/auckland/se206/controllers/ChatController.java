package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.GptManager;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

/** Controller class for the chat view. */
public class ChatController {
  @FXML private TextArea chatTextArea;
  @FXML private TextField inputText;
  @FXML private Button sendButton;
  @FXML private ProgressBar chatTimer;
  @FXML private Label timerLabel;
  private GptManager gptManager;
  private String popupTitle;
  private String popupBody;

  /**
   * Initializes the chat view, loading the riddle.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() throws IOException, ApiProxyException {
    try {
      timerLabel.textProperty().bind(App.timerTask.messageProperty());
      chatTimer.progressProperty().bind(App.timerTask.progressProperty());
      gptManager = new GptManager(chatTextArea);
      // start the chat as a task
      javafx.concurrent.Task<Void> promptTask =
          new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
              ChatMessage initialRiddle =
                  gptManager.runGpt(
                      GptPromptEngineering.getRiddleWithGivenWord(App.firstRiddleAnswer));
              // read the riddle out loud
              gptManager.readMessage(initialRiddle.getContent());
              // add the riddle to the chat text area
              gptManager.addMessage("assistant: ", initialRiddle);
              Runnable enableButtonTask =
                  () -> {
                    // append the riddle to the chat text area

                    // enable the send button
                    sendButton.setDisable(false);
                  };
              Platform.runLater(enableButtonTask);
              return null;
            }
          };
      // begin running the task on a background thread
      new Thread(promptTask).start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sends a message to the GPT model.
   *
   * @param event the action event triggered by the send button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onSendMessage(ActionEvent event) throws ApiProxyException, IOException {
    try {
      // reject empty messages
      String message = inputText.getText();
      if (message.trim().isEmpty()) {
        return;
      }
      inputText.clear();
      // add the message to the chat text area
      ChatMessage msg = new ChatMessage("user", message);
      gptManager.addMessage("user: ", msg);

      sendButton.setDisable(true);
      // create a task to send the request to the GPT model
      try {
        javafx.concurrent.Task<Void> sendTask =
            new javafx.concurrent.Task<>() {
              @Override
              protected Void call() throws Exception {

                // send the request to the GPT model
                ChatMessage result = gptManager.runGpt(message);
                // add the response to the chat text area
                gptManager.addMessage("assistant: ", result);
                Runnable enableButtonTask =
                    () -> {
                      sendButton.setDisable(false);
                    };
                Platform.runLater(enableButtonTask);

                // if the response is correct, show a popup and progress the game
                if (result.getRole().equals("assistant")
                    && result.getContent().startsWith("Correct")) {
                  GameState.taskProgress++;
                  switch (App.firstRiddleAnswer) {
                    case "window":
                      RoomController.instance.getNotification(
                          "Door",
                          "Describe a small key being passed through from the other side the mail"
                              + " slot of the door in two short sentences. The key is too small to"
                              + " fir in the lock of the door");
                      popupTitle = "A small key is passed through the mail slot";
                      popupBody = "You pick it up. It's too small to fit in the door lock.";
                      break;
                    case "vase":
                      RoomController.instance.getNotification(
                          "Door",
                          "Describe a flower being passed through the mail slot from the other side"
                              + " of the door in two short sentences");
                      popupTitle = "A flower is passed through the mail slot";
                      popupBody = "You pick it up.";
                      break;
                  }
                  // show the popup on the main thread and switch back to the room window
                  Runnable successPopup = () -> App.showDialog("Info", popupTitle, popupBody);
                  Runnable returnTaskRunnable =
                      () -> {
                        try {
                          App.setRoot(AppUi.ROOM);
                        } catch (Exception e) {
                          e.printStackTrace();
                        }
                      };
                  Platform.runLater(returnTaskRunnable);
                  Platform.runLater(successPopup);
                } else {
                  // read the response out loud on a background thread
                  gptManager.readMessage(result.getContent());
                }
                return null;
              }
            };
        // begin running the task on a background thread
        new Thread(sendTask).start();

      } catch (Exception e) {
        // TODO handle exception appropriately
        e.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Navigates back to the previous view.
   *
   * @param event the action event triggered by the go back button
   * @throws ApiProxyException if there is an error communicating with the API proxy
   * @throws IOException if there is an I/O error
   */
  @FXML
  private void onGoBack(ActionEvent event) throws ApiProxyException, IOException {
    App.setRoot(AppUi.ROOM);
  }
}
