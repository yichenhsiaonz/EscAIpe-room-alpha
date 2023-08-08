package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class EndController {
  @FXML private Label title;
  @FXML private Button generateEndingButton;
  @FXML private Button quitButton;
  @FXML private TextArea endingBox;
  private ChatCompletionRequest chatCompletionRequest;
  private TextToSpeech voice = new TextToSpeech();
  private String endPrompt;

  @FXML
  public void initialize() {
    // Initialization code goes here
    if (App.getWon()) {
      title.setText("You have escaped the room!");
      endPrompt =
          "The user has escaped an escape room. Write a short, one paragraph ending for them.";
    } else {
      title.setText("You have failed to escape the room!");
      endPrompt =
          "The user has failed to escape an escape room. Write a short, one paragraph ending for"
              + " them.";
    }
  }

  @FXML
  private void quitGame(ActionEvent event) {
    System.exit(0);
  }

  @FXML
  private void generateEnding(ActionEvent event) throws ApiProxyException, IOException {
    try {
      javafx.concurrent.Task<Void> promptTask =
          new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
              chatCompletionRequest =
                  new ChatCompletionRequest()
                      .setN(1)
                      .setTemperature(0.2)
                      .setTopP(0.5)
                      .setMaxTokens(200);
              runGpt(new ChatMessage("user", endPrompt));
              return null;
            }
          };
      new Thread(promptTask).start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void runGpt(ChatMessage prompt) throws ApiProxyException, IOException {
    generateEndingButton.setDisable(true);
    chatCompletionRequest.addMessage(prompt);
    try {
      javafx.concurrent.Task<Void> sendTask =
          new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
              ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
              Choice result = chatCompletionResult.getChoices().iterator().next();
              chatCompletionRequest.addMessage(result.getChatMessage());

              Runnable addGptMessage =
                  () -> endingBox.appendText(result.getChatMessage().getContent());
              Platform.runLater(addGptMessage);
              javafx.concurrent.Task<Void> readMessageTask =
                  new javafx.concurrent.Task<>() {
                    @Override
                    protected Void call() throws Exception {
                      voice.speak(result.getChatMessage().getContent());
                      return null;
                    }
                  };

              new Thread(readMessageTask).start();

              return null;
            }
          };
      new Thread(sendTask).start();

    } catch (Exception e) {
      // TODO handle exception appropriately
      e.printStackTrace();
    }
  }
}
