package survey.model.question;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.model.response.MatchingResponse;
import survey.model.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * A matching question with two columns.
 * The left column items are labelled A, B, C … and the right column 1, 2, 3 …
 * The user enters a letter-number pair (e.g. "A 2") for each left item.
 */
public class Matching extends Question {

    private static final long serialVersionUID = 1L;

    private List<String> leftColumn;
    private List<String> rightColumn;

    /**
     * Constructs a Matching question.
     * Matching questions accept exactly one set of pairs (allowMultiple = false).
     *
     * @param prompt the question text / instruction
     */
    public Matching(String prompt) {
        super(prompt, false);
        this.leftColumn = new ArrayList<>();
        this.rightColumn = new ArrayList<>();
    }

    /**
     * Returns the mutable left-column item list.
     *
     * @return the list of left-column (letter-labelled) items
     */
    public List<String> getLeftColumn() {
        return leftColumn;
    }

    /**
     * Returns the mutable right-column item list.
     *
     * @return the list of right-column (number-labelled) items
     */
    public List<String> getRightColumn() {
        return rightColumn;
    }

    /**
     * Adds a matching pair to both columns.
     *
     * @param left  the item for the left column
     * @param right the item for the right column
     */
    public void addPair(String left, String right) {
        leftColumn.add(left);
        rightColumn.add(right);
    }

    /**
     * Displays the question prompt followed by both columns neatly aligned.
     * Left items are labelled A), B), C) … and right items 1), 2), 3) …
     * Each pair occupies one line with the left column padded to a fixed width.
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.println(prompt);
        int size = Math.min(leftColumn.size(), rightColumn.size());
        output.println(String.format("  %-24s %s", "Left", "Right"));
        output.println("  " + "-".repeat(40));
        for (int i = 0; i < size; i++) {
            String left = String.format("  %c) %-20s", (char) ('A' + i), leftColumn.get(i));
            String right = String.format("  %d) %s", (i + 1), rightColumn.get(i));
            output.println(left + right);
        }
    }

    /**
     * Displays the two-column layout and collects one "letter number" pair per
     * left-column item (e.g. "A 2"). Loops on bad input.
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for prompts and errors
     * @return a MatchingResponse containing one pair string per left item
     */
    @Override
    public Response take(InputHandler input, OutputHandler output) {
        display(output);
        MatchingResponse response = new MatchingResponse(prompt);
        int size = Math.min(leftColumn.size(), rightColumn.size());
        char maxLetter = (char) ('A' + size - 1);
        output.println("  Match each left item to a right item.");
        output.println("  Enter one answer per line in the format: LETTER NUMBER");
        StringBuilder example = new StringBuilder("  e.g.");
        for (int i = 0; i < size; i++) {
            example.append("\n       ").append((char) ('A' + i)).append(" ").append(i + 1);
        }
        output.println(example.toString());
        for (int i = 0; i < size; i++) {
            String leftLabel = String.valueOf((char) ('A' + i));
            while (true) {
                String ans = input.readString(
                        "  " + String.format("%-20s", leftLabel + ") " + leftColumn.get(i))
                                + "-> ")
                        .trim();
                if (isValidPair(ans, size)) {
                    response.addAnswer(ans.toUpperCase().replaceAll("\\s+", " "));
                    break;
                }
                output.printError(
                        "Enter a pair like 'A 2'. Letter A-" + maxLetter
                                + ", number 1-" + size + ".");
            }
        }
        return response;
    }

    /**
     * Interactively modifies the prompt and/or any left/right column item.
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
        int size = Math.min(leftColumn.size(), rightColumn.size());
        output.println("Current pairs:");
        for (int i = 0; i < size; i++) {
            output.println("  " + String.format("%c) %-20s", (char) ('A' + i), leftColumn.get(i))
                    + (i + 1) + ") " + rightColumn.get(i));
        }
        boolean change = input.readBoolean("  Change a pair?");
        while (change) {
            int idx = input.readIntInRange(
                    "  Pair number to change (1-" + size + "): ", 1, size) - 1;
            String nl = input.readString(
                    "  New left item " + (char) ('A' + idx) + " (press Enter to keep): ").trim();
            if (!nl.isEmpty()) {
                leftColumn.set(idx, nl);
            }
            String nr = input.readString(
                    "  New right item " + (idx + 1) + " (press Enter to keep): ").trim();
            if (!nr.isEmpty()) {
                rightColumn.set(idx, nr);
            }
            change = input.readBoolean("  Change another pair?");
        }
    }

    /**
     * Validates that a string is a well-formed "letter number" pair where the
     * letter maps to a left-column item and the number maps to a right-column item.
     *
     * @param ans  the raw input string (e.g. "A 2")
     * @param size the number of items in each column
     * @return true if the pair is valid
     */
    private boolean isValidPair(String ans, int size) {
        if (ans == null)
            return false;
        String[] parts = ans.trim().split("\\s+");
        if (parts.length != 2)
            return false;
        if (parts[0].length() != 1)
            return false;
        char letter = Character.toUpperCase(parts[0].charAt(0));
        if (letter < 'A' || letter > ('A' + size - 1))
            return false;
        try {
            int num = Integer.parseInt(parts[1]);
            return num >= 1 && num <= size;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates that the answer is a non-empty string.
     * Full pair-format validation is done inside take().
     *
     * @param answer the candidate answer
     * @return true if non-null and non-empty
     */
    @Override
    public boolean validateAnswer(String answer) {
        return answer != null && !answer.trim().isEmpty();
    }
}
