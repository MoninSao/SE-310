package survey.model.question;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.model.response.EssayResponse;
import survey.model.response.Response;

/**
 * An essay question that accepts one or more free-text responses.
 * ShortAnswer extends this class with a character limit.
 *
 * Phase 1: display() implemented. take() / modify() are stubs — Phase 3.
 */
public class Essay extends Question {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an Essay question.
     *
     * @param prompt        the question text
     * @param allowMultiple true if the user may give more than one essay response
     */
    public Essay(String prompt, boolean allowMultiple) {
        super(prompt, allowMultiple);
    }

    /**
     * Displays the question prompt followed by an "[Essay response]" hint.
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.println(prompt);
        output.println("[Essay response]");
    }

    /**
     * Prompts the user for one or more essay answers.
     * If allowMultiple is true, first asks how many responses (up to 10),
     * then collects each one labelled A), B), C) …
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for prompts and errors
     * @return an EssayResponse containing all collected answers
     */
    @Override
    public Response take(InputHandler input, OutputHandler output) {
        display(output);
        EssayResponse response = new EssayResponse(prompt);
        int count = 1;
        if (allowMultiple) {
            count = input.readIntInRange("  How many responses? ", 1, 10);
        }
        for (int i = 0; i < count; i++) {
            String label = String.valueOf((char) ('A' + i));
            while (true) {
                String ans = input.readString("  " + label + ") ");
                if (validateAnswer(ans)) {
                    response.addAnswer(ans);
                    break;
                }
                output.printError("Response cannot be empty.");
            }
        }
        return response;
    }

    /**
     * Interactively modifies the question prompt and multi-response setting.
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for prompts and errors
     */
    @Override
    public void modify(InputHandler input, OutputHandler output) {
        output.println("Current prompt: " + prompt);
        String np = input.readString("  New prompt (press Enter to keep): ").trim();
        if (!np.isEmpty()) {
            setPrompt(np);
        }
        setAllowMultiple(input.readBoolean("  Allow multiple responses?"));
    }

    /**
     * Validates that the answer is non-null and non-empty.
     *
     * @param answer the candidate answer
     * @return true if the answer contains at least one non-whitespace character
     */
    @Override
    public boolean validateAnswer(String answer) {
        return answer != null && !answer.trim().isEmpty();
    }
}
