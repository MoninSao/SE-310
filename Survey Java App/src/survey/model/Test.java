package survey.model;

import survey.io.OutputHandler;
import survey.model.question.Essay;
import survey.model.question.Question;
import survey.model.response.Response;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * A graded test that extends Survey with a parallel list of correct answers.
 * Each slot in correctAnswers corresponds to the question at the same index;
 * null slots mark essay questions that are not auto-graded.
 */
public class Test extends Survey {

    private static final long serialVersionUID = 1L;

    /** Parallel to questions; null for essay questions. */
    private List<String> correctAnswers;

    /**
     * Constructs a new, empty Test with the given name.
     *
     * @param name the test title
     */
    public Test(String name) {
        super(name);
        this.correctAnswers = new ArrayList<>();
    }

    /**
     * Appends a correct answer (or null for an essay) to the parallel list.
     *
     * @param a the correct answer string, or null
     */
    public void addCorrectAnswer(String a) {
        correctAnswers.add(a);
    }

    /** @return the parallel list of correct answers */
    public List<String> getCorrectAnswers() {
        return correctAnswers;
    }

    /**
     * Replaces the correct answer at the given index.
     *
     * @param index the 0-based question index
     * @param a     the new correct answer string (or null for essay)
     */
    public void setCorrectAnswer(int index, String a) {
        correctAnswers.set(index, a);
    }

    /**
     * Displays each question followed by its correct answer (skipped for null
     * slots).
     *
     * @param output the OutputHandler to write to
     */
    public void displayWithAnswers(OutputHandler output) {
        List<Question> questions = getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            output.print((i + 1) + ") ");
            questions.get(i).display(output);
            String ca = correctAnswers.get(i);
            if (ca != null) {
                output.println("The correct answer is " + ca);
            }
        }
    }

    /**
     * Returns the number of questions whose runtime class is exactly Essay
     * (ShortAnswer subclasses are NOT counted as essays and remain gradeable).
     *
     * @return the essay question count
     */
    public int getEssayCount() {
        int count = 0;
        for (Question q : getQuestions()) {
            if (q.getClass() == Essay.class) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the number of questions that can be auto-graded.
     *
     * @return total question count minus essay count
     */
    public int getGradeableCount() {
        return getQuestions().size() - getEssayCount();
    }

    /**
     * Grades a SurveyResponse against the stored correct answers.
     * Each question is worth an equal share of 100 points.
     * Essay questions (exact class Essay) are skipped.
     * Comparison is case-insensitive on the first answer in each Response.
     *
     * @param sr the SurveyResponse to grade
     * @return a score in the range [0, 100]
     */
    public double grade(SurveyResponse sr) {
        List<Question> questions = getQuestions();
        List<Response> responses = sr.getResponses();
        double pointsEach = 100.0 / questions.size();
        double score = 0.0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getClass() == Essay.class) {
                continue;
            }
            String correct = correctAnswers.get(i);
            // Join all answers with "\n" so Matching (multi-answer) is compared
            // correctly; single-answer types produce the same result as .get(0).
            String studentAnswer = String.join("\n", responses.get(i).getAnswers());
            if (correct != null && studentAnswer.equalsIgnoreCase(correct)) {
                score += pointsEach;
            }
        }
        return score;
    }

    /**
     * Deserializes a Test from the specified file path.
     * The cast is safe because the file was saved as a Test instance.
     *
     * @param path the file path to read from
     * @return the deserialized Test
     * @throws IOException            if the file cannot be read
     * @throws ClassNotFoundException if the class cannot be found during
     *                                deserialization
     */
    public static Test load(Path path) throws IOException, ClassNotFoundException {
        return (Test) Survey.load(path);
    }
}
