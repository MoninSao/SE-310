package survey;

import survey.model.SurveyResponse;
import survey.model.Test;
import survey.model.question.Essay;
import survey.model.question.Matching;
import survey.model.question.MultipleChoice;
import survey.model.question.ShortAnswer;
import survey.model.question.TrueFalse;
import survey.model.question.ValidDate;
import survey.model.response.EssayResponse;
import survey.model.response.MatchingResponse;
import survey.model.response.MultipleChoiceResponse;
import survey.model.response.ShortAnswerResponse;
import survey.model.response.TrueFalseResponse;
import survey.model.response.ValidDateResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Seed program — generates sample serialized artifacts without user
 * interaction.
 *
 * Produces:
 * tests/all_question_types_test.ser — a Test with all 6 question types
 * responses/Alice_all_question_types_test_<ts>.ser
 * responses/Bob_all_question_types_test_<ts>.ser
 * responses/Carol_all_question_types_test_<ts>.ser
 *
 * Run once from the project root after compiling:
 * java -cp out survey.SeedData
 */
public class SeedData {

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static void main(String[] args) throws IOException {

        // ------------------------------------------------------------------ //
        // Build the test //
        // ------------------------------------------------------------------ //
        Test test = new Test("all_question_types_test");

        // 1 — True / False
        TrueFalse q1 = new TrueFalse("Is Java a purely object-oriented language?");
        test.addQuestion(q1);
        test.addCorrectAnswer("False"); // primitives exist → False

        // 2 — Multiple Choice (single answer)
        MultipleChoice q2 = new MultipleChoice(
                "Which keyword is used to inherit a class in Java?", false);
        q2.addChoice("implements");
        q2.addChoice("extends");
        q2.addChoice("inherits");
        q2.addChoice("super");
        test.addQuestion(q2);
        test.addCorrectAnswer("B"); // extends

        // 3 — Short Answer
        ShortAnswer q3 = new ShortAnswer(
                "Name one Java primitive type.", false, 20);
        test.addQuestion(q3);
        test.addCorrectAnswer("int");

        // 4 — Essay (null correct answer — not auto-graded)
        Essay q4 = new Essay(
                "Explain the concept of polymorphism in your own words.", false);
        test.addQuestion(q4);
        test.addCorrectAnswer(null);

        // 5 — Valid Date
        ValidDate q5 = new ValidDate(
                "What date was the Java 1.0 SE specification released?", false);
        test.addQuestion(q5);
        test.addCorrectAnswer("1996-01-23");

        // 6 — Matching
        Matching q6 = new Matching(
                "Match each Java collection to its defining characteristic.");
        q6.addPair("ArrayList", "Backed by a resizable array");
        q6.addPair("HashMap", "Stores key-value pairs with O(1) average lookup");
        q6.addPair("LinkedList", "Doubly-linked nodes, efficient insertion/removal");
        test.addQuestion(q6);
        test.addCorrectAnswer("A 1, B 2, C 3");

        // ------------------------------------------------------------------ //
        // Save the test //
        // ------------------------------------------------------------------ //
        Path testsDir = Paths.get("tests");
        Files.createDirectories(testsDir);
        Path testPath = testsDir.resolve("all_question_types_test.ser");
        test.save(testPath);
        System.out.println("Saved: " + testPath);

        // ------------------------------------------------------------------ //
        // Build and save three responses //
        // ------------------------------------------------------------------ //
        Path responsesDir = Paths.get("responses");
        Files.createDirectories(responsesDir);

        saveResponse(responsesDir, buildAlice(test.getName()), "Alice", test.getName());
        saveResponse(responsesDir, buildBob(test.getName()), "Bob", test.getName());
        saveResponse(responsesDir, buildCarol(test.getName()), "Carol", test.getName());
    }

    // ----------------------------------------------------------------------- //
    // Response builders //
    // ----------------------------------------------------------------------- //

