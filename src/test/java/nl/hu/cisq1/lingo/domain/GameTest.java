package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.GameState;
import nl.hu.cisq1.lingo.domain.enums.RoundOutcome;
import nl.hu.cisq1.lingo.domain.exceptions.InvalidActionException;

import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private static final Dictionary TEST_DICTIONARY = new StubDictionary();

    private static class StubDictionary implements Dictionary {
        @Override
        public boolean exists(String word) {
            return true;
        }

        @Override
        public String randomWord(int length) {
            return "bruhh";
        }
    }

    @ParameterizedTest
    @MethodSource("wordLengths")
    @DisplayName("Word length progresses correctly")
    void wordLengthProgressesCorrectly(int previous, int expected) {
        Game game = new Game(null, "ZappBrannigan", 0, GameState.NEW, null, new ArrayList<>(), previous, false);

        game.startNewRound(TEST_DICTIONARY);

        assertEquals(expected, game.getLastWordLength());
    }

    static Stream<Arguments> wordLengths() {
        return Stream.of(
            Arguments.of(0, 5),
            Arguments.of(5, 6),
            Arguments.of(6, 7),
            Arguments.of(7, 5)
        );
    }

    @Test
    @DisplayName("Starting game initializes correctly")
    void startingGameInitializesCorrectly() {
        Game game = new Game(null, "ZappBrannigan", 0, GameState.NEW, null, new ArrayList<>(), 0, false);

        game.startGame(TEST_DICTIONARY);

        assertEquals(0, game.getScore());
        assertEquals(GameState.IN_ROUND, game.getState());
        assertNotNull(game.getCurrentRound());
        assertEquals(5, game.getLastWordLength());
    }

    @ParameterizedTest
    @MethodSource("invalidStates")
    @DisplayName("Cannot start new round in invalid states")
    void cannotStartNewRoundInInvalidStates(GameState state) {
        Game game = new Game(null, "ZappBrannigan", 0, state, null, new ArrayList<>(), 0, false);

        assertThrows(InvalidActionException.class, () -> game.startNewRound(TEST_DICTIONARY));
    }

    static Stream<Arguments> invalidStates() {
        return Stream.of(
            Arguments.of(GameState.ELIMINATED),
            Arguments.of(GameState.IN_ROUND)
        );
    }

    @Test
    @DisplayName("Correct guess wins round and updates state")
    void correctGuessWinsRoundAndUpdatesState() {
        Round round = new Round("bruhh", 5, 0, new ArrayList<>(), RoundOutcome.IN_PROGRESS, Hint.initialFor("bruhh"));
        Game game = new Game(null, "ZappBrannigan", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        Feedback feedback = game.guess("bruhh", TEST_DICTIONARY);

        assertTrue(feedback.isWordGuessed());
        assertEquals(GameState.WAITING_FOR_ROUND, game.getState());
        assertNotNull(game.getCurrentRound());
        assertEquals(0, game.getPastRounds().size());
    }

    @Test
    @DisplayName("Score increases after winning round")
    void scoreIncreasesAfterWinningRound() {
        Round round = new Round("bruhh", 5, 0, new ArrayList<>(), RoundOutcome.IN_PROGRESS, Hint.initialFor("bruhh"));
        Game game = new Game(null, "ZappBrannigan", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        game.guess("bruhh", TEST_DICTIONARY);

        assertTrue(game.getScore() > 0);
    }

    @Test
    @DisplayName("Losing round eliminates player")
    void losingRoundEliminatesPlayer() {
        Round round = new Round("bruhh", 5, 4, new ArrayList<>(), RoundOutcome.IN_PROGRESS, Hint.initialFor("bruhh"));
        Game game = new Game(null, "ZappBrannigan", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        game.guess("nopes", TEST_DICTIONARY);

        assertEquals(GameState.ELIMINATED, game.getState());
        assertNotNull(game.getCurrentRound());
        assertEquals(0, game.getPastRounds().size());
    }

    @Test
    @DisplayName("Cannot guess when not in round")
    void cannotGuessWhenNotInRound() {
        Game game = new Game(null, "ZappBrannigan", 0, GameState.NEW, null, new ArrayList<>(), 0, false);

        assertThrows(InvalidActionException.class, () -> game.guess("bruhh", TEST_DICTIONARY));
    }

    @Test
    @DisplayName("Forfeit eliminates player")
    void forfeitEliminatesPlayer() {
        Round round = new Round("bruhh", 5, 2, new ArrayList<>(), RoundOutcome.IN_PROGRESS, Hint.initialFor("bruhh"));
        Game game = new Game(null, "ZappBrannigan", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        game.forfeit();

        assertEquals(GameState.ELIMINATED, game.getState());
        assertNotNull(game.getCurrentRound());
        assertEquals(0, game.getPastRounds().size());
    }

    @Test
    @DisplayName("Cannot forfeit when not in round")
    void cannotForfeitWhenNotInRound() {
        Game game = new Game(null, "ZappBrannigan", 0, GameState.NEW, null, new ArrayList<>(), 0, false);

        assertThrows(InvalidActionException.class, () -> game.forfeit());
    }

    @Test
    @DisplayName("Current hint is returned from active round")
    void currentHintReturnedFromActiveRound() {
        Hint expectedHint = Hint.initialFor("bruhh");
        Round round = new Round("bruhh", 5, 0, new ArrayList<>(), RoundOutcome.IN_PROGRESS, expectedHint);
        Game game = new Game(null, "ZappBrannigan", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        Hint actualHint = game.getCurrentHint();

        assertEquals(expectedHint, actualHint);
    }

    @Test
    @DisplayName("Cannot get hint when not in round")
    void cannotGetHintWhenNotInRound() {
        Game game = new Game(null, "ZappBrannigan", 0, GameState.NEW, null, new ArrayList<>(), 0, false);

        assertThrows(InvalidActionException.class, () -> game.getCurrentHint());
    }

    @Test
    @DisplayName("Attempts remaining is returned from active round")
    void attemptsRemainingReturnedFromActiveRound() {
        Round round = new Round("bruhh", 5, 2, new ArrayList<>(), RoundOutcome.IN_PROGRESS, Hint.initialFor("bruhh"));
        Game game = new Game(null, "ZappBrannigan", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        int remaining = game.getAttemptsRemaining();

        assertEquals(3, remaining);
    }

    @Test
    @DisplayName("Cannot get attempts when not in round")
    void cannotGetAttemptsWhenNotInRound() {
        Game game = new Game(null, "ZappBrannigan", 0, GameState.NEW, null, new ArrayList<>(), 0, false);

        assertThrows(InvalidActionException.class, () -> game.getAttemptsRemaining());
    }

    @Test
    @DisplayName("Past rounds are tracked correctly")
    void pastRoundsTrackedCorrectly() {
        Round round = new Round("bruhh", 5, 0, new ArrayList<>(), RoundOutcome.IN_PROGRESS, Hint.initialFor("bruhh"));
        Game game = new Game(null, "ZappBrannigan", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        game.guess("bruhh", TEST_DICTIONARY);
        game.startNewRound(TEST_DICTIONARY);

        assertEquals(1, game.getPastRounds().size());
    }

    @Test
    @DisplayName("Can start new round after winning")
    void canStartNewRoundAfterWinning() {
        Round round = new Round("bruhh", 5, 0, new ArrayList<>(), RoundOutcome.IN_PROGRESS, Hint.initialFor("bruhh"));
        Game game = new Game(null, "ZappBrannigan", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        game.guess("bruhh", TEST_DICTIONARY);
        game.startNewRound(TEST_DICTIONARY);

        assertEquals(GameState.IN_ROUND, game.getState());
        assertNotNull(game.getCurrentRound());
        assertEquals(6, game.getLastWordLength());
    }
}
