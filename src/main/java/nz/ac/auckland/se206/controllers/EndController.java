package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.GptManager;
import nz.ac.auckland.se206.gpt.ChatMessage;
import nz.ac.auckland.se206.gpt.openai.ApiProxyException;

public class EndController {
  @FXML private Label title;
  @FXML private Button generateEndingButton;
  @FXML private Button quitButton;
  @FXML private TextArea endingBox;
  private String endPrompt;
  private GptManager gptManager;

  @FXML
  public void initialize() {
    // Initialization code goes here

    // set the title based on whether the user won or lost
    // set the prompt based on whether the user won or lost
    if (App.getWon()) {
      title.setText("You have escaped the room!");
      endPrompt =
          "The user has escaped an escape room. Write a short, three sentence ending for them.";
    } else {
      title.setText("You have failed to escape the room!");
      endPrompt =
          "The user has failed to escape an escape room. Write a short, three sentence ending for"
              + " them.";
    }
  }

  @FXML
  private void onClickQuit(ActionEvent event) {
    System.exit(0);
  }

  @FXML
  private void onClickEnding(ActionEvent event) throws ApiProxyException, IOException {
    // disable the button so user can't generate multiple endings
    generateEndingButton.setDisable(true);
    try {
      gptManager = new GptManager(endingBox);
      // start the chat as a task
      javafx.concurrent.Task<Void> promptTask =
          new javafx.concurrent.Task<>() {
            @Override
            protected Void call() throws Exception {
              ChatMessage ending = gptManager.runGpt(endPrompt);
              // append the riddle to the chat text area
              gptManager.addMessage("", ending);
              return null;
            }
          };
      // begin running the task on a background thread
      new Thread(promptTask).start();
      // run the task in a background thread
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
