package survey.model.question;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.model.response.Response;
import survey.model.response.TrueFalseResponse;

/**
 * A true/false question.
 * Extends MultipleChoice with fixed choices ["True", "False"].
 * Display shows "T/F" instead of the choice list to match the spec format.
 *
 * Phase 1: display() implemented. take() / modify() are stubs — Phase 2.
 */
public class TrueFalse extends MultipleChoice {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a TrueFalse question with the given prompt.
     * Choices are fixed to ["True", "False"]; multiple responses are not allowed.
     *
     * @param prompt the question text
     */
    public TrueFalse(String prompt) {
        super(prompt, false);
        choices.add("True");
        choices.add("False");
    }

    /**
     * Displays the question prompt followed by "T/F" on the next line,
     * matching the spec's display format.
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.println(prompt);
        output.println("T/F");
    }

    /**
     * Prompts the user for a T or F answer, normalises it to "True" or "False",
     * and returns a TrueFalseResponse.
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for prompts and errors
     * @return a TrueFalseResponse containing "True" or "False"
     */
    @Override
    public Response take(InputHandler input, OutputHandler output) {
        display(output);
        TrueFalseResponse response = new TrueFalseResponse(prompt);
        while (true) {
            String ans = input.readString("  Enter T or F: ").trim().toLowerCase();
            if (validateAnswer(ans)) {
                String normalized = (ans.equals("t") || ans.equals("true")) ? "True" : "False";
                response.addAnswer(normalized);
                return response;
            }
            output.printError("Please enter T or F.");
        }
    }

    /**
     * Interactively modifies the question prompt.
     * The T/F choices are fixed and cannot be changed.
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
    }

    /**
     * Validates that the answer is a recognisable true/false value.
     *
     * @param answer the candidate answer
     * @return true if the answer is t/f/true/false (case-insensitive)
     */
    @Override
    public boolean validateAnswer(String answer) {
        if (answer == null)
            return false;
        String a = answer.trim().toLowerCase();
        return a.equals("t") || a.equals("f") || a.equals("true") || a.equals("false");
    }
}
