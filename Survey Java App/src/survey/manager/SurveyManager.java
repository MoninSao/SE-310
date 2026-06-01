package survey.manager;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.model.Survey;
import survey.model.SurveyResponse;
import survey.model.question.Essay;
import survey.model.question.Matching;
import survey.model.question.MultipleChoice;
import survey.model.question.Question;
import survey.model.question.ShortAnswer;
import survey.model.question.TrueFalse;
import survey.model.question.ValidDate;
import survey.model.response.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages the lifecycle of surveys: create, display, save, load, take, and
 * modify.
 * Holds the "currently active" survey pointer that the rest of the menu
 * operates on.
 * Uses relative paths for all file I/O so the project works on any machine.
 */
public class SurveyManager {

    /** Relative path to the directory where serialized survey files are stored. */
    public static final Path SURVEYS_DIR = Paths.get("surveys");

    /**
     * Relative path to the directory where serialized response files are stored.
     */
    public static final Path RESPONSES_DIR = Paths.get("responses");

    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private List<Survey> surveys;
    private Survey currentSurvey;

    private final InputHandler input;
    private final OutputHandler output;

    /**
     * Constructs a SurveyManager with the given I/O handlers.
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for console output
     */
    public SurveyManager(InputHandler input, OutputHandler output) {
        this.surveys = new ArrayList<>();
        this.input = input;
        this.output = output;
    }

    // -------------------------------------------------------------------------
    // currentSurvey accessors
    // -------------------------------------------------------------------------

    /** @return true if a survey is currently loaded */
    public boolean hasCurrentSurvey() {
        return currentSurvey != null;
    }

    /** @return the currently active Survey, or null if none is loaded */
    public Survey getCurrentSurvey() {
        return currentSurvey;
    }

    /**
     * Sets the currently active survey.
     *
     * @param survey the Survey to make active
     */
    public void setCurrentSurvey(Survey survey) {
        this.currentSurvey = survey;
    }

    // -------------------------------------------------------------------------
    // File operations
    // -------------------------------------------------------------------------

