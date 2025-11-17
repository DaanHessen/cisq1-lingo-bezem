package nl.hu.cisq1.lingo.application;

import nl.hu.cisq1.lingo.domain.Feedback;
import nl.hu.cisq1.lingo.domain.Game;
import nl.hu.cisq1.lingo.domain.Hint;
import nl.hu.cisq1.lingo.domain.Round;
import nl.hu.cisq1.lingo.domain.enums.GameState;
import nl.hu.cisq1.lingo.domain.enums.Mark;
import nl.hu.cisq1.lingo.domain.enums.RoundOutcome;
import nl.hu.cisq1.lingo.presentation.dto.response.AttemptResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.FeedbackResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.GameResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.GuessResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.RoundResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameMapperTest {

    @Test
    @DisplayName("Maps game with current round correctly")
    void mapsGameWithCurrentRound() {
        UUID gameId = UUID.randomUUID();
        Hint hint = Hint.initialFor("bruhh");
        Round round = new Round("bruhh", 5, 0, new ArrayList<>(), RoundOutcome.IN_PROGRESS, hint);
        Game game = new Game(gameId, "BeetleJuice", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        GameResponse response = GameMapper.toGameResponse(game);

        assertNotNull(response);
        assertEquals(gameId, response.id());
        assertEquals(GameState.IN_ROUND, response.state());
        assertEquals(0, response.score());
        assertEquals(5, response.lastWordLength());
        assertNotNull(response.currentRound());
    }

    @Test
    @DisplayName("Maps game without current round correctly")
    void mapsGameWithoutCurrentRound() {
        UUID gameId = UUID.randomUUID();
        Game game = new Game(gameId, "BeetleJuice", 25, GameState.WAITING_FOR_ROUND, null, new ArrayList<>(), 5, false);

        GameResponse response = GameMapper.toGameResponse(game);

        assertNotNull(response);
        assertEquals(gameId, response.id());
        assertEquals(GameState.WAITING_FOR_ROUND, response.state());
        assertEquals(25, response.score());
        assertEquals(5, response.lastWordLength());
        assertNull(response.currentRound());
    }

    @Test
    @DisplayName("Maps eliminated game correctly")
    void mapsEliminatedGame() {
        UUID gameId = UUID.randomUUID();
        Game game = new Game(gameId, "BeetleJuice", 0, GameState.ELIMINATED, null, new ArrayList<>(), 5, false);

        GameResponse response = GameMapper.toGameResponse(game);

        assertNotNull(response);
        assertEquals(GameState.ELIMINATED, response.state());
        assertNull(response.currentRound());
    }

    @Test
    @DisplayName("Maps round with attempts correctly")
    void mapsRoundWithAttempts() {
        Hint hint = Hint.initialFor("bruhh");
        List<Feedback> history = List.of(
            Feedback.of("wrong", List.of(Mark.ABSENT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT))
        );
        Round round = new Round("bruhh", 5, 1, history, RoundOutcome.IN_PROGRESS, hint);

        RoundResponse response = GameMapper.toRoundResponse(round);

        assertNotNull(response);
        assertEquals(RoundOutcome.IN_PROGRESS, response.outcome());
        assertEquals(4, response.attemptsRemaining());
        assertEquals(5, response.maxAttempts());
        assertEquals(1, response.attempts().size());
        assertNull(response.targetWord());
    }

    @Test
    @DisplayName("Maps completed round with revealed target word")
    void mapsCompletedRoundWithTargetWord() {
        Hint hint = Hint.initialFor("bruhh");
        List<Feedback> history = List.of(
            Feedback.correct("bruhh")
        );
        Round round = new Round("bruhh", 5, 1, history, RoundOutcome.WON, hint);
        RoundResponse response = GameMapper.toRoundResponse(round);

        assertNotNull(response);
        assertEquals(RoundOutcome.WON, response.outcome());
        assertEquals("bruhh", response.targetWord());
    }

    @Test
    @DisplayName("Maps round with empty history correctly")
    void mapsRoundWithEmptyHistory() {
        Hint hint = Hint.initialFor("bruhh");
        Round round = new Round("bruhh", 5, 0, new ArrayList<>(), RoundOutcome.IN_PROGRESS, hint);

        RoundResponse response = GameMapper.toRoundResponse(round);

        assertNotNull(response);
        assertTrue(response.attempts().isEmpty());
        assertEquals(5, response.attemptsRemaining());
    }

    @Test
    @DisplayName("Maps single feedback to attempt response")
    void mapsFeedbackToAttemptResponse() {
        List<Mark> marks = List.of(Mark.CORRECT, Mark.PRESENT, Mark.ABSENT, Mark.CORRECT, Mark.CORRECT);
        Feedback feedback = Feedback.of("crocs", marks);

        AttemptResponse response = GameMapper.toAttemptResponse(feedback);

        assertNotNull(response);
        assertEquals("crocs", response.attempt());
        assertEquals(marks, response.marks());
        assertEquals(5, response.marks().size());
    }

    @Test
    @DisplayName("Maps feedback list to attempt response list")
    void mapsFeedbackListToAttemptResponseList() {
        List<Feedback> feedbackList = List.of(
            Feedback.of("wrong", List.of(Mark.ABSENT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT)),
            Feedback.of("close", List.of(Mark.PRESENT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT, Mark.CORRECT)),
            Feedback.correct("bruhh")
        );

        List<AttemptResponse> responses = GameMapper.toAttemptResponseList(feedbackList);

        assertNotNull(responses);
        assertEquals(3, responses.size());
        assertEquals("wrong", responses.get(0).attempt());
        assertEquals("close", responses.get(1).attempt());
        assertEquals("bruhh", responses.get(2).attempt());
    }

    @Test
    @DisplayName("Maps empty feedback list correctly")
    void mapsEmptyFeedbackList() {
        List<Feedback> emptyList = new ArrayList<>();

        List<AttemptResponse> responses = GameMapper.toAttemptResponseList(emptyList);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("Maps valid feedback to feedback response")
    void mapsValidFeedbackToFeedbackResponse() {
        List<Mark> marks = List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT);
        Feedback feedback = Feedback.of("bruhh", marks);

        FeedbackResponse response = GameMapper.toFeedbackResponse(feedback);

        assertNotNull(response);
        assertEquals(marks, response.marks());
        assertTrue(response.correct());
        assertTrue(response.valid());
    }

    @Test
    @DisplayName("Maps invalid feedback to feedback response")
    void mapsInvalidFeedbackToFeedbackResponse() {
        Feedback feedback = Feedback.invalid("zzzzz");

        FeedbackResponse response = GameMapper.toFeedbackResponse(feedback);

        assertNotNull(response);
        assertFalse(response.correct());
        assertFalse(response.valid());
        assertTrue(response.marks().stream().allMatch(mark -> mark == Mark.INVALID));
    }

    @Test
    @DisplayName("Maps partial match feedback to feedback response")
    void mapsPartialMatchFeedbackToFeedbackResponse() {
        List<Mark> marks = List.of(Mark.CORRECT, Mark.PRESENT, Mark.ABSENT, Mark.CORRECT, Mark.PRESENT);
        Feedback feedback = Feedback.of("capes", marks);

        FeedbackResponse response = GameMapper.toFeedbackResponse(feedback);

        assertNotNull(response);
        assertFalse(response.correct());
        assertTrue(response.valid());
        assertEquals(marks, response.marks());
    }

    @Test
    @DisplayName("Maps guess response with winning feedback")
    void mapsGuessResponseWithWinningFeedback() {
        UUID gameId = UUID.randomUUID();
        Feedback feedback = Feedback.correct("bruhh");
        Game game = new Game(gameId, "BeetleJuice", 25, GameState.WAITING_FOR_ROUND, null, new ArrayList<>(), 5, false);

        GuessResponse response = GameMapper.toGuessResponse(feedback, game);

        assertNotNull(response);
        assertTrue(response.feedback().correct());
        assertTrue(response.feedback().valid());
        assertEquals(GameState.WAITING_FOR_ROUND, response.gameState().state());
        assertEquals(25, response.gameState().score());
    }

    @Test
    @DisplayName("Maps guess response with invalid feedback")
    void mapsGuessResponseWithInvalidFeedback() {
        UUID gameId = UUID.randomUUID();
        Hint hint = Hint.initialFor("bruhh");
        Round round = new Round("bruhh", 5, 1, new ArrayList<>(), RoundOutcome.IN_PROGRESS, hint);
        Feedback feedback = Feedback.invalid("zzzzz");
        Game game = new Game(gameId, "BeetleJuice", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        GuessResponse response = GameMapper.toGuessResponse(feedback, game);

        assertNotNull(response);
        assertFalse(response.feedback().valid());
        assertFalse(response.feedback().correct());
        assertEquals(GameState.IN_ROUND, response.gameState().state());
        assertNotNull(response.gameState().currentRound());
    }

    @Test
    @DisplayName("Maps guess response with partial match feedback")
    void mapsGuessResponseWithPartialMatch() {
        UUID gameId = UUID.randomUUID();
        Hint hint = Hint.initialFor("bruhh");
        Round round = new Round("bruhh", 5, 1, new ArrayList<>(), RoundOutcome.IN_PROGRESS, hint);
        List<Mark> marks = List.of(Mark.CORRECT, Mark.ABSENT, Mark.ABSENT, Mark.PRESENT, Mark.ABSENT);
        Feedback feedback = Feedback.of("axpel", marks);
        Game game = new Game(gameId, "BeetleJuice", 0, GameState.IN_ROUND, round, new ArrayList<>(), 5, false);

        GuessResponse response = GameMapper.toGuessResponse(feedback, game);

        assertNotNull(response);
        assertFalse(response.feedback().correct());
        assertTrue(response.feedback().valid());
        assertEquals(GameState.IN_ROUND, response.gameState().state());
        assertEquals(marks, response.feedback().marks());
    }
}
