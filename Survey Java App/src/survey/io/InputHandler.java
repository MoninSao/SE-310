package survey.io;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Handles all console input for the survey system.
 * Every read method validates its input and loops until a valid value is given,
 * so callers never receive malformed data and the program never crashes on bad
 * input.
 */
public class InputHandler {

    private final Scanner scanner;
    private final OutputHandler output;

    /**
     * Constructs an InputHandler that reads from stdin and reports errors
     * through the given OutputHandler.
     *
     * @param output the OutputHandler used to display prompts and error messages
     */
    public InputHandler(OutputHandler output) {
        this.scanner = new Scanner(System.in);
        this.output = output;
    }

    /**
     * Displays a prompt and reads a single line of text (trimmed).
     * Never returns null; returns an empty string if the user enters nothing.
     *
     * @param prompt the text shown before reading
     * @return the trimmed line entered by the user
     */
    public String readString(String prompt) {
        output.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Displays a prompt and reads an integer in [min, max] (inclusive).
     * Loops with an error message until a valid integer in range is entered.
     *
     * @param prompt the text shown before each read attempt
     * @param min    minimum acceptable value (inclusive)
     * @param max    maximum acceptable value (inclusive)
     * @return an integer in [min, max]
     */
    public int readIntInRange(String prompt, int min, int max) {
        while (true) {
            output.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) {
                    return v;
                }
                output.printError("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                output.printError("That's not a number. Try again.");
            }
        }
    }

    /**
     * Displays a prompt and reads any integer.
     * Loops with an error message until a valid integer is entered.
     *
     * @param prompt the text shown before each read attempt
     * @return the integer entered by the user
     */
    public int readInt(String prompt) {
        while (true) {
            output.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                output.printError("That's not a number. Try again.");
            }
        }
    }

    /**
     * Displays a prompt and reads a yes/no boolean.
     * Accepts: true/false, t/f, yes/no, y/n (case-insensitive).
     * Loops until a recognised value is entered.
     *
     * @param prompt the question text (without a "yes/no" suffix — added
     *               automatically)
     * @return true for yes/true/t/y, false for no/false/f/n
     */
    public boolean readBoolean(String prompt) {
        while (true) {
            output.print(prompt + " (yes/no): ");
            String line = scanner.nextLine().trim().toLowerCase();
            switch (line) {
                case "true":
                case "t":
                case "yes":
                case "y":
                    return true;
                case "false":
                case "f":
                case "no":
                case "n":
                    return false;
                default:
                    output.printError("Please enter yes or no.");
            }
        }
    }

    /**
     * Displays a prompt and reads a date matching the given formatter.
     * Loops with an error message until a valid date is entered.
     *
     * @param prompt    the text shown before each read attempt
     * @param formatter the DateTimeFormatter that defines the expected format
     * @return a valid LocalDate parsed from the user's input
     */
    public LocalDate readDate(String prompt, DateTimeFormatter formatter) {
        while (true) {
            output.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return LocalDate.parse(line, formatter);
            } catch (DateTimeParseException e) {
                output.printError("Invalid date format. Please try again.");
            }
        }
    }

    /**
     * Closes the underlying Scanner.
     * Call this once when the application is about to exit.
     */
    public void close() {
        scanner.close();
    }
}
