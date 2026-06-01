package survey.model.question;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.model.response.Response;
import survey.model.response.ValidDateResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A date question that requires a valid calendar date as an answer.
 * Automatically displays the expected format hint alongside the prompt,
 * as required by the assignment spec.
 *
 * Phase 1: display() implemented. take() / modify() are stubs — Phase 3.
 */
public class ValidDate extends Question {

    private static final long serialVersionUID = 1L;

    /** The format hint shown to users below every date question. */
    public static final String DATE_FORMAT_HINT = "A date should be entered in the following format: YYYY-MM-DD";

    /** The DateTimeFormatter used for validation and parsing. */
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    /**
     * Constructs a ValidDate question.
     *
     * @param prompt        the question text
     * @param allowMultiple true if the user may supply more than one date
     */
    public ValidDate(String prompt, boolean allowMultiple) {
        super(prompt, allowMultiple);
    }

    /**
     * Displays the question prompt and the mandatory format hint.
     * The spec requires the format line to appear with every date question.
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.println(prompt);
        output.println(DATE_FORMAT_HINT);
    }

    /**
     * Prompts the user for one or more valid dates.
     * The format hint is always printed directly after the question prompt,
     * before each input attempt. Validates with DateTimeFormatter.
     * If allowMultiple is true, first asks how many dates (up to 10).
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for prompts and errors
     * @return a ValidDateResponse containing all collected date strings
     */
    @Override
    public Response take(InputHandler input, OutputHandler output) {
        ValidDateResponse response = new ValidDateResponse(prompt);
        int count = 1;
        if (allowMultiple) {
            display(output);
            count = input.readIntInRange("  How many dates? ", 1, 10);
        }
        for (int i = 0; i < count; i++) {
            display(output);
            LocalDate date = input.readDate("  Enter date: ", FORMATTER);
            response.addAnswer(date.format(FORMATTER));
        }
        return response;
    }

    /**
     * Interactively modifies the question prompt.
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
     * Validates that the answer is a parseable YYYY-MM-DD date.
     *
     * @param answer the candidate answer
     * @return true if the string is a valid date in YYYY-MM-DD format
     */
    @Override
    public boolean validateAnswer(String answer) {
        if (answer == null || answer.trim().isEmpty())
            return false;
        try {
            LocalDate.parse(answer.trim(), FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Counts occurrences of each distinct date string and prints
     * one line per distinct date in the form "YYYY-MM-DD: n".
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
            output.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
