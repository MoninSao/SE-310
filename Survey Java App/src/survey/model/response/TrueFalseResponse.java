package survey.model.response;

import survey.io.OutputHandler;

/**
 * Response to a true/false question.
 * Extends MultipleChoiceResponse since TrueFalse extends MultipleChoice.
 * Valid answers are: "True" or "False" (case-insensitive t/f also accepted).
 */
public class TrueFalseResponse extends MultipleChoiceResponse {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a TrueFalseResponse for the given question prompt.
     *
     * @param questionPrompt the text of the question being answered
     */
    public TrueFalseResponse(String questionPrompt) {
        super(questionPrompt);
    }

    /**
     * Returns whether the single stored answer is a recognised true/false value.
     *
     * @return true if the answer is one of: true, false, t, f (case-insensitive)
     */
    @Override
    public boolean isValid() {
        if (answers.isEmpty())
            return false;
        String a = answers.get(0).trim().toLowerCase();
        return a.equals("true") || a.equals("false") || a.equals("t") || a.equals("f");
    }

    /**
     * Displays the question prompt and the single true/false answer.
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.print(questionPrompt + " -> ");
        output.println(answers.isEmpty() ? "(no answer)" : answers.get(0));
    }
}
