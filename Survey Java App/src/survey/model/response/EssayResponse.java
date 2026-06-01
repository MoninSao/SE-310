package survey.model.response;

import survey.io.OutputHandler;

/**
 * Response to an essay question.
 * Multiple responses are supported; each is displayed labelled A), B), C) …
 */
public class EssayResponse extends Response {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs an EssayResponse for the given question prompt.
     *
     * @param questionPrompt the text of the question being answered
     */
    public EssayResponse(String questionPrompt) {
        super(questionPrompt);
    }

    /**
     * Returns whether every stored answer is non-empty.
     *
     * @return true if at least one answer exists and none are blank
     */
    @Override
    public boolean isValid() {
        return !answers.isEmpty() && answers.stream().noneMatch(String::isEmpty);
    }

    /**
     * Displays the question prompt followed by each answer labelled A), B), C) …
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.println(questionPrompt);
        for (int i = 0; i < answers.size(); i++) {
            output.println(String.valueOf((char) ('A' + i)) + ") " + answers.get(i));
        }
    }
}
