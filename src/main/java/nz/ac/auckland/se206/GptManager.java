package nz.ac.auckland.se206;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionRequest;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult;
import nz.ac.auckland.se206.gpt.openai.ChatCompletionResult.Choice;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class GptManager {
  private static TextToSpeech voice = new TextToSpeech();

  @FXML private TextArea outputBox;
  private ChatCompletionRequest chatCompletionRequest;
  

  public GptManager(TextArea outputBox) throws IOException {
    try {
      this.outputBox = outputBox;
      // set the parameters for the GPT model
      chatCompletionRequest =
          new ChatCompletionRequest().setN(1).setTemperature(0.2).setTopP(0.5).setMaxTokens(100);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ChatMessage runGpt(String prompt) throws IOException, ApiProxyException {
    try {
      ChatMessage input = new ChatMessage("user", prompt);
      chatCompletionRequest.addMessage(input);
      // send the prompt
      // send the request and get the response
      ChatCompletionResult chatCompletionResult = chatCompletionRequest.execute();
      Choice result = chatCompletionResult.getChoices().iterator().next();
      // add the response to the history
      chatCompletionRequest.addMessage(result.getChatMessage());
      return result.getChatMessage();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public void addMessage(String header, ChatMessage message) {
    Runnable addMessageTask =
        new Runnable() {
          @Override
          public void run() {
            outputBox.appendText(header + message.getContent() + "\n\n");
          }
        };
    Platform.runLater(addMessageTask);
  }

  public void readMessage(String message) {
    javafx.concurrent.Task<Void> readMessageTask =
        new javafx.concurrent.Task<>() {
          @Override
          protected Void call() throws Exception {
            // read the message out loud
            voice.speak(message);
            return null;
          }
        };
    new Thread(readMessageTask).start();
  }
}
