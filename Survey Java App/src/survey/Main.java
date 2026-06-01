package survey;

import survey.driver.MainDriver;

/**
 * Application entry point.
 * Constructs a MainDriver and starts the top-level menu loop.
 */
public class Main {

    /**
     * Main method — run with: java -cp out survey.Main
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new MainDriver().run();
    }
}
