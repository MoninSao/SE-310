package survey;

import survey.driver.SurveyDriver;

/**
 * Application entry point.
 * Constructs a SurveyDriver and starts the menu loop.
 */
public class Main {

    /**
     * Main method — run with: java -cp out survey.Main
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new SurveyDriver().run();
    }
}
