package survey.model.response;

import survey.io.OutputHandler;

/**
 * Response to a multiple-choice question.
 * Stores one or more selected choice letters (e.g. "A", "B").
 */
public class MultipleChoiceResponse extends Response {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a MultipleChoiceResponse for the given question prompt.
     *
     * @param questionPrompt the text of the question being answered
     */
    public MultipleChoiceResponse(String questionPrompt) {
        super(questionPrompt);
    }

    /**
     * Returns whether at least one answer has been recorded.
     *
     * @return true if the answer list is non-empty
     */
    @Override
    public boolean isValid() {
        return !answers.isEmpty();
    }

    /**
     * Displays the question prompt and all selected choices on one line.
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.print(questionPrompt + " -> ");
        output.println(String.join(", ", answers));
    }
}
