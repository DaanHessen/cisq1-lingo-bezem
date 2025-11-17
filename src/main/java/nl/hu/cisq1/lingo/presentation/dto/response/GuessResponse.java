package nl.hu.cisq1.lingo.presentation.dto.response;

public record GuessResponse(
    FeedbackResponse feedback,
    GameResponse gameState
) {
}
