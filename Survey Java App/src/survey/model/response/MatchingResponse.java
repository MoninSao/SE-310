package survey.model.response;

import survey.io.OutputHandler;

/**
 * Response to a matching question.
 * Each answer is a "letter number" pair (e.g. "A 2") — one per row.
 */
public class MatchingResponse extends Response {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a MatchingResponse for the given question prompt.
     *
     * @param questionPrompt the text of the question being answered
     */
    public MatchingResponse(String questionPrompt) {
        super(questionPrompt);
    }

    /**
     * Returns whether at least one matching pair has been recorded.
     *
     * @return true if the answer list is non-empty
     */
    @Override
    public boolean isValid() {
        return !answers.isEmpty();
    }

    /**
     * Displays the question prompt followed by each matching pair on its own line.
     * Each pair is shown in "LETTER NUMBER" format (e.g. A 2).
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.println(questionPrompt);
        for (String pair : answers) {
            output.println("  " + pair);
        }
    }
}
