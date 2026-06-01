package survey.io;

import java.util.List;

/**
 * Handles all console output for the survey system.
 * Centralising output here makes it easy to redirect or test.
 */
public class OutputHandler {

    /**
     * Prints a string without a trailing newline.
     *
     * @param message the text to print
     */
    public void print(String message) {
        System.out.print(message);
    }

    /**
     * Prints a string followed by a newline.
     *
     * @param message the text to print
     */
    public void println(String message) {
        System.out.println(message);
    }

    /** Prints a blank line. */
    public void println() {
        System.out.println();
    }

    /**
     * Prints an error message, visually distinct from normal output.
     *
     * @param message the error text to display
     */
    public void printError(String message) {
        System.out.println("  [!] " + message);
    }

    /**
     * Prints a numbered menu with a title and list of options.
     * Does NOT print an input prompt — callers should follow up with
     * InputHandler.readIntInRange() to collect the selection.
     *
     * @param title   the menu heading
     * @param options ordered list of option labels (1-based in display)
     */
    public void printMenu(String title, List<String> options) {
        println();
        println(title);
        for (int i = 0; i < options.size(); i++) {
            println((i + 1) + ") " + options.get(i));
        }
    }
}
