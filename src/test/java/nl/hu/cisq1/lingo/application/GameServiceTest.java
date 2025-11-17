package nl.hu.cisq1.lingo.application;

import nl.hu.cisq1.lingo.application.exceptions.GameNotFoundException;
import nl.hu.cisq1.lingo.domain.Game;
import nl.hu.cisq1.lingo.domain.Hint;
import nl.hu.cisq1.lingo.domain.Round;
import nl.hu.cisq1.lingo.domain.enums.GameState;
import nl.hu.cisq1.lingo.domain.enums.RoundOutcome;
import nl.hu.cisq1.lingo.presentation.dto.request.GuessRequest;
import nl.hu.cisq1.lingo.presentation.dto.response.GameResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.GuessResponse;
import nl.hu.cisq1.lingo.repository.GameRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    private static final String WORD_5 = "bruhh";
    private static final String WORD_6 = "kroket";
    private static final String WORD_INVALID = "zzzzz";

    @Mock
    private GameRepository gameRepository;

    @Mock
    private DictionaryService dictionaryService;

    @InjectMocks
    private GameService gameService;

    @Test
    @DisplayName("Starting new game creates first round with five-letter word")
    void startNewGameCreatesFiveLetterRound() {
        when(dictionaryService.randomWord(5)).thenReturn(WORD_5);
        when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GameResponse response = gameService.startNewGame("JoeRogan");

        assertEquals(GameState.IN_ROUND, response.state());
        assertEquals(0, response.score());
        assertEquals(5, response.lastWordLength());
        assertNotNull(response.currentRound());
        verify(dictionaryService).randomWord(5);
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    @DisplayName("Correct guess wins round and updates game state to waiting")
    void makeGuessWithCorrectWordWinsRound() {
        UUID gameId = UUID.randomUUID();
        Game activeGame = activeGame(gameId, WORD_5);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(activeGame));
        when(dictionaryService.exists(WORD_5)).thenReturn(true);
        when(gameRepository.save(activeGame)).thenReturn(activeGame);

        GuessResponse response = gameService.makeGuess(gameId, new GuessRequest(WORD_5));

        assertTrue(response.feedback().correct());
        assertTrue(response.feedback().valid());
        assertEquals(GameState.WAITING_FOR_ROUND, response.gameState().state());
        assertEquals(25, response.gameState().score());
        assertEquals(5, response.gameState().lastWordLength());
        verify(gameRepository).findById(gameId);
        verify(dictionaryService).exists(WORD_5);
        verify(gameRepository).save(activeGame);
    }

    @Test
    @DisplayName("Invalid word is rejected and round remains active")
    void makeGuessWithInvalidWordKeepsRoundActive() {
        UUID gameId = UUID.randomUUID();
        Game activeGame = activeGame(gameId, WORD_5);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(activeGame));
        when(dictionaryService.exists(WORD_INVALID)).thenReturn(false);
        when(gameRepository.save(activeGame)).thenReturn(activeGame);

        GuessResponse response = gameService.makeGuess(gameId, new GuessRequest(WORD_INVALID));

        assertFalse(response.feedback().valid());
        assertEquals(GameState.IN_ROUND, response.gameState().state());
        assertEquals(0, response.gameState().score());
        assertEquals(5, response.gameState().lastWordLength());
        verify(gameRepository).findById(gameId);
        verify(dictionaryService).exists(WORD_INVALID);
        verify(gameRepository).save(activeGame);
    }

    @Test
    @DisplayName("Making guess for non-existent game throws exception")
    void makeGuessThrowsWhenGameNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(gameRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class,
            () -> gameService.makeGuess(nonExistentId, new GuessRequest(WORD_5))
        );

        verify(gameRepository).findById(nonExistentId);
        verifyNoInteractions(dictionaryService);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("Starting new round after winning progresses to six-letter word")
    void startNewRoundProgressesToSixLetterWord() {
        UUID gameId = UUID.randomUUID();
        Game waitingGame = waitingGame(gameId, 25, 5);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(waitingGame));
        when(dictionaryService.randomWord(6)).thenReturn(WORD_6);
        when(gameRepository.save(waitingGame)).thenReturn(waitingGame);

        GameResponse response = gameService.startNewRound(gameId);

        assertEquals(GameState.IN_ROUND, response.state());
        assertEquals(25, response.score());
        assertEquals(6, response.lastWordLength());
        assertNotNull(response.currentRound());
        verify(gameRepository).findById(gameId);
        verify(dictionaryService).randomWord(6);
        verify(gameRepository).save(waitingGame);
    }

    @Test
    @DisplayName("Starting new round for non-existent game throws exception")
    void startNewRoundThrowsWhenGameNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(gameRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class,
            () -> gameService.startNewRound(nonExistentId));

        verify(gameRepository).findById(nonExistentId);
        verifyNoInteractions(dictionaryService);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    @DisplayName("Retrieving existing game returns current state")
    void getGameReturnsCurrentState() {
        UUID gameId = UUID.randomUUID();
        Game activeGame = activeGame(gameId, WORD_5);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(activeGame));

        GameResponse response = gameService.getGame(gameId);

        assertEquals(gameId, response.id());
        assertEquals(GameState.IN_ROUND, response.state());
        assertEquals(0, response.score());
        assertEquals(5, response.lastWordLength());
        assertNotNull(response.currentRound());
        verify(gameRepository).findById(gameId);
    }

    @Test
    @DisplayName("Retrieving non-existent game throws exception")
    void getGameThrowsWhenGameNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(gameRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class,
            () -> gameService.getGame(nonExistentId));

        verify(gameRepository).findById(nonExistentId);
    }

    @Test
    @DisplayName("Forfeiting game eliminates player and ends round")
    void forfeitGameEliminatesPlayer() {
        UUID gameId = UUID.randomUUID();
        Game activeGame = activeGame(gameId, WORD_5);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(activeGame));
        when(gameRepository.save(activeGame)).thenReturn(activeGame);

        GameResponse response = gameService.forfeitGame(gameId);

        assertEquals(GameState.ELIMINATED, response.state());
        assertEquals(0, response.score());
        assertNotNull(response.currentRound());
        verify(gameRepository).findById(gameId);
        verify(gameRepository).save(activeGame);
    }

    @Test
    @DisplayName("Forfeiting non-existent game throws exception")
    void forfeitGameThrowsWhenGameNotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(gameRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(GameNotFoundException.class,
            () -> gameService.forfeitGame(nonExistentId));

        verify(gameRepository).findById(nonExistentId);
        verify(gameRepository, never()).save(any(Game.class));
    }

    private Game activeGame(UUID id, String targetWord) {
        Round round = new Round(targetWord, 5, 0, new ArrayList<>(), RoundOutcome.IN_PROGRESS, Hint.initialFor(targetWord));
        List<Round> pastRounds = new ArrayList<>();
        return new Game(id, "JoeRogan", 0, GameState.IN_ROUND, round, pastRounds, targetWord.length(), false);
    }

    private Game waitingGame(UUID id, int score, int lastWordLength) {
        Round finishedRound = new Round(WORD_5, 5, 1, new ArrayList<>(), RoundOutcome.WON, Hint.initialFor(WORD_5));
        List<Round> pastRounds = new ArrayList<>();
        pastRounds.add(finishedRound);
        return new Game(id, "JoeRogan", score, GameState.WAITING_FOR_ROUND, null, pastRounds, lastWordLength, false);
    }
}
