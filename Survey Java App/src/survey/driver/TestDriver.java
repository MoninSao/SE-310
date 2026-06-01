package survey.driver;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.manager.TestManager;

import java.util.Arrays;
import java.util.List;

/**
 * Top-level controller for the test subsystem.
 * Owns the TestManager reference and drives the 10-option Test Menu.
 *
 * Menu options: Create / Display w/o answers / Display w/ answers / Load /
 * Save / Take / Modify / Tabulate / Grade / Return
 */
public class TestDriver {

    private final InputHandler input;
    private final OutputHandler output;
    private final TestManager manager;

    /**
     * Constructs a TestDriver with injected collaborators (used by MainDriver).
     */
    public TestDriver(InputHandler input, OutputHandler output, TestManager manager) {
        this.input = input;
        this.output = output;
        this.manager = manager;
    }

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    /**
     * Starts the test subsystem and loops on the Test Menu until the user
     * chooses Return.
     */
    public void run() {
        output.println("=========================================");
        output.println("           Test System  v1.0             ");
        output.println("=========================================");

        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = input.readIntInRange("Enter choice: ", 1, 10);
            switch (choice) {
                case 1:
                    showCreateMenu();
                    break;
                case 2:
                    manager.displayTest();
                    break;
                case 3:
                    manager.displayTestWithAnswers();
                    break;
                case 4:
                    manager.loadTest();
                    break;
                case 5:
                    manager.saveTest();
                    break;
                case 6:
                    manager.takeTest();
                    break;
                case 7:
                    manager.modifyTest();
                    break;
                case 8:
                    manager.tabulateTest();
                    break;
                case 9:
                    manager.gradeTest();
                    break;
                case 10:
                    running = false;
                    break;
                default:
                    break;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Test Menu display
    // -------------------------------------------------------------------------

    private void showMainMenu() {
        List<String> options = Arrays.asList(
                "Create a new Test",
                "Display the current Test (without answers)",
                "Display the current Test (with answers)",
                "Load an existing Test",
                "Save the current Test",
                "Take the current Test",
                "Modify the current Test",
                "Tabulate a Test",
                "Grade a Test",
                "Return to previous menu");
        output.printMenu("--- Test Menu ---", options);
    }

    // -------------------------------------------------------------------------
    // Create submenu
    // -------------------------------------------------------------------------

    /**
     * Prompts the user for a test name, creates it via TestManager, then loops
     * on the 7-option add-question submenu before returning to the Test Menu.
     */
    private void showCreateMenu() {
        String name = input.readString("Enter a name for the new test: ").trim();
        if (name.isEmpty()) {
            output.printError("Test name cannot be empty.");
            return;
        }
        manager.createNewTest(name);

        List<String> options = Arrays.asList(
                "Add a new T/F question",
                "Add a new multiple-choice question",
                "Add a new short answer question",
                "Add a new essay question",
                "Add a new date question",
                "Add a new matching question",
                "Return to previous menu");

        boolean inMenu = true;
        while (inMenu) {
            output.printMenu("--- Add Questions ---", options);
            int choice = input.readIntInRange("Enter choice: ", 1, 7);
            switch (choice) {
                case 1:
                    manager.addTrueFalseQuestion();
                    break;
                case 2:
                    manager.addMultipleChoiceQuestion();
                    break;
                case 3:
                    manager.addShortAnswerQuestion();
                    break;
                case 4:
                    manager.addEssayQuestion();
                    break;
                case 5:
                    manager.addValidDateQuestion();
                    break;
                case 6:
                    manager.addMatchingQuestion();
                    break;
                case 7:
                    inMenu = false;
                    break;
                default:
                    break;
            }
        }
    }
}
