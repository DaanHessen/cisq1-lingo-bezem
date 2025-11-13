package nl.hu.cisq1.lingo.application;

import nl.hu.cisq1.lingo.domain.Feedback;
import nl.hu.cisq1.lingo.domain.Game;
import nl.hu.cisq1.lingo.domain.Round;
import nl.hu.cisq1.lingo.presentation.dto.response.AttemptResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.FeedbackResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.GameResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.GuessResponse;
import nl.hu.cisq1.lingo.presentation.dto.response.RoundResponse;

import java.util.List;

public class GameMapper {

    public static GameResponse toGameResponse(Game game) {
        RoundResponse roundResponse = null;
        if (game.getCurrentRound() != null) {
            roundResponse = toRoundResponse(game.getCurrentRound());
        }
        return new GameResponse(
            game.getId(),
            game.getState(),
            game.getScore(),
            game.getLastWordLength(),
            roundResponse
        );
    }

    public static RoundResponse toRoundResponse(Round round) {
        return new RoundResponse(
            round.getId(),
            round.getOutcome(),
            round.getCurrentHint().getValue(),
            round.getAttemptsRemaining(),
            round.getMaxAttempts(),
            toAttemptResponseList(round.getHistory()),
            round.getTargetWord().orElse(null)
        );
    }

    public static AttemptResponse toAttemptResponse(Feedback feedback) {
        return new AttemptResponse(
            feedback.getAttempt(),
            feedback.getMarks()
        );
    }

    public static List<AttemptResponse> toAttemptResponseList(List<Feedback> feedbackList) {
        return feedbackList.stream()
            .map(GameMapper::toAttemptResponse)
            .toList();
    }

    public static FeedbackResponse toFeedbackResponse(Feedback feedback) {
        return new FeedbackResponse(
            feedback.getMarks(),
            feedback.isWordGuessed(),
            feedback.isGuessValid()
        );

    }

    public static GuessResponse toGuessResponse(Feedback feedback, Game game) {
        FeedbackResponse fbr = toFeedbackResponse(feedback);
        GameResponse gmr = toGameResponse(game);
        
        return new GuessResponse(fbr, gmr);
    }
}
