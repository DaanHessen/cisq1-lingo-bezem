package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.RoundOutcome;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Round {
    private String targetWord;
    private int maxAttempts;
    private int attemptsUsed;
    private RoundOutcome outcome;
    private List<Feedback> history;
    private Hint currentHint;

    private Feedback guess(String attempt, Dictionary dict) {
        return null;
    }

    private boolean isSolved() {
        return true;
    }

    private boolean isOver() {
        return false;
    }

    private int getAttemptsRemaining() {
        return maxAttempts - attemptsUsed;
    }

    private Hint getCurrentHint() {
        return currentHint;
    }

    private String revealAnswer() {
        return targetWord;
    }
}


