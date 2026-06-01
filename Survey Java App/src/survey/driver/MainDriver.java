package survey.driver;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.manager.SurveyManager;
import survey.manager.TestManager;

import java.util.Arrays;
import java.util.List;

/**
 * Top-level application controller.
 * Creates shared I/O handlers and routes to either the survey or test
 * subsystem.
 *
 * Menu 1 — Survey | Test | Quit
 */
public class MainDriver {

    private final InputHandler input;
    private final OutputHandler output;
    private final SurveyDriver surveyDriver;
    private final TestDriver testDriver;

    public MainDriver() {
        this.output = new OutputHandler();
        this.input = new InputHandler(output);
        SurveyManager surveyManager = new SurveyManager(input, output);
        TestManager testManager = new TestManager(input, output);
        this.surveyDriver = new SurveyDriver(input, output, surveyManager);
        this.testDriver = new TestDriver(input, output, testManager);
    }

    public void run() {
        output.println("=========================================");
        output.println("       Survey & Test System  v1.0        ");
        output.println("=========================================");

        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = input.readIntInRange("Enter choice: ", 1, 3);
            switch (choice) {
                case 1:
                    surveyDriver.run();
                    break;
                case 2:
                    testDriver.run();
                    break;
                case 3:
                    output.println("Goodbye!");
                    input.close();
                    running = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void showMainMenu() {
        List<String> options = Arrays.asList("Survey", "Test", "Quit");
        output.printMenu("--- Main Menu ---", options);
    }
}
