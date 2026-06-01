package survey.model.question;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.model.response.Response;

import java.io.Serializable;
import java.util.List;

/**
 * Abstract base class for all survey question types.
 * Each subclass must implement display, take, modify, and validateAnswer.
 * Implements Serializable so questions survive save/load round-trips.
 */
public abstract class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The question text shown to the user. */
    protected String prompt;

    /**
     * Whether this question accepts more than one response.
     * When true, take() will ask the user "how many responses?" and loop.
     */
    protected boolean allowMultiple;

    /**
     * Constructs a Question with the given prompt and response-count policy.
     *
     * @param prompt        the question text
     * @param allowMultiple true if multiple responses are allowed
     */
    public Question(String prompt, boolean allowMultiple) {
        this.prompt = prompt;
        this.allowMultiple = allowMultiple;
    }

    /** @return the question prompt */
    public String getPrompt() {
        return prompt;
    }

    /** @param prompt the new question text */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /** @return true if multiple responses are allowed */
    public boolean isAllowMultiple() {
        return allowMultiple;
    }

    /** @param allowMultiple the new multi-response policy */
    public void setAllowMultiple(boolean allowMultiple) {
        this.allowMultiple = allowMultiple;
    }

    /**
     * Displays this question to the console.
     *
     * @param output the OutputHandler to write to
     */
    public abstract void display(OutputHandler output);

    /**
     * Prompts the user to answer this question and returns their Response.
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for prompts and errors
     * @return a Response containing the user's answer(s)
     */
    public abstract Response take(InputHandler input, OutputHandler output);

    /**
     * Interactively modifies this question's fields.
     * Each subclass defines which fields can be changed and how.
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for prompts and errors
     */
    public abstract void modify(InputHandler input, OutputHandler output);

    /**
     * Validates a single candidate answer string against this question's rules.
     *
     * @param answer the candidate answer
     * @return true if the answer is acceptable
     */
    public abstract boolean validateAnswer(String answer);

    /**
     * Aggregates all collected responses for this question and prints a
     * tabulation summary to the given output.
     *
     * @param responses the per-question Response objects collected across all
     *                  survey takers
     * @param output    the OutputHandler to write results to
     */
    public abstract void tabulate(List<Response> responses, OutputHandler output);
}
