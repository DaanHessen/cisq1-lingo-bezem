package nl.hu.cisq1.lingo.presentation.dto.response;

import java.util.List;
import java.util.UUID;
import nl.hu.cisq1.lingo.domain.enums.RoundOutcome;

public record RoundResponse(
    UUID id,
    RoundOutcome outcome,
    String hint,
    int attemptsRemaining,
    int maxAttempts,
    List<AttemptResponse> attempts,
    String targetWord
) {
}