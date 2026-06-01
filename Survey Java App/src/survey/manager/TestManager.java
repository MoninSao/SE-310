package survey.manager;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.model.SurveyResponse;
import survey.model.Test;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Manages the lifecycle of tests: create, display, save, load, take, modify,
 * tabulate, and grade.
 * Mirrors SurveyManager but operates on Test objects and the tests/ directory.
 */
public class TestManager {

    /** Relative path to the directory where serialized test files are stored. */
    public static final Path TESTS_DIR = Paths.get("tests");

    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    private Test currentTest;

    private final InputHandler input;
    private final OutputHandler output;

    /**
     * Constructs a TestManager with the given I/O handlers.
     *
     * @param input  the InputHandler for reading user input
     * @param output the OutputHandler for console output
     */
    public TestManager(InputHandler input, OutputHandler output) {
        this.input = input;
        this.output = output;
    }

    // -------------------------------------------------------------------------
    // currentTest accessors
    // -------------------------------------------------------------------------

    /** @return true if a test is currently loaded */
    public boolean hasCurrentTest() {
        return currentTest != null;
    }

    /** @return the currently active Test, or null if none is loaded */
    public Test getCurrentTest() {
        return currentTest;
    }

    /** Sets the currently active test. */
    public void setCurrentTest(Test t) {
        this.currentTest = t;
    }

    // -------------------------------------------------------------------------
    // File operations
    // -------------------------------------------------------------------------

