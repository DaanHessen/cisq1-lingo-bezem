package nl.hu.cisq1.lingo.application;

import nl.hu.cisq1.lingo.domain.Game;
import nl.hu.cisq1.lingo.domain.enums.GameState;
import nl.hu.cisq1.lingo.presentation.dto.request.GuessRequest;
import nl.hu.cisq1.lingo.presentation.dto.response.GameResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.GuessResponse;
import nl.hu.cisq1.lingo.repository.GameRepository;
import nl.hu.cisq1.lingo.words.data.WordRepository;
import nl.hu.cisq1.lingo.words.domain.Word;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class GameServiceIntegrationTest {
    private static final String WORD_5 = "bruhh";
    private static final String WORD_6 = "kroket";
    private static final String WORD_7 = "bananen";

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private WordRepository wordRepository;

    @BeforeEach
    void resetDatabase() {
        gameRepository.deleteAll();
        wordRepository.deleteAll();
        wordRepository.save(new Word(WORD_5));
        wordRepository.save(new Word(WORD_6));
        wordRepository.save(new Word(WORD_7));
    }

    @Test
    @DisplayName("Starting a game persists initial five-letter round")
    void startNewGamePersistsInitialRound() {
        GameResponse response = gameService.startNewGame();

        assertEquals(GameState.IN_ROUND, response.state());
        assertEquals(0, response.score());
        assertEquals(WORD_5.length(), response.lastWordLength());
        assertNotNull(response.currentRound());
        assertEquals(1, gameRepository.count());

        Game persistedGame = gameRepository.findAll().get(0);
        assertEquals(GameState.IN_ROUND, persistedGame.getState());
        assertEquals(WORD_5.length(), persistedGame.getLastWordLength());
        assertNotNull(persistedGame.getCurrentRound());
    }

    @Test
    @DisplayName("Winning guess moves game to waiting state with updated score")
    void makeGuessWinningAttemptMovesGameToWaitingState() {
        UUID gameId = gameService.startNewGame().id();

        GuessResponse response = gameService.makeGuess(gameId, new GuessRequest(WORD_5));

        assertTrue(response.feedback().correct());
        assertEquals(GameState.WAITING_FOR_ROUND, response.gameState().state());
        assertEquals(25, response.gameState().score());
        assertNull(response.gameState().currentRound());

        Game persistedGame = gameRepository.findById(gameId).orElseThrow();
        assertEquals(GameState.WAITING_FOR_ROUND, persistedGame.getState());
        assertEquals(25, persistedGame.getScore());
        assertNull(persistedGame.getCurrentRound());
    }

    @Test
    @DisplayName("Starting new round after win increases word length")
    void startNewRoundAfterWinUsesNextWordLength() {
        UUID gameId = gameService.startNewGame().id();
        gameService.makeGuess(gameId, new GuessRequest(WORD_5));

        GameResponse response = gameService.startNewRound(gameId);

        assertEquals(GameState.IN_ROUND, response.state());
        assertEquals(WORD_6.length(), response.lastWordLength());
        assertEquals(25, response.score());
        assertNotNull(response.currentRound());

        Game persistedGame = gameRepository.findById(gameId).orElseThrow();
        assertEquals(GameState.IN_ROUND, persistedGame.getState());
        assertEquals(WORD_6.length(), persistedGame.getLastWordLength());
        assertEquals(25, persistedGame.getScore());
        assertNotNull(persistedGame.getCurrentRound());
    }

    @Test
    @DisplayName("Forfeiting an active round eliminates the player")
    void forfeitGameEliminatesPlayer() {
        UUID gameId = gameService.startNewGame().id();

        GameResponse response = gameService.forfeitGame(gameId);

        assertEquals(GameState.ELIMINATED, response.state());
        assertEquals(0, response.score());
        assertNull(response.currentRound());

        Game persistedGame = gameRepository.findById(gameId).orElseThrow();
        assertEquals(GameState.ELIMINATED, persistedGame.getState());
        assertEquals(0, persistedGame.getScore());
        assertNull(persistedGame.getCurrentRound());
    }

    @Test
    @DisplayName("Retrieving persisted game returns latest state")
    void getGameReturnsPersistedState() {
        UUID gameId = gameService.startNewGame().id();

        GameResponse response = gameService.getGame(gameId);

        assertEquals(gameId, response.id());
        assertEquals(GameState.IN_ROUND, response.state());
        assertEquals(0, response.score());
        assertNotNull(response.currentRound());
    }
}
