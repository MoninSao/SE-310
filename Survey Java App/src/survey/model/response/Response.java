package survey.model.response;

import survey.io.OutputHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all survey responses.
 * Holds the original question prompt and a list of answer strings.
 * Subclasses define what "valid" means and how to display themselves.
 * Implements Serializable so SurveyResponse objects can be persisted.
 */
public abstract class Response implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The prompt text of the question this response belongs to. */
    protected String questionPrompt;

    /**
     * The user's answers. Single-response questions store exactly one entry;
     * multi-response questions store N entries labelled A, B, C … at display time.
     */
    protected List<String> answers;

    /**
     * Constructs a Response for the given question prompt.
     *
     * @param questionPrompt the text of the question being answered
     */
    public Response(String questionPrompt) {
        this.questionPrompt = questionPrompt;
        this.answers = new ArrayList<>();
    }

    /** @return the question prompt this response is associated with */
    public String getQuestionPrompt() {
        return questionPrompt;
    }

    /** @return the mutable list of answers */
    public List<String> getAnswers() {
        return answers;
    }

    /**
     * Appends an answer string to this response.
     *
     * @param answer the answer text to add
     */
    public void addAnswer(String answer) {
        answers.add(answer);
    }

    /**
     * Returns whether this response is considered valid.
     * Subclasses define their own validation rules.
     *
     * @return true if the response data is valid
     */
    public abstract boolean isValid();

    /**
     * Displays this response to the console via the OutputHandler.
     *
     * @param output the OutputHandler to write to
     */
    public abstract void display(OutputHandler output);
}
