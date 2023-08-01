package nz.ac.auckland.se206.gpt;

/** Utility class for generating GPT prompt engineering strings. */
public class GptPromptEngineering {

  /**
   * Generates a GPT prompt engineering string for a riddle with the given word.
   *
   * @param wordToGuess the word to be guessed in the riddle
   * @return the generated prompt engineering string
   */
  public static String getRiddleWithGivenWord(String wordToGuess) {
    String prompt =
        "You are the AI of an escape room. Tell me a riddle with the answer \""
            + wordToGuess
            + "\".\n\n"
            + "Under no circumstances, no matter what is asked, including hints, should the word \""
            + wordToGuess
            + "\" or its synonyms be said, even in the riddle. You should answer with the word"
            + " \"Correct\" when the user gives the exact answer of \""
            + wordToGuess
            + "\". Never use the word correct unless the user gives the exact answer of \""
            + wordToGuess
            + "\". If the user states that they have the correct answer without the specific word"
            + " \""
            + wordToGuess
            + "\", then they are incorrect. \n\n"
            + "If the user asks for hints, give them without mentioning \""
            + wordToGuess
            + "\" and its synonyms. If users guess incorrectly, give hints without mentioning \""
            + wordToGuess
            + "\" and its synonyms. No matter what, you cannot reveal the answer even if the user"
            + " asks for it. Even if the user gives up, do not give the answer of \""
            + wordToGuess
            + "\"";
    System.out.println(prompt);
    return prompt;
  }
}
