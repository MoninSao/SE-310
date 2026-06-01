package survey.model;

import survey.io.OutputHandler;
import survey.model.question.Question;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a survey containing an ordered list of questions.
 * Implements Serializable for Java object serialization (save/load).
 * All fields are serializable; InputHandler/OutputHandler are never stored
 * here.
 */
public class Survey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<Question> questions;

    /**
     * Constructs a new, empty survey with the given name.
     *
     * @param name the survey title
     */
    public Survey(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
    }

    /** @return the survey name */
    public String getName() {
        return name;
    }

    /** @param name the new survey name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the ordered list of questions */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     * Appends a question to this survey.
     *
     * @param question the question to add
     */
    public void addQuestion(Question question) {
        questions.add(question);
    }

    /**
     * Removes the question at the given 0-based index.
     *
     * @param index the index of the question to remove
     */
    public void removeQuestion(int index) {
        questions.remove(index);
    }

    /**
     * Displays the survey name and all questions, numbered from 1.
     * Each question delegates display to its own display() method.
     *
     * @param output the OutputHandler to write to
     */
    public void display(OutputHandler output) {
        output.println("=== Survey: " + name + " ===");
        if (questions.isEmpty()) {
            output.println("  (no questions)");
            return;
        }
        for (int i = 0; i < questions.size(); i++) {
            output.print((i + 1) + ") ");
            questions.get(i).display(output);
        }
    }

    /**
     * Serializes this Survey to the specified file path.
     * Creates or overwrites the file at the given path.
     *
     * @param path the file path to write to (use relative paths — no absolute
     *             paths)
     * @throws IOException if the file cannot be written
     */
    public void save(Path path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path.toFile()))) {
            oos.writeObject(this);
        }
    }

    /**
     * Deserializes a Survey from the specified file path.
     *
     * @param path the file path to read from
     * @return the deserialized Survey
     * @throws IOException            if the file cannot be read
     * @throws ClassNotFoundException if the Survey class cannot be found during
     *                                deserialization
     */
    public static Survey load(Path path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(path.toFile()))) {
            return (Survey) ois.readObject();
        }
    }
}
