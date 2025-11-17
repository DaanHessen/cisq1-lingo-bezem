package nl.hu.cisq1.lingo.application;

import nl.hu.cisq1.lingo.application.exceptions.GameNotFoundException;
import nl.hu.cisq1.lingo.domain.Game;
import nl.hu.cisq1.lingo.presentation.dto.request.GuessRequest;
import nl.hu.cisq1.lingo.presentation.dto.response.GameResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.GuessResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.ScoreboardEntry;
import nl.hu.cisq1.lingo.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {
    private final GameRepository gameRepository;
    private final DictionaryService dictionaryService;

    public GameService(GameRepository gameRepository, DictionaryService dictionaryService) {
        this.gameRepository = gameRepository;
        this.dictionaryService = dictionaryService;
    }

    @Transactional
    public GameResponse startNewGame(String username) {
        return startNewGame(username, false);
    }

    @Transactional
    public GameResponse startNewGame(String username, boolean randomLength) {
        Game game = new Game();
        game.setUsername(username);
        game.setRandomLength(randomLength);
        game.startGame(dictionaryService);

        game = gameRepository.save(game);
        return GameMapper.toGameResponse(game);
    }    

    @Transactional
    public GuessResponse makeGuess(UUID gameId, GuessRequest request) {
        Game game = findGameById(gameId);
        String attempt = request.attempt();

        var feedback = game.guess(attempt, dictionaryService);
        game = gameRepository.save(game);

        return GameMapper.toGuessResponse(feedback, game);
    }

    @Transactional
    public GameResponse startNewRound(UUID gameId) {
        return startNewRound(gameId, false);
    }

    @Transactional
    public GameResponse startNewRound(UUID gameId, boolean randomLength) {
        Game game = findGameById(gameId);

        game.startNewRound(dictionaryService, randomLength);
        game = gameRepository.save(game);
        
        return GameMapper.toGameResponse(game);
    }

    @Transactional(readOnly = true)
    public GameResponse getGame(UUID gameId) {
        Game game = findGameById(gameId);
        return GameMapper.toGameResponse(game);
    }

    @Transactional
    public GameResponse forfeitGame(UUID gameId) {
        Game game = findGameById(gameId);
        
        game.forfeit();
        gameRepository.save(game);

        return GameMapper.toGameResponse(game);
    }

    @Transactional(readOnly = true)
    public List<ScoreboardEntry> getScoreboard() {
        return gameRepository.findTop20ByOrderByScoreDesc()
            .stream()
            .map(game -> new ScoreboardEntry(
                game.getUsername(), 
                game.getScore(),
                game.isRandomLength() ? "Random" : "Sequential"
            ))
            .toList();
    }

    private Game findGameById(UUID gameId) {
        return gameRepository.findById(gameId)
            .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
    }
}
