package survey.model;

import survey.io.OutputHandler;
import survey.model.response.Response;

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
 * Stores one user's answers to a survey.
 * Each take of a survey creates a separate SurveyResponse, which is then saved
 * to its own file in the responses/ directory.
 * Implements Serializable for persistence via Java object serialization.
 */
public class SurveyResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String surveyName;
    private String respondentId;
    private List<Response> responses;

    /**
     * Constructs a new, empty SurveyResponse.
     *
     * @param surveyName   the name of the survey being taken
     * @param respondentId the name or ID of the person taking the survey
     */
    public SurveyResponse(String surveyName, String respondentId) {
        this.surveyName = surveyName;
        this.respondentId = respondentId;
        this.responses = new ArrayList<>();
    }

    /** @return the name of the survey this response belongs to */
    public String getSurveyName() {
        return surveyName;
    }

    /** @return the respondent's name or ID */
    public String getRespondentId() {
        return respondentId;
    }

    /** @return the ordered list of per-question responses */
    public List<Response> getResponses() {
        return responses;
    }

    /**
     * Appends a per-question response.
     *
     * @param response the Response to add
     */
    public void addResponse(Response response) {
        responses.add(response);
    }

    /**
     * Displays a summary of all responses.
     *
     * @param output the OutputHandler to write to
     */
    public void display(OutputHandler output) {
        output.println("=== Response by: " + respondentId + " ===");
        for (int i = 0; i < responses.size(); i++) {
            output.print((i + 1) + ") ");
            responses.get(i).display(output);
        }
    }

    /**
     * Serializes this SurveyResponse to the specified file path.
     *
     * @param path the file path to write to
     * @throws IOException if the file cannot be written
     */
    public void save(Path path) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(path.toFile()))) {
            oos.writeObject(this);
        }
    }

    /**
     * Deserializes a SurveyResponse from the specified file path.
     *
     * @param path the file path to read from
     * @return the deserialized SurveyResponse
     * @throws IOException            if the file cannot be read
     * @throws ClassNotFoundException if the class cannot be found during
     *                                deserialization
     */
    public static SurveyResponse load(Path path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(path.toFile()))) {
            return (SurveyResponse) ois.readObject();
        }
    }
}
