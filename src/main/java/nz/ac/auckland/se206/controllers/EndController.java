package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import nz.ac.auckland.se206.App;
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

    // set the title based on whether the user won or lost
    // set the prompt based on whether the user won or lost
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
  private void clickQuit(MouseEvent event) {
    System.exit(0);
  }

  @FXML
  private void clickEnding(MouseEvent event) throws ApiProxyException, IOException {
    // disable the button so user can't generate multiple endings
    generateEndingButton.setDisable(true);
    try {
      javafx.concurrent.Task<Void> promptTask =
          new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws IOException, ApiProxyException {
              // generate a new gpt session as user may run out of time before opening the chat
              // window
              // use prompt based on whether the user won or lost
              chatCompletionRequest =
                  new ChatCompletionRequest()
                      .setN(1)
                      .setTemperature(0.2)
                      .setTopP(0.5)
                      .setMaxTokens(200);

              chatCompletionRequest.addMessage("user", endPrompt);
              ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
              Choice result = chatCompletionResult.getChoices().iterator().next();

              // add the gpt message to the text area on the main thread
              Runnable addGptMessage =
                  () -> endingBox.appendText(result.getChatMessage().getContent());
              Platform.runLater(addGptMessage);
              javafx.concurrent.Task<Void> readMessageTask =
                  new javafx.concurrent.Task<>() {
                    @Override
                    protected Void call() throws Exception {
                      // read the message out loud on a background thread
                      voice.speak(result.getChatMessage().getContent());
                      return null;
                    }
                  };

              new Thread(readMessageTask).start();
              return null;
            }
          };
      // run the task in a background thread
      new Thread(promptTask).start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
