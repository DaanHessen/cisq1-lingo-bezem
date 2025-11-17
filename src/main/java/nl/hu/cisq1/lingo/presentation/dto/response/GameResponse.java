package nl.hu.cisq1.lingo.presentation.dto.response;

import java.util.UUID;
import nl.hu.cisq1.lingo.domain.enums.GameState;

public record GameResponse(
    UUID id,
    GameState state,
    int score,
    int lastWordLength,
    RoundResponse currentRound
) {
}
