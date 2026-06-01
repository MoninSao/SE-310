# Survey Java App

A console-based survey and test system written in Java.  Supports creating, saving, loading, displaying, taking, grading, and modifying surveys and tests that contain six question types: True/False, Multiple Choice, Short Answer, Essay, Valid Date, and Matching.

---

## Project Structure

```
Survey Java App/
├── compile.sh          # Compiles all sources into out/
├── run.sh              # Runs the compiled application
├── README.md           # This file
├── src/
│   └── survey/
│       ├── Main.java                    # Entry point
│       ├── SeedData.java               # One-time seed: generates sample test + responses
│       ├── driver/
│       │   ├── MainDriver.java          # Top-level menu (survey vs. test)
│       │   ├── SurveyDriver.java        # Survey menu controller
│       │   └── TestDriver.java          # Test menu controller
│       ├── io/
│       │   ├── InputHandler.java        # Validated console input helpers
│       │   └── OutputHandler.java       # Centralised console output
│       ├── manager/
│       │   ├── SurveyManager.java       # Survey lifecycle (create/save/load/take/modify)
│       │   └── TestManager.java         # Test lifecycle (create/save/load/take/grade/modify)
│       └── model/
│           ├── Survey.java              # Survey domain object (serializable)
│           ├── SurveyResponse.java      # One respondent's complete answers (serializable)
│           ├── question/
│           │   ├── Question.java        # Abstract base for all question types
│           │   ├── TrueFalse.java
│           │   ├── MultipleChoice.java
│           │   ├── ShortAnswer.java
│           │   ├── Essay.java
│           │   ├── ValidDate.java
│           │   └── Matching.java
│           └── response/
│               ├── Response.java        # Abstract base for all response types
│               ├── TrueFalseResponse.java
│               ├── MultipleChoiceResponse.java
│               ├── ShortAnswerResponse.java
│               ├── EssayResponse.java
│               ├── ValidDateResponse.java
│               └── MatchingResponse.java
├── surveys/            # Serialized survey files (*.ser)
│   ├── MySurvey.ser
│   └── sample_survey.ser
├── tests/              # Serialized test files (*.ser)
│   └── all_question_types_test.ser
└── responses/          # Serialized response files (*.ser)
    ├── Alice_all_question_types_test_<ts>.ser
    ├── Bob_all_question_types_test_<ts>.ser
    └── Carol_all_question_types_test_<ts>.ser
```

---

## Requirements

- Java 11 or later (tested with Java 17/21)
- Bash shell (for `compile.sh` / `run.sh`)

---

## Compile Steps

Run from the `Survey Java App/` directory:

```bash
chmod +x compile.sh run.sh   # first time only
./compile.sh
```

`compile.sh` finds every `.java` file under `src/` with `find src -name "*.java"` and writes class files to `out/`.  
Output on success:

```
Compilation successful. Class files written to: out/
```

---

## Run Steps

```bash
./run.sh
```

`run.sh` launches `survey.Main` with classpath `out/`.  The JVM is started from the project root so that all relative paths (`surveys/`, `responses/`) resolve correctly regardless of the shell's working directory.

---

## Sample Files

### Surveys

| File | Description |
|------|-------------|
| `surveys/MySurvey.ser` | A pre-built sample survey you can load immediately |
| `surveys/sample_survey.ser` | A second pre-built survey demonstrating all question types |

To load a sample, choose **3) Load an existing Survey** from the main Survey menu and select the file.

### Tests

| File | Description |
|------|-------------|
| `tests/all_question_types_test.ser` | A pre-built test with one question of every type (True/False, Multiple Choice, Short Answer, Essay, Valid Date, Matching) and a stored correct answer for each auto-gradeable question |

Three pre-seeded response files for this test are included in `responses/` (respondents: Alice, Bob, Carol).  
To explore them:
1. Choose **2) Test System** from the main menu.
2. Load `all_question_types_test` via **4) Load an existing Test**.
3. Use **8) Tabulate** to see aggregated results, or **9) Grade** to score an individual response.

To regenerate the seed files from scratch (e.g. after a serialization-breaking change):
```bash
java -cp out survey.SeedData
```

---

## Response File Naming

When a survey is taken, the response is saved to:

```
responses/<respondentId>_<surveyName>_<yyyyMMdd_HHmmss>.ser
```

Example: `responses/jsmith_MySurvey_20260508_143022.ser`

---

## Question Types

| Type | Description | Multi-response |
|------|-------------|---------------|
| True/False | Answer T or F | No |
| Multiple Choice | Select from labelled choices A, B, C… | Optional |
| Short Answer | Free text up to a character limit | Optional |
| Essay | Unlimited free text | Optional |
| Valid Date | Date in YYYY-MM-DD format | Optional |
| Matching | Match left-column items to right-column items (e.g. `A 2`) | No |

---

## Known Issues

- **Serialization compatibility**: Modifying the class structure of any `Question` or `Response` subclass after saving a `.ser` file may cause `InvalidClassException` when loading that file.  Delete and recreate the `.ser` file after structural changes.
- **Date validation**: The date parser uses `uuuu-MM-dd` (strict ISO proleptic calendar).  Years before 0000 are not accepted.
- **Large surveys**: The in-memory list is not paginated; very large surveys may produce verbose console output.