    /**
     * Lists the filenames of all .ser files found in the tests/ directory.
     *
     * @return sorted list of .ser filenames (basename only)
     */
    public List<String> listSavedTests() {
        ensureDirectoryExists(TESTS_DIR);
        try (Stream<Path> stream = Files.list(TESTS_DIR)) {
            return stream
                    .filter(p -> p.getFileName().toString().endsWith(".ser"))
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            output.printError("Could not list tests: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Saves the current test to tests/&lt;safeName&gt;.ser.
     */
    public void saveTest() {
        if (!hasCurrentTest()) {
            output.println("You must have a test loaded in order to save it.");
            return;
        }
        ensureDirectoryExists(TESTS_DIR);
        String safeName = currentTest.getName()
                .replaceAll("[^a-zA-Z0-9_\\-]", "_");
        Path path = TESTS_DIR.resolve(safeName + ".ser");
        try {
            currentTest.save(path);
            output.println("Test saved to: " + path);
        } catch (IOException e) {
            output.printError("Could not save test: " + e.getMessage());
        }
    }

    /**
     * Presents a numbered list of saved test files and loads the user's selection
     * as the current test.
     */
    public void loadTest() {
        List<String> files = listSavedTests();
        if (files.isEmpty()) {
            output.println("No saved tests found in '" + TESTS_DIR + "/'.");
            return;
        }
        output.println("Please select a file to load:");
        for (int i = 0; i < files.size(); i++) {
            output.println((i + 1) + ") " + files.get(i));
        }
        int choice = input.readIntInRange("Enter choice: ", 1, files.size());
        Path path = TESTS_DIR.resolve(files.get(choice - 1));
        try {
            currentTest = Test.load(path);
            output.println("Test '" + currentTest.getName() + "' loaded.");
        } catch (IOException | ClassNotFoundException e) {
            output.printError("Could not load test: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Test operations
    // -------------------------------------------------------------------------

    /**
     * Creates a new empty test with the given name and makes it the current test.
     *
     * @param name the name for the new test
     */
    public void createNewTest(String name) {
        currentTest = new Test(name);
        output.println("Test '" + name + "' created.");
    }

    /**
     * Displays the current test (questions only, no answers).
     */
    public void displayTest() {
        if (!hasCurrentTest()) {
            output.println("You must have a test loaded in order to display it.");
            return;
        }
        currentTest.display(output);
    }

    /**
     * Displays the current test with correct answers shown after each question.
     */
    public void displayTestWithAnswers() {
        if (!hasCurrentTest()) {
            output.println("You must have a test loaded in order to display it.");
            return;
        }
        currentTest.displayWithAnswers(output);
    }

    /**
     * Conducts the current test interactively.
     * Identical logic to SurveyManager.takeSurvey(); responses are saved to the
     * shared responses/ directory so gradeTest() can find them by surveyName.
     */
    public void takeTest() {
        if (!hasCurrentTest()) {
            output.println("You must have a test loaded in order to take it.");
            return;
        }
        List<Question> questions = currentTest.getQuestions();
        if (questions.isEmpty()) {
            output.println("This test has no questions.");
            return;
        }
        String respondentId = input.readString("Enter your name or ID: ").trim();
        if (respondentId.isEmpty()) {
            output.printError("Respondent ID cannot be empty.");
            return;
        }
        SurveyResponse sr = new SurveyResponse(currentTest.getName(), respondentId);
        for (Question q : questions) {
            Response r = q.take(input, output);
            sr.addResponse(r);
        }
        ensureDirectoryExists(SurveyManager.RESPONSES_DIR);
        String filename = buildResponseFilename(respondentId, currentTest.getName());
        Path path = SurveyManager.RESPONSES_DIR.resolve(filename);
        try {
            sr.save(path);
            output.println("Response saved to: " + path);
        } catch (IOException e) {
            output.printError("Could not save response: " + e.getMessage());
        }
    }

    /**
     * Modifies a question in the current test.
     * After calling the question's modify() method, prompts the user to update the
     * correct answer for any non-essay question.
     */
    public void modifyTest() {
        if (!hasCurrentTest()) {
            output.println("You must have a test loaded in order to modify it.");
            return;
        }
        List<Question> questions = currentTest.getQuestions();
        if (questions.isEmpty()) {
            output.println("This test has no questions to modify.");
            return;
        }
        output.println("=== Questions in: " + currentTest.getName() + " ===");
        for (int i = 0; i < questions.size(); i++) {
            output.print((i + 1) + ") ");
            questions.get(i).display(output);
        }
        int idx = input.readIntInRange(
                "Enter question number to modify: ", 1, questions.size()) - 1;
        Question q = questions.get(idx);
        q.modify(input, output);
        if (q.getClass() != Essay.class) {
            if (input.readBoolean("Update the correct answer for this question?")) {
                String newAnswer = promptCorrectAnswer(q);
                currentTest.setCorrectAnswer(idx, newAnswer);
                output.println("Correct answer updated.");
            }
        }
        output.println("Question updated.");
    }

    /**
     * Tabulates all responses for the current test by scanning responses/ for
     * entries whose surveyName field matches the test name.
     */
    public void tabulateTest() {
        if (!hasCurrentTest()) {
            output.println("You must have a test loaded in order to tabulate it.");
            return;
        }
        List<SurveyResponse> allResponses = loadResponsesForSurvey(currentTest.getName());
        if (allResponses.isEmpty()) {
            output.println("No responses found for test '" + currentTest.getName() + "'.");
            return;
        }
        List<Question> questions = currentTest.getQuestions();
        output.println("=== Tabulation: " + currentTest.getName()
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

    /**
     * Prompts the user to select a saved test and a matching response, then grades
     * the response and prints the result.
     */
    public void gradeTest() {
        // Step 1: pick a test to grade
        List<String> testFiles = listSavedTests();
        if (testFiles.isEmpty()) {
            output.println("No saved tests found in '" + TESTS_DIR + "/'.");
            return;
        }
        output.println("Select a test to grade:");
        for (int i = 0; i < testFiles.size(); i++) {
            output.println((i + 1) + ") " + testFiles.get(i));
        }
        int testChoice = input.readIntInRange("Enter choice: ", 1, testFiles.size());
        Path testPath = TESTS_DIR.resolve(testFiles.get(testChoice - 1));
        Test test;
        try {
            test = Test.load(testPath);
        } catch (IOException | ClassNotFoundException e) {
            output.printError("Could not load test: " + e.getMessage());
            return;
        }

        // Step 2: pick a response for that test
        List<SurveyResponse> responses = loadResponsesForSurvey(test.getName());
        if (responses.isEmpty()) {
            output.println("No responses found for test '" + test.getName() + "'.");
            return;
        }
        output.println("Select a response to grade:");
        for (int i = 0; i < responses.size(); i++) {
            output.println((i + 1) + ") " + responses.get(i).getRespondentId());
        }
        int respChoice = input.readIntInRange("Enter choice: ", 1, responses.size());
        SurveyResponse sr = responses.get(respChoice - 1);

        // Step 3: grade and display
        double score = test.grade(sr);
        int essayCount = test.getEssayCount();
        int totalQuestions = test.getQuestions().size();
        int gradeableCount = test.getGradeableCount();
        int roundedScore = (int) Math.round(score);

        String gradeString;
        if (essayCount == 0) {
            gradeString = "You received a " + roundedScore + " on the test.";
        } else {
            int autoPoints = (int) Math.round(gradeableCount * (100.0 / totalQuestions));
            gradeString = "You received a " + roundedScore + " on the test."
                    + " The test was worth 100 points, but only " + autoPoints
                    + " of those points could be auto graded because there was "
                    + essayCount + " essay question(s).";
        }
        output.println(gradeString);
    }

    // -------------------------------------------------------------------------
    // Question creation (adds to test + records correct answer)
    // -------------------------------------------------------------------------

    /**
     * Prompts for and adds a True/False question, then records the correct answer.
     */
    public void addTrueFalseQuestion() {
        String prompt = input.readString("Enter the T/F question prompt: ").trim();
        if (prompt.isEmpty()) {
            output.printError("Prompt cannot be empty.");
            return;
        }
        TrueFalse q = new TrueFalse(prompt);
        currentTest.addQuestion(q);
        currentTest.addCorrectAnswer(promptCorrectAnswer(q));
        output.println("T/F question added.");
    }

    /**
     * Prompts for and adds a Multiple Choice question, then records the correct
     * answer.
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
        currentTest.addQuestion(q);
        currentTest.addCorrectAnswer(promptCorrectAnswer(q));
        output.println("Multiple-choice question added.");
    }

    /**
     * Prompts for and adds a Short Answer question, then records the correct
     * answer.
     */
    public void addShortAnswerQuestion() {
        String prompt = input.readString("Enter the short answer question prompt: ").trim();
        if (prompt.isEmpty()) {
            output.printError("Prompt cannot be empty.");
            return;
        }
        int maxChars = input.readIntInRange("Maximum characters allowed per response: ", 1, 500);
        boolean allowMultiple = input.readBoolean("Allow multiple responses?");
        ShortAnswer q = new ShortAnswer(prompt, allowMultiple, maxChars);
        currentTest.addQuestion(q);
        currentTest.addCorrectAnswer(promptCorrectAnswer(q));
        output.println("Short answer question added.");
    }

    /**
     * Prompts for and adds an Essay question.
     * Essay questions are not auto-graded; null is stored as the correct answer.
     */
    public void addEssayQuestion() {
        String prompt = input.readString("Enter the essay question prompt: ").trim();
        if (prompt.isEmpty()) {
            output.printError("Prompt cannot be empty.");
            return;
        }
        boolean allowMultiple = input.readBoolean("Allow multiple responses?");
        Essay q = new Essay(prompt, allowMultiple);
        currentTest.addQuestion(q);
        currentTest.addCorrectAnswer(null);
        output.println("Essay question added.");
    }

    /**
     * Prompts for and adds a Valid Date question, then records the correct answer.
     */
    public void addValidDateQuestion() {
        String prompt = input.readString("Enter the date question prompt: ").trim();
        if (prompt.isEmpty()) {
            output.printError("Prompt cannot be empty.");
            return;
        }
        boolean allowMultiple = input.readBoolean("Allow multiple dates?");
        ValidDate q = new ValidDate(prompt, allowMultiple);
        currentTest.addQuestion(q);
        currentTest.addCorrectAnswer(promptCorrectAnswer(q));
        output.println("Date question added.");
    }

    /**
     * Prompts for and adds a Matching question, then records the correct answer.
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
        currentTest.addQuestion(q);
        currentTest.addCorrectAnswer(promptCorrectAnswer(q));
        output.println("Matching question added.");
    }

    // -------------------------------------------------------------------------
    // promptCorrectAnswer helper
    // -------------------------------------------------------------------------

    /**
     * Interactively collects the correct answer for the given question type.
     *
     * <ul>
     * <li>TrueFalse — 1/2 menu, returns "True" or "False"</li>
     * <li>MultipleChoice — shows choices, accepts valid letter, returns
     * uppercase</li>
     * <li>ShortAnswer — reads the expected answer string</li>
     * <li>ValidDate — reads a date, returns YYYY-MM-DD string</li>
     * <li>Matching — reads one LETTER NUMBER pair per left-column item, joins
     * with "\n"</li>
     * <li>Essay — returns null (not auto-graded)</li>
     * </ul>
     *
     * @param q the question whose correct answer is being set
     * @return the correct answer string, or null for essay questions
     */
    private String promptCorrectAnswer(Question q) {
        if (q instanceof TrueFalse) {
            output.println("Select the correct answer:");
            output.println("  1) True");
            output.println("  2) False");
            int pick = input.readIntInRange("Enter choice: ", 1, 2);
            return pick == 1 ? "True" : "False";

        } else if (q instanceof MultipleChoice) {
            MultipleChoice mc = (MultipleChoice) q;
            List<String> choices = mc.getChoices();
            output.println("Choices:");
            for (int i = 0; i < choices.size(); i++) {
                output.println("  " + (char) ('A' + i) + ") " + choices.get(i));
            }
            char maxLetter = (char) ('A' + choices.size() - 1);
            while (true) {
                String ans = input.readString(
                        "Enter the correct letter (A-" + maxLetter + "): ")
                        .trim().toUpperCase();
                if (ans.length() == 1
                        && ans.charAt(0) >= 'A'
                        && ans.charAt(0) <= maxLetter) {
                    return ans;
                }
                output.printError("Please enter a letter between A and " + maxLetter + ".");
            }

        } else if (q instanceof ShortAnswer) {
            return input.readString("Enter the expected answer: ").trim();

        } else if (q instanceof ValidDate) {
            LocalDate date = input.readDate("Enter the correct date (YYYY-MM-DD): ", DATE_FMT);
            return date.format(DATE_FMT);

        } else if (q instanceof Matching) {
            Matching mq = (Matching) q;
            int size = mq.getLeftColumn().size();
            char maxLetter = (char) ('A' + size - 1);
            output.println("Enter the correct matching pairs (format: LETTER NUMBER):");
            List<String> pairs = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                String leftLabel = String.valueOf((char) ('A' + i));
                while (true) {
                    String ans = input.readString(
                            "  " + leftLabel + ") " + mq.getLeftColumn().get(i) + " -> ")
                            .trim();
                    if (mq.isValidPair(ans, size)) {
                        pairs.add(ans.toUpperCase().replaceAll("\\s+", " "));
                        break;
                    }
                    output.printError("Enter a pair like 'A 2'. Letter A-" + maxLetter
                            + ", number 1-" + size + ".");
                }
            }
            return String.join("\n", pairs);
        }

        // Essay — no correct answer
        return null;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Scans RESPONSES_DIR for all *.ser files and returns those whose stored
     * surveyName field matches the given name.
     *
     * @param surveyName the name to filter by
     * @return list of matching SurveyResponse objects
     */
    public List<SurveyResponse> loadResponsesForSurvey(String surveyName) {
        List<SurveyResponse> result = new ArrayList<>();
        if (!Files.exists(SurveyManager.RESPONSES_DIR)) {
            return result;
        }
        try (Stream<Path> stream = Files.list(SurveyManager.RESPONSES_DIR)) {
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
     *
     * @param respondentId the name or ID of the respondent
     * @param surveyName   the name of the survey/test
     * @return a safe filename string (without directory prefix)
     */
    public String buildResponseFilename(String respondentId, String surveyName) {
        String safeId = respondentId.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        String safeName = surveyName.replaceAll("[^a-zA-Z0-9_\\-]", "_");
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        return safeId + "_" + safeName + "_" + timestamp + ".ser";
    }
}
