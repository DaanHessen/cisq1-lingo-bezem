package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.Mark;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackTest {
    private static final Dictionary VALID_DICTIONARY = new StubDictionary(true);
    private static final Dictionary INVALID_DICTIONARY = new StubDictionary(false);

    private static class StubDictionary implements Dictionary {
        private final boolean existsResult;

        public StubDictionary(boolean existsResult) {
            this.existsResult = existsResult;
        }

        @Override
        public boolean exists(String word) {
            return existsResult;
        }

        @Override
        public String randomWord(int length) {
            return "bruhh";
        }
    }

    @Test
    @DisplayName("word is guessed if all characters are valid")
    void wordIsGuessed() {
        List<Mark> marks = List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT);
        Feedback feedback = Feedback.of("bruhh", marks);

        boolean res = feedback.isWordGuessed();
        assertTrue(res);
    }

    @Test
    @DisplayName("word is not guessed if guess is invalid")
    void wordIsNotGuessed() {
        List<Mark> marks = List.of(Mark.CORRECT, Mark.CORRECT, Mark.ABSENT, Mark.PRESENT, Mark.CORRECT);
        Feedback feedback = Feedback.of("bruhh", marks);

        boolean res = feedback.isWordGuessed();
        assertFalse(res);
    }

    @Test
    @DisplayName("guess is deemed invalid if marks are invalid")
    void guessIsInvalidIfMarksAreInvalid() {
        List<Mark> marks = List.of(Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID, Mark.INVALID);
        Feedback feedback = Feedback.of("bruhh", marks);

        boolean res = feedback.isGuessValid();
        assertFalse(res);
    }

    @Test
    @DisplayName("Correct marks are assigned for a correct guess")
    void correctMarksAreAssignedForCorrectGuess() {
        Feedback feedback = Feedback.correct("bruhh");

        assertTrue(feedback.getMarks().stream().allMatch(mark -> mark == Mark.CORRECT));
    }

    @Test
    @DisplayName("Invalid marks are assigned for an invalid guess")
    void invalidMarksAreAssignedForInvalidGuess() {
        Feedback feedback = Feedback.invalid("bruhh");

        assertTrue(feedback.getMarks().stream().allMatch(mark -> mark == Mark.INVALID));
    }

    @Test
    @DisplayName("Generates correct feedback for a correct guess")
    void correctFeedbackForCorrectGuess() {
        Feedback feedback = Feedback.generate("bruhh", "bruhh", VALID_DICTIONARY);

        assertTrue(feedback.isWordGuessed());
        assertEquals("bruhh", feedback.getAttempt());
    }

    @Test
    @DisplayName("Generates invalid feedback for non-existing word")
    void invalidFeedbackForNonExistingWord() {
        Feedback feedback = Feedback.generate("bruhh", "🦍🦍🦍🦍🦍", INVALID_DICTIONARY);

        assertFalse(feedback.isGuessValid());
    }

    @Test
    @DisplayName("Generates invalid feedback for mismatched word lengths")
    void invalidFeedbackForMismatchedLengths() {
        Feedback feedback = Feedback.generate("bruhh", "🦍", VALID_DICTIONARY);

        assertFalse(feedback.isGuessValid());
    }

    @ParameterizedTest
    @MethodSource("edgeCases")
    @DisplayName("Generates correct marks for edge case word combinations")
    void generatesFeedbackCorrectlyForEdgeCases(String target, String attempt, List<Mark> expectedMarks) {
        Feedback feedback = Feedback.generate(target, attempt, VALID_DICTIONARY);

        assertEquals(expectedMarks, feedback.getMarks(), 
            String.format("Failed for target=%s, attempt=%s", target, attempt));
    }

    static Stream<Arguments> edgeCases() {
        return Stream.of(
            Arguments.of("BANANA", "BANAAN",
                List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.PRESENT, Mark.PRESENT)),
            Arguments.of("GROEP", "GEDOE",
                List.of(Mark.CORRECT, Mark.PRESENT, Mark.ABSENT, Mark.PRESENT, Mark.ABSENT)),
            Arguments.of("AAABBB", "BBBAAA",
                List.of(Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT, Mark.PRESENT)),
            Arguments.of("WOORD", "ABCEF",
                List.of(Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT)),
            Arguments.of("KRUIS", "KSUIR",
                List.of(Mark.CORRECT, Mark.PRESENT, Mark.CORRECT, Mark.CORRECT, Mark.PRESENT)),
            Arguments.of("KASTJE", "KAASJE",
                List.of(Mark.CORRECT, Mark.CORRECT, Mark.ABSENT, Mark.PRESENT, Mark.CORRECT, Mark.CORRECT))
        );
    }
}
