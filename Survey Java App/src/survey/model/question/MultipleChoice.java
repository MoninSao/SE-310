package survey.model.question;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.model.response.MultipleChoiceResponse;
import survey.model.response.Response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A multiple-choice question with a list of labelled choices (A, B, C …).
 * Supports single or multiple responses.
 * TrueFalse extends this class with fixed "True"/"False" choices.
 *
 * Phase 1: display() is fully implemented.
 * take() and modify() are stubs — implemented in Phase 2.
 */
public class MultipleChoice extends Question {

    private static final long serialVersionUID = 1L;

    /** The list of choice texts. Index 0 = "A)", index 1 = "B)", etc. */
    protected List<String> choices;

    /**
     * Constructs a MultipleChoice question.
     *
     * @param prompt        the question text
     * @param allowMultiple true if the user may select more than one choice
     */
    public MultipleChoice(String prompt, boolean allowMultiple) {
        super(prompt, allowMultiple);
        this.choices = new ArrayList<>();
    }

    /** @return the mutable list of choice texts */
    public List<String> getChoices() {
        return choices;
    }

    /**
     * Appends a new choice to the list.
     *
     * @param choice the choice text to add
     */
    public void addChoice(String choice) {
        choices.add(choice);
    }

    /**
     * Replaces an existing choice at the specified index.
     *
     * @param index the 0-based index of the choice to replace
     * @param value the new choice text
     */
    public void setChoice(int index, String value) {
        choices.set(index, value);
    }

    /**
     * Displays the question prompt followed by choices on one line as "A) … B) … C)
     * …".
     *
     * @param output the OutputHandler to write to
     */
    @Override
    public void display(OutputHandler output) {
        output.println(prompt);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < choices.size(); i++) {
            if (i > 0)
                sb.append("   ");
            sb.append((char) ('A' + i)).append(") ").append(choices.get(i));
        }
        output.println(sb.toString());
    }

    /**
     * Prompts the user to select one or more choices and returns a
     * MultipleChoiceResponse.
     * When allowMultiple is true the user is first asked how many choices they
     * want to enter, then each choice is validated against the available letters.
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for prompts and errors
     * @return a MultipleChoiceResponse containing the selected letter(s)
     */
    @Override
    public Response take(InputHandler input, OutputHandler output) {
        display(output);
        MultipleChoiceResponse response = new MultipleChoiceResponse(prompt);
        char maxLetter = (char) ('A' + choices.size() - 1);
        int count = 1;
        if (allowMultiple) {
            count = input.readIntInRange("  How many choices to select? ", 1, choices.size());
            output.println("  Please give " + count + " choices:");
        }
        for (int i = 0; i < count; i++) {
            while (true) {
                String ans = input.readString("  Enter choice letter (A-" + maxLetter + "): ").trim().toUpperCase();
                if (validateAnswer(ans)) {
                    response.addAnswer(String.valueOf(ans.charAt(0)));
                    break;
                }
                output.printError("Invalid choice. Enter a letter between A and " + maxLetter + ".");
            }
        }
        return response;
    }

    /**
     * Interactively modifies the question prompt and/or individual choice texts,
     * matching the spec's "Do you wish to modify the prompt?" / "Do you wish to
     * modify choices?" / letter-based choice selection flow.
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for prompts and errors
     */
    @Override
    public void modify(InputHandler input, OutputHandler output) {
        output.println("Current prompt: " + prompt);
        boolean modPrompt = input.readBoolean("  Do you wish to modify the prompt?");
        if (modPrompt) {
            String np = input.readString("  Enter a new prompt: ").trim();
            if (!np.isEmpty()) {
                setPrompt(np);
            }
        }
        boolean changeChoices = input.readBoolean("  Do you wish to modify choices?");
        if (changeChoices) {
            char maxLetter = (char) ('A' + choices.size() - 1);
            boolean another = true;
            while (another) {
                output.println("  Which choice do you want to modify?");
                StringBuilder sb = new StringBuilder("  ");
                for (int i = 0; i < choices.size(); i++) {
                    if (i > 0)
                        sb.append("   ");
                    sb.append((char) ('A' + i)).append(") ").append(choices.get(i));
                }
                output.println(sb.toString());
                while (true) {
                    String letterStr = input.readString(
                            "  Enter choice letter (A-" + maxLetter + "): ")
                            .trim().toUpperCase();
                    if (letterStr.length() == 1) {
                        char c = letterStr.charAt(0);
                        if (c >= 'A' && c <= maxLetter) {
                            int idx = c - 'A';
                            String newText = input.readString(
                                    "  New value for choice " + c + ": ").trim();
                            if (!newText.isEmpty()) {
                                setChoice(idx, newText);
                            }
                            break;
                        }
                    }
                    output.printError("Enter a letter between A and " + maxLetter + ".");
                }
                another = input.readBoolean("  Change another choice?");
            }
        }
    }

    /**
     * Validates that the answer is a letter corresponding to one of the choices.
     *
     * @param answer the candidate answer (e.g. "A", "b")
     * @return true if the letter maps to an existing choice
     */
    @Override
    public boolean validateAnswer(String answer) {
        if (answer == null || answer.isEmpty())
            return false;
        char c = Character.toUpperCase(answer.trim().charAt(0));
        return c >= 'A' && c < (char) ('A' + choices.size());
    }

    /**
     * Counts how many times each choice letter appears across all responses
     * and prints one line per letter in the form "A: n".
     *
     * @param responses the collected responses for this question
     * @param output    the OutputHandler to write to
     */
    @Override
    public void tabulate(List<Response> responses, OutputHandler output) {
        LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < choices.size(); i++) {
            counts.put(String.valueOf((char) ('A' + i)), 0);
        }
        for (Response r : responses) {
            for (String ans : r.getAnswers()) {
                String key = ans.trim().toUpperCase();
                if (counts.containsKey(key)) {
                    counts.put(key, counts.get(key) + 1);
                }
            }
        }
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            output.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