    private static SurveyResponse buildAlice(String surveyName) {
        SurveyResponse sr = new SurveyResponse(surveyName, "Alice");

        TrueFalseResponse r1 = new TrueFalseResponse(
                "Is Java a purely object-oriented language?");
        r1.addAnswer("False");
        sr.addResponse(r1);

        MultipleChoiceResponse r2 = new MultipleChoiceResponse(
                "Which keyword is used to inherit a class in Java?");
        r2.addAnswer("B");
        sr.addResponse(r2);

        ShortAnswerResponse r3 = new ShortAnswerResponse(
                "Name one Java primitive type.");
        r3.addAnswer("int");
        sr.addResponse(r3);

        EssayResponse r4 = new EssayResponse(
                "Explain the concept of polymorphism in your own words.");
        r4.addAnswer("Polymorphism lets an object take many forms. A parent "
                + "reference can point to a child object and call overridden "
                + "methods, enabling flexible and extensible designs.");
        sr.addResponse(r4);

        ValidDateResponse r5 = new ValidDateResponse(
                "What date was the Java 1.0 SE specification released?");
        r5.addAnswer("1996-01-23");
        sr.addResponse(r5);

        MatchingResponse r6 = new MatchingResponse(
                "Match each Java collection to its defining characteristic.");
        r6.addAnswer("A 1");
        r6.addAnswer("B 2");
        r6.addAnswer("C 3");
        sr.addResponse(r6);

        return sr;
    }

    private static SurveyResponse buildBob(String surveyName) {
        SurveyResponse sr = new SurveyResponse(surveyName, "Bob");

        TrueFalseResponse r1 = new TrueFalseResponse(
                "Is Java a purely object-oriented language?");
        r1.addAnswer("True"); // incorrect on purpose
        sr.addResponse(r1);

        MultipleChoiceResponse r2 = new MultipleChoiceResponse(
                "Which keyword is used to inherit a class in Java?");
        r2.addAnswer("A"); // incorrect on purpose
        sr.addResponse(r2);

        ShortAnswerResponse r3 = new ShortAnswerResponse(
                "Name one Java primitive type.");
        r3.addAnswer("double");
        sr.addResponse(r3);

        EssayResponse r4 = new EssayResponse(
                "Explain the concept of polymorphism in your own words.");
        r4.addAnswer("Polymorphism means one interface, many implementations. "
                + "It allows methods to do different things based on the object "
                + "that invokes them.");
        sr.addResponse(r4);

        ValidDateResponse r5 = new ValidDateResponse(
                "What date was the Java 1.0 SE specification released?");
        r5.addAnswer("1995-05-23"); // incorrect on purpose
        sr.addResponse(r5);

        MatchingResponse r6 = new MatchingResponse(
                "Match each Java collection to its defining characteristic.");
        r6.addAnswer("A 2"); // incorrect on purpose
        r6.addAnswer("B 1");
        r6.addAnswer("C 3");
        sr.addResponse(r6);

        return sr;
    }

    private static SurveyResponse buildCarol(String surveyName) {
        SurveyResponse sr = new SurveyResponse(surveyName, "Carol");

        TrueFalseResponse r1 = new TrueFalseResponse(
                "Is Java a purely object-oriented language?");
        r1.addAnswer("False");
        sr.addResponse(r1);

        MultipleChoiceResponse r2 = new MultipleChoiceResponse(
                "Which keyword is used to inherit a class in Java?");
        r2.addAnswer("B");
        sr.addResponse(r2);

        ShortAnswerResponse r3 = new ShortAnswerResponse(
                "Name one Java primitive type.");
        r3.addAnswer("boolean");
        sr.addResponse(r3);

        EssayResponse r4 = new EssayResponse(
                "Explain the concept of polymorphism in your own words.");
        r4.addAnswer("Polymorphism is the ability of different objects to respond "
                + "to the same message in different ways. It supports the "
                + "open/closed principle.");
        sr.addResponse(r4);

        ValidDateResponse r5 = new ValidDateResponse(
                "What date was the Java 1.0 SE specification released?");
        r5.addAnswer("1996-01-23");
        sr.addResponse(r5);

        MatchingResponse r6 = new MatchingResponse(
                "Match each Java collection to its defining characteristic.");
        r6.addAnswer("A 1");
        r6.addAnswer("B 2");
        r6.addAnswer("C 3");
        sr.addResponse(r6);

        return sr;
    }

    // ----------------------------------------------------------------------- //
    // Helper //
    // ----------------------------------------------------------------------- //

    private static void saveResponse(Path dir, SurveyResponse sr,
            String respondentId, String surveyName)
            throws IOException {
        String ts = LocalDateTime.now().format(TS_FMT);
        String filename = respondentId + "_" + surveyName + "_" + ts + ".ser";
        Path path = dir.resolve(filename);
        sr.save(path);
        System.out.println("Saved: " + path);
    }
}
