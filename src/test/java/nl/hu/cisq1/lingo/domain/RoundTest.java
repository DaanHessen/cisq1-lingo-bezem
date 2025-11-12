package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.RoundOutcome;
import nl.hu.cisq1.lingo.domain.exceptions.InvalidActionException;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class RoundTest {
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

    @ParameterizedTest
    @MethodSource("roundOutcomesWon")
    @DisplayName("Round is over only when outcome is WON")
    void roundIsOverBasedOnOutcome(RoundOutcome outcome, boolean expectedSolved) {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            0,
            outcome,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        assertEquals(expectedSolved, round.isSolved());
    }

    static Stream<Arguments> roundOutcomesWon() {
        return Stream.of(
            Arguments.of(RoundOutcome.WON, true),
            Arguments.of(RoundOutcome.IN_PROGRESS, false),
            Arguments.of(RoundOutcome.LOST, false)
        );
    }

    @Test
    @DisplayName("Attempts remaining is calculated correctly")
    void attemptsRemainingCalculatedCorrectly() {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            2,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        assertEquals(3, round.getAttemptsRemaining());
    }

    @Test
    @DisplayName("Current hint is returned correctly")
    void currentHintReturnedCorrectly() {
        Hint initialHint = Hint.initialFor("bruhh");
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            0,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            initialHint
        );

        assertEquals(initialHint, round.getCurrentHint());
    }

    @Test
    @DisplayName("Target word is revealed correctly")
    void targetWordRevealedCorrectly() {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            0,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        assertEquals("bruhh", round.revealAnswer());
    }

    @Test
    @DisplayName("Correct guess wins the round")
    void correctGuessWinsRound() {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            0,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        Feedback feedback = round.guess("bruhh", VALID_DICTIONARY);

        assertTrue(feedback.isWordGuessed());
        assertTrue(round.isSolved());
        assertEquals(RoundOutcome.WON, round.getOutcome());
        assertEquals(1, round.getAttemptsUsed());
    }

    @Test
    @DisplayName("Incorrect guess updates round state")
    void incorrectGuessUpdatesRoundState() {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            0,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        Feedback feedback = round.guess("brake", VALID_DICTIONARY);

        assertFalse(feedback.isWordGuessed());
        assertFalse(round.isSolved());
        assertEquals(RoundOutcome.IN_PROGRESS, round.getOutcome());
        assertEquals(1, round.getAttemptsUsed());
    }

    @Test
    @DisplayName("Hint is updated after guess")
    void hintIsUpdatedAfterGuess() {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            0,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        round.guess("brush", VALID_DICTIONARY);

        assertEquals("bru.h", round.getCurrentHint().getValue());
    }

    @Test
    @DisplayName("Feedback is added to history")
    void feedbackAddedToHistory() {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            0,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        round.guess("brake", VALID_DICTIONARY);
        round.guess("trash", VALID_DICTIONARY);

        assertEquals(2, round.getHistory().size());
    }

    @Test
    @DisplayName("Round is lost after max attempts")
    void roundIsLostAfterMaxAttempts() {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            4,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        round.guess("brake", VALID_DICTIONARY);

        assertEquals(RoundOutcome.LOST, round.getOutcome());
        assertTrue(round.isOver());
        assertEquals(0, round.getAttemptsRemaining());
    }

    @ParameterizedTest
    @MethodSource("finishedRounds")
    @DisplayName("Cannot guess after round is over")
    void cannotGuessAfterRoundIsOver(RoundOutcome outcome) {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            3,
            outcome,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        assertThrows(InvalidActionException.class, () -> {
            round.guess("brake", VALID_DICTIONARY);
        });
    }

    static Stream<Arguments> finishedRounds() {
        return Stream.of(
            Arguments.of(RoundOutcome.WON),
            Arguments.of(RoundOutcome.LOST)
        );
    }

    @Test
    @DisplayName("Invalid word generates invalid feedback")
    void invalidWordGeneratesInvalidFeedback() {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            0,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        Feedback feedback = round.guess("zzzzz", INVALID_DICTIONARY);

        assertFalse(feedback.isGuessValid());
        assertEquals(1, round.getAttemptsUsed());
    }

    @Test
    @DisplayName("Wrong length generates invalid feedback")
    void wrongLengthGeneratesInvalidFeedback() {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            0,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        Feedback feedback = round.guess("bru", VALID_DICTIONARY);

        assertFalse(feedback.isGuessValid());
    }

    @Test
    @DisplayName("Winning on last attempt sets outcome to WON")
    void winningOnLastAttemptSetsOutcomeToWon() {
        Round round = new Round(
            UUID.randomUUID(),
            "bruhh",
            5,
            4,
            RoundOutcome.IN_PROGRESS,
            new ArrayList<>(),
            Hint.initialFor("bruhh")
        );

        Feedback feedback = round.guess("bruhh", VALID_DICTIONARY);

        assertTrue(feedback.isWordGuessed());
        assertEquals(RoundOutcome.WON, round.getOutcome());
        assertFalse(round.getOutcome() == RoundOutcome.LOST);
    }
}