    /**
     * Lists the filenames of all .ser files found in the surveys/ directory.
     * Returns an empty list (not a crash) if the directory does not exist.
     *
     * @return sorted list of .ser filenames (basename only, not full path)
     */
    public List<String> listSavedSurveys() {
        ensureDirectoryExists(SURVEYS_DIR);
        try (Stream<Path> stream = Files.list(SURVEYS_DIR)) {
            return stream
                    .filter(p -> p.getFileName().toString().endsWith(".ser"))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            output.printError("Could not list surveys: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves the current survey to surveys/<name>.ser.
     * Non-alphanumeric characters in the survey name are replaced with underscores
     * to produce a safe, portable filename.
     * Displays a spec-required error message if no survey is loaded.
     */
    public void saveSurvey() {
        if (!hasCurrentSurvey()) {
            output.println("You must have a survey loaded in order to save it.");
            return;
        }
        ensureDirectoryExists(SURVEYS_DIR);
        String safeName = currentSurvey.getName()
                .replaceAll("[^a-zA-Z0-9_\\-]", "_");
        Path path = SURVEYS_DIR.resolve(safeName + ".ser");
        try {
            currentSurvey.save(path);
            output.println("Survey saved to: " + path);
        } catch (IOException e) {
            output.printError("Could not save survey: " + e.getMessage());
        }
    }

    /**
     * Presents a numbered list of saved survey files and loads the user's selection
     * as the current survey.
     */
    public void loadSurvey() {
        List<String> files = listSavedSurveys();
        if (files.isEmpty()) {
            output.println("No saved surveys found in '" + SURVEYS_DIR + "/'.");
            return;
        }
        output.println("Please select a file to load:");
        for (int i = 0; i < files.size(); i++) {
            output.println((i + 1) + ") " + files.get(i));
        }
        int choice = input.readIntInRange("Enter choice: ", 1, files.size());
        Path path = SURVEYS_DIR.resolve(files.get(choice - 1));
        try {
            currentSurvey = Survey.load(path);
            // Keep the in-memory pool consistent
            surveys.removeIf(s -> s.getName().equals(currentSurvey.getName()));
            surveys.add(currentSurvey);
            output.println("Survey '" + currentSurvey.getName() + "' loaded.");
        } catch (IOException | ClassNotFoundException e) {
            output.printError("Could not load survey: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Survey operations (delegates to currentSurvey)
    // -------------------------------------------------------------------------

    /**
     * Creates a new empty survey with the given name and makes it the current
     * survey.
     * Called by SurveyDriver after prompting the user for a name.
     *
     * @param name the name for the new survey
     */
    public void createNewSurvey(String name) {
        currentSurvey = new Survey(name);
        surveys.add(currentSurvey);
        output.println("Survey '" + name + "' created.");
    }

    /**
     * Displays the current survey to the console.
     * Displays a spec-required error message if no survey is loaded.
     */
    public void displaySurvey() {
        if (!hasCurrentSurvey()) {
            output.println("You must have a survey loaded in order to display it.");
            return;
        }
        currentSurvey.display(output);
    }

    /**
     * Conducts the current survey interactively.
     * Prompts the respondent for their ID, loops through every question calling
     * take(), collects all responses into a SurveyResponse, and serialises it
     * to a timestamped file in responses/.
     * Displays a spec-required error message if no survey is loaded.
     */
    public void takeSurvey() {
        if (!hasCurrentSurvey()) {
            output.println("You must have a survey loaded in order to take it.");
            return;
        }
        List<Question> questions = currentSurvey.getQuestions();
        if (questions.isEmpty()) {
            output.println("This survey has no questions.");
            return;
        }
        String respondentId = input.readString("Enter your name or ID: ").trim();
        if (respondentId.isEmpty()) {
            output.printError("Respondent ID cannot be empty.");
            return;
        }
        SurveyResponse sr = new SurveyResponse(currentSurvey.getName(), respondentId);
        for (Question q : questions) {
            Response r = q.take(input, output);
            sr.addResponse(r);
        }
        ensureDirectoryExists(RESPONSES_DIR);
        String filename = buildResponseFilename(respondentId, currentSurvey.getName());
        Path path = RESPONSES_DIR.resolve(filename);
        try {
            sr.save(path);
            output.println("Response saved to: " + path);
        } catch (IOException e) {
            output.printError("Could not save response: " + e.getMessage());
        }
    }

    /**
     * Modifies a question in the current survey.
     * Lists all questions, prompts the user for a number, and delegates to
     * the selected question's modify() method.
     * Displays a spec-required error message if no survey is loaded.
     */
    public void modifySurvey() {
        if (!hasCurrentSurvey()) {
            output.println("You must have a survey loaded in order to modify it.");
            return;
        }
        List<Question> questions = currentSurvey.getQuestions();
        if (questions.isEmpty()) {
            output.println("This survey has no questions to modify.");
            return;
        }
        output.println("=== Questions in: " + currentSurvey.getName() + " ===");
        for (int i = 0; i < questions.size(); i++) {
            output.print((i + 1) + ") ");
            questions.get(i).display(output);
        }
        int idx = input.readIntInRange(
                "Enter question number to modify: ", 1, questions.size()) - 1;
        questions.get(idx).modify(input, output);
        output.println("Question updated.");
    }

    // -------------------------------------------------------------------------
    // Question creation stubs (wired in Phase 2 / Phase 3)
    // -------------------------------------------------------------------------

    /**
     * Prompts the user to create a True/False question and adds it to the current
     * survey.
     */
    public void addTrueFalseQuestion() {
        String prompt = input.readString("Enter the T/F question prompt: ").trim();
        if (prompt.isEmpty()) {
            output.printError("Prompt cannot be empty.");
            return;
        }
        currentSurvey.addQuestion(new TrueFalse(prompt));
        output.println("T/F question added.");
    }

    /**
     * Prompts the user to create a Multiple Choice question (with choices) and
     * adds it to the current survey.
     */
    public void addMultipleChoiceQuestion() {
        String prompt = input.readString("Enter the multiple-choice question prompt: ").trim();
        if (prompt.isEmpty()) {
            output.printError("Prompt cannot be empty.");
            return;
        }
        int numChoices = input.readIntInRange("How many choices? ", 2, 26);
        MultipleChoice q = new MultipleChoice(prompt, false);
        for (int i = 0; i < numChoices; i++) {
            String choice = input.readString("  Choice " + (char) ('A' + i) + ": ").trim();
            q.addChoice(choice.isEmpty() ? "(empty)" : choice);
        }
        q.setAllowMultiple(input.readBoolean("Allow multiple selections?"));
        currentSurvey.addQuestion(q);
        output.println("Multiple-choice question added.");
    }

    /**
     * Prompts the user to create a Short Answer question and adds it to the current
     * survey.
     */
    public void addShortAnswerQuestion() {
        String prompt = input.readString("Enter the short answer question prompt: ").trim();
        if (prompt.isEmpty()) {
            output.printError("Prompt cannot be empty.");
            return;
        }
        int maxChars = input.readIntInRange("Maximum characters allowed per response: ", 1, 500);
        boolean allowMultiple = input.readBoolean("Allow multiple responses?");
        currentSurvey.addQuestion(new ShortAnswer(prompt, allowMultiple, maxChars));
        output.println("Short answer question added.");
    }

    /**
     * Prompts the user to create an Essay question and adds it to the current
     * survey.
     */
    public void addEssayQuestion() {
        String prompt = input.readString("Enter the essay question prompt: ").trim();
        if (prompt.isEmpty()) {
            output.printError("Prompt cannot be empty.");
            return;
        }
        boolean allowMultiple = input.readBoolean("Allow multiple responses?");
        currentSurvey.addQuestion(new Essay(prompt, allowMultiple));
        output.println("Essay question added.");
    }

    /**
     * Prompts the user to create a Valid Date question and adds it to the current
     * survey.
     */
    public void addValidDateQuestion() {
        String prompt = input.readString("Enter the date question prompt: ").trim();
        if (prompt.isEmpty()) {
            output.printError("Prompt cannot be empty.");
            return;
        }
        boolean allowMultiple = input.readBoolean("Allow multiple dates?");
        currentSurvey.addQuestion(new ValidDate(prompt, allowMultiple));
        output.println("Date question added.");
    }

    /**
     * Prompts the user to create a Matching question and adds it to the current
     * survey.
     */
    public void addMatchingQuestion() {
        String prompt = input.readString("Enter the matching question prompt/instruction: ").trim();
        if (prompt.isEmpty()) {
            output.printError("Prompt cannot be empty.");
            return;
        }
        int numPairs = input.readIntInRange("How many matching pairs? ", 1, 26);
        Matching q = new Matching(prompt);
        for (int i = 0; i < numPairs; i++) {
            String left = input.readString(
                    "  Left item " + (char) ('A' + i) + ": ").trim();
            String right = input.readString(
                    "  Right item " + (i + 1) + ": ").trim();
            q.addPair(left.isEmpty() ? "(empty)" : left,
                    right.isEmpty() ? "(empty)" : right);
        }
        currentSurvey.addQuestion(q);
        output.println("Matching question added.");
    }

    // -------------------------------------------------------------------------
    // Tabulation
    // -------------------------------------------------------------------------

    /**
     * Scans RESPONSES_DIR for all *.ser files, deserializes each as a
     * SurveyResponse, and returns only those whose stored survey name matches
     * the given name. No filename parsing is performed — the match uses the
     * surveyName field stored inside the object.
     *
     * @param surveyName the name to filter by
     * @return list of matching SurveyResponse objects
     */
    public List<SurveyResponse> loadResponsesForSurvey(String surveyName) {
        List<SurveyResponse> result = new ArrayList<>();
        if (!Files.exists(RESPONSES_DIR)) {
            return result;
        }
        try (Stream<Path> stream = Files.list(RESPONSES_DIR)) {
            List<Path> files = stream
                    .filter(p -> p.getFileName().toString().endsWith(".ser"))
                    .collect(Collectors.toList());
            for (Path file : files) {
                try {
                    SurveyResponse sr = SurveyResponse.load(file);
                    if (sr.getSurveyName().equals(surveyName)) {
                        result.add(sr);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    output.printError("Skipping unreadable response file '"
                            + file.getFileName() + "': " + e.getMessage());
                }
            }
        } catch (IOException e) {
            output.printError("Could not list responses directory: " + e.getMessage());
        }
        return result;
    }

    /**
     * Tabulates all collected responses for the current survey.
     * For each question at index i, gathers the per-question Response from
     * every taker (by position) and delegates to question.tabulate().
     * Displays a spec-required error message if no survey is loaded.
     */
    public void tabulateSurvey() {
        if (!hasCurrentSurvey()) {
            output.println("You must have a survey loaded in order to tabulate it.");
            return;
        }
        List<SurveyResponse> allResponses = loadResponsesForSurvey(currentSurvey.getName());
        if (allResponses.isEmpty()) {
            output.println("No responses found for survey '" + currentSurvey.getName() + "'.");
            return;
        }
        List<Question> questions = currentSurvey.getQuestions();
        output.println("=== Tabulation: " + currentSurvey.getName()
                + " (" + allResponses.size() + " response(s)) ===");
        for (int i = 0; i < questions.size(); i++) {
            List<Response> forThisQ = new ArrayList<>();
            for (SurveyResponse sr : allResponses) {
                List<Response> responses = sr.getResponses();
                if (i < responses.size()) {
                    forThisQ.add(responses.get(i));
                }
            }
            questions.get(i).tabulate(forThisQ, output);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Creates the given directory (and any parents) if it does not already exist.
     *
     * @param dir the directory path to ensure exists
     */
    private void ensureDirectoryExists(Path dir) {
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                output.printError("Could not create directory '" + dir + "': " + e.getMessage());
            }
        }
    }

    /**
     * Builds a safe, timestamped filename for a response file.
     * Format: respondentId_surveyName_timestamp.ser
     * All non-alphanumeric characters are replaced with underscores.
     *
     * @param respondentId the name or ID of the respondent
     * @param surveyName   the name of the survey
     * @return a safe filename string (without directory prefix)
     */
    public String buildResponseFilename(String respondentId, String surveyName) {
        String safeId = respondentId.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        String safeName = surveyName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        return safeId + "_" + safeName + "_" + timestamp + ".ser";
    }
}
