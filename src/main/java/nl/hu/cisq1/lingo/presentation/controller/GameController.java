package nl.hu.cisq1.lingo.presentation.controller;

import nl.hu.cisq1.lingo.application.GameService;
import nl.hu.cisq1.lingo.presentation.dto.request.GuessRequest;
import nl.hu.cisq1.lingo.presentation.dto.request.StartGameRequest;
import nl.hu.cisq1.lingo.presentation.dto.response.GameResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.GuessResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.ScoreboardEntry;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService service;

    public GameController(GameService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameResponse startGame(@RequestBody StartGameRequest request) {
        boolean randomLength = request.randomLength() != null ? request.randomLength() : false;
        return service.startNewGame(request.username(), randomLength);
    }

    @GetMapping("/{gameId}")
    public GameResponse getGame(@PathVariable UUID gameId) {
        return service.getGame(gameId);
    }

    @PostMapping("/{gameId}/guess")
    public GuessResponse guess(@PathVariable UUID gameId, @RequestBody GuessRequest request) {
        return service.makeGuess(gameId, request);
    }

    @PostMapping("/{gameId}/rounds")
    public GameResponse startNewRound(
            @PathVariable UUID gameId,
            @RequestParam(required = false, defaultValue = "false") boolean random) {
        return service.startNewRound(gameId, random);
    }

    @DeleteMapping("/{gameId}")
    public GameResponse forfeitGame(@PathVariable UUID gameId) {
        return service.forfeitGame(gameId);
    }

    @GetMapping("/scoreboard")
    public List<ScoreboardEntry> getScoreboard() {
        return service.getScoreboard();
    }
}
