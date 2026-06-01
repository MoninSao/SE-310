package survey.model.response;

import survey.io.OutputHandler;

/**
 * Response to a short answer question.
 * Extends EssayResponse since ShortAnswer extends Essay.
 * Typically only one response is collected, so display is condensed to one
 * line.
 */
public class ShortAnswerResponse extends EssayResponse {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a ShortAnswerResponse for the given question prompt.
     *
     * @param questionPrompt the text of the question being answered
     */
    public ShortAnswerResponse(String questionPrompt) {
        super(questionPrompt);
    }

    /**
     * Displays the question prompt and the first answer on one line.
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.print(questionPrompt + " -> ");
        output.println(answers.isEmpty() ? "(no answer)" : answers.get(0));
    }
}
