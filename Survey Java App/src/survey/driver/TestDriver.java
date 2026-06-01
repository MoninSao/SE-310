package survey.driver;

import survey.io.InputHandler;
import survey.io.OutputHandler;
import survey.manager.TestManager;

import java.util.Arrays;
import java.util.List;

/**
 * Top-level controller for the test subsystem.
 * Stub — full implementation will be added in Phase 1B.
 */
public class TestDriver {

    private final InputHandler input;
    private final OutputHandler output;
    private final TestManager manager;

    public TestDriver(InputHandler input, OutputHandler output, TestManager manager) {
        this.input = input;
        this.output = output;
        this.manager = manager;
    }

    public void run() {
        output.println("=========================================");
        output.println("           Test System  v1.0             ");
        output.println("=========================================");

        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = input.readIntInRange("Enter choice: ", 1, 1);
            switch (choice) {
                case 1:
                    running = false;
                    break;
                default:
                    break;
            }
        }
    }

    private void showMainMenu() {
        List<String> options = Arrays.asList("Return to previous menu");
        output.printMenu("--- Test Menu ---", options);
    }
}
