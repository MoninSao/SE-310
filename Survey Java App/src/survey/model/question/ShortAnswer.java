package survey.model.question;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.model.response.Response;
import survey.model.response.ShortAnswerResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A short-answer question with an enforced character limit.
 * Extends Essay because short answers are a constrained form of free-text
 * response.
 *
 * Phase 1: display() implemented. take() / modify() are stubs — Phase 3.
 */
public class ShortAnswer extends Essay {

    private static final long serialVersionUID = 1L;

    /** Maximum characters allowed in a single response. */
    private int maxChars;

    /**
     * Constructs a ShortAnswer question.
     *
     * @param prompt        the question text
     * @param allowMultiple true if the user may give more than one short response
     * @param maxChars      maximum characters allowed per response
     */
    public ShortAnswer(String prompt, boolean allowMultiple, int maxChars) {
        super(prompt, allowMultiple);
        this.maxChars = maxChars;
    }

    /** @return the maximum character count for a single response */
    public int getMaxChars() {
        return maxChars;
    }

    /** @param maxChars the new maximum character count */
    public void setMaxChars(int maxChars) {
        this.maxChars = maxChars;
    }

    /**
     * Displays the question prompt followed by a hint showing the character limit.
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.println(prompt);
        output.println("[Short answer — max " + maxChars + " characters]");
    }

    /**
     * Prompts the user for one or more short answers, enforcing the maxChars
     * limit on each response via validateAnswer().
     * If allowMultiple is true, first asks how many responses (up to 10),
     * then collects each one labelled A), B), C) …
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for prompts and errors
     * @return a ShortAnswerResponse containing all collected answers
     */
    @Override
    public Response take(InputHandler input, OutputHandler output) {
        display(output);
        ShortAnswerResponse response = new ShortAnswerResponse(prompt);
        int count = 1;
        if (allowMultiple) {
            count = input.readIntInRange("  How many responses? ", 1, 10);
        }
        for (int i = 0; i < count; i++) {
            String label = allowMultiple ? String.valueOf((char) ('A' + i)) + ") " : "";
            while (true) {
                String ans = input.readString("  " + label);
                if (validateAnswer(ans)) {
                    response.addAnswer(ans);
                    break;
                }
                if (ans.trim().isEmpty()) {
                    output.printError("Response cannot be empty.");
                } else {
                    output.printError(
                            "Response must be at most " + maxChars + " characters (entered "
                                    + ans.length() + ").");
                }
            }
        }
        return response;
    }

    /**
     * Interactively modifies the question prompt and/or character limit.
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
        output.println("Current max chars: " + maxChars);
        setMaxChars(input.readIntInRange("  New max chars (1-500): ", 1, 500));
    }

    /**
     * Validates that the answer is non-empty and within the character limit.
     *
     * @param answer the candidate answer
     * @return true if the answer is non-blank and at most maxChars characters long
     */
    @Override
    public boolean validateAnswer(String answer) {
        return answer != null && !answer.trim().isEmpty() && answer.length() <= maxChars;
    }

    /**
     * Counts occurrences of each distinct answer string and prints
     * one line per distinct value in the form "value: n".
     *
     * @param responses the collected responses for this question
     * @param output    the OutputHandler to write to
     */
    @Override
    public void tabulate(List<Response> responses, OutputHandler output) {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
        for (Response r : responses) {
            for (String ans : r.getAnswers()) {
                counts.put(ans, counts.getOrDefault(ans, 0) + 1);
            }
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            output.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
