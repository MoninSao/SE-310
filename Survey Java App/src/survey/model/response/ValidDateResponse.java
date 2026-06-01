package survey.model.response;

import survey.io.OutputHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Response to a valid date question.
 * The stored answer must be parseable as a date in YYYY-MM-DD format.
 */
public class ValidDateResponse extends Response {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    /**
     * Constructs a ValidDateResponse for the given question prompt.
     *
     * @param questionPrompt the text of the question being answered
     */
    public ValidDateResponse(String questionPrompt) {
        super(questionPrompt);
    }

    /**
     * Returns whether the stored answer is a valid YYYY-MM-DD date.
     *
     * @return true if the first answer parses as a valid date
     */
    @Override
    public boolean isValid() {
        if (answers.isEmpty())
            return false;
        try {
            LocalDate.parse(answers.get(0).trim(), FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Displays the question prompt and the date answer on one line.
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.print(questionPrompt + " -> ");
        output.println(answers.isEmpty() ? "(no answer)" : answers.get(0));
    }
}
