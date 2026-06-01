package survey.driver;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.manager.SurveyManager;

import java.util.Arrays;
import java.util.List;

/**
 * Top-level controller for the survey application.
 * Owns the I/O handlers and the SurveyManager, and drives the two-level
 * text menu described in the assignment spec.
 *
 * Menu 1 — main options (create / display / load / save / take / modify / quit)
 * Menu 2 — add-question submenu shown after creating a new survey
 */
public class SurveyDriver {

    private final OutputHandler output;
    private final InputHandler input;
    private final SurveyManager manager;

    /**
     * Constructs a SurveyDriver, wiring together all collaborators.
     */
    public SurveyDriver() {
        this.output = new OutputHandler();
        this.input = new InputHandler(output);
        this.manager = new SurveyManager(input, output);
    }

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    /**
     * Starts the application and loops on Menu 1 until the user chooses Quit.
     */
    public void run() {
        output.println("=========================================");
        output.println("          Survey System  v1.0            ");
        output.println("=========================================");

        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = input.readIntInRange("Enter choice: ", 1, 7);
            switch (choice) {
                case 1:
                    showCreateMenu();
                    break;
                case 2:
                    manager.displaySurvey();
                    break;
                case 3:
                    manager.loadSurvey();
                    break;
                case 4:
                    manager.saveSurvey();
                    break;
                case 5:
                    manager.takeSurvey();
                    break;
                case 6:
                    manager.modifySurvey();
                    break;
                case 7:
                    output.println("Goodbye!");
                    input.close();
                    running = false;
                    break;
                default:
                    // readIntInRange guarantees 1-7; this branch is unreachable
                    break;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Menu 1 — main menu display
    // -------------------------------------------------------------------------

    private void showMainMenu() {
        List<String> options = Arrays.asList(
                "Create a new Survey",
                "Display an existing Survey",
                "Load an existing Survey",
                "Save the current Survey",
                "Take the current Survey",
                "Modify the current Survey",
                "Quit");
        output.printMenu("--- Main Menu ---", options);
    }

    // -------------------------------------------------------------------------
    // Menu 2 — create / add-questions submenu
    // -------------------------------------------------------------------------

    /**
     * Prompts the user for a survey name, creates it via SurveyManager, then
     * loops on Menu 2 so the user can add questions before returning to Menu 1.
     */
    private void showCreateMenu() {
        // Ask for the survey name first
        String name = input.readString("Enter a name for the new survey: ").trim();
        if (name.isEmpty()) {
            output.printError("Survey name cannot be empty.");
            return;
        }
        manager.createNewSurvey(name);

        // Loop on Menu 2 until the user chooses "Return to previous menu"
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
