package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GameState;
import nz.ac.auckland.se206.SceneManager.AppUi;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.GptPromptEngineering;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;

/** Controller class for the chat view. */
public class ChatController {
  @FXML private TextArea chatTextArea;
  @FXML private TextField inputText;
  @FXML private Button sendButton;
  @FXML private ProgressBar chatTimer;

  private String popupTitle;
  private String popupBody;
  private ChatCompletionRequest chatCompletionRequest;

  /**
   * Initializes the chat view, loading the riddle.
   *
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  @FXML
  public void initialize() throws ApiProxyException {
    chatTimer.progressProperty().bind(App.timerTask.progressProperty());

    String instanceRiddleAnswer = RoomController.currentRiddleAnswer;
    javafx.concurrent.Task<Void> promptTask =
        new javafx.concurrent.Task<>() {
          @Override
          protected Void call() throws Exception {
            chatCompletionRequest =
                new ChatCompletionRequest()
                    .setN(1)
                    .setTemperature(0.2)
                    .setTopP(0.5)
                    .setMaxTokens(100);
            runGpt(
                new ChatMessage(
                    "user", GptPromptEngineering.getRiddleWithGivenWord(instanceRiddleAnswer)));
            return null;
          }
        };
    new Thread(promptTask).start();
  }

  /**
   * Appends a chat message to the chat text area.
   *
   * @param msg the chat message to append
   */
  private void appendChatMessage(ChatMessage msg) {
    chatTextArea.appendText(msg.getRole() + ": " + msg.getContent() + "\n\n");
  }

  /**
   * Runs the GPT model with a given chat message.
   *
   * @param msg the chat message to process
   * @return the response chat message
   * @throws ApiProxyException if there is an error communicating with the API proxy
   */
  private void runGpt(ChatMessage msg) throws ApiProxyException, IOException {
    sendButton.setDisable(true);
    chatCompletionRequest.addMessage(msg);
    try {
      javafx.concurrent.Task<Void> sendTask =
          new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
              ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
              Choice result = chatCompletionResult.getChoices().iterator().next();
              chatCompletionRequest.addMessage(result.getChatMessage());
              Runnable addGptMessage = () -> appendChatMessage(result.getChatMessage());
              sendButton.setDisable(false);
              Platform.runLater(addGptMessage);

              // javafx.concurrent.Task<Void> readMessageTask =
              //     new javafx.concurrent.Task<>() {
              //       @Override
              //       protected Void call() throws Exception {
              //         App.voice.speak(result.getChatMessage().getContent());
              //         return null;
              //       }
              //     };
              // new Thread(readMessageTask).start();
              if (result.getChatMessage().getRole().equals("assistant")
                  && result.getChatMessage().getContent().startsWith("Correct")) {
                if (GameState.taskProgress == 0) {
                  switch (App.firstRiddleAnswer) {
                    case "window":
                      popupTitle = "A small key is passed through the mail slot";
                      popupBody = "You pick it up. It's too small to fit in the door lock.";
                      GameState.hasWindowKey = true;
                      break;
                    case "vase":
                      popupTitle = "A flower is passed through the mail slot";
                      popupBody = "You pick it up.";
                      GameState.hasFlower = true;
                      break;
                  }
                } else if (GameState.taskProgress == 1) {
                  switch (App.secondRiddleAnswer) {
                    case "vase":
                      popupTitle = "A hand reaches in through the window holding a flower.";
                      popupBody = "You take the flower.";
                      GameState.hasFlower = true;
                      break;
                    case "window":
                      popupTitle = "A second secret compartment opens up with a small key inside.";
                      popupBody = "You take the key.";
                      GameState.hasWindowKey = true;
                      break;
                  }
                }
                Runnable successPopup = () -> App.showDialog("Info", popupTitle, popupBody);
                App.setRoot(AppUi.ROOM);
                Platform.runLater(successPopup);
                GameState.taskProgress++;
              }
              return null;
            }
          };
      new Thread(sendTask).start();

    } catch (Exception e) {
      // TODO handle exception appropriately
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
    String message = inputText.getText();
    if (message.trim().isEmpty()) {
      return;
    }
    inputText.clear();
    ChatMessage msg = new ChatMessage("user", message);
    appendChatMessage(msg);
    runGpt(msg);
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
