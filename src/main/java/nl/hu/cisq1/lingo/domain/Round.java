package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.RoundOutcome;
import nl.hu.cisq1.lingo.domain.exceptions.InvalidActionException;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "rounds")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String targetWord;
    private int maxAttempts;
    private int attemptsUsed;
    private RoundOutcome outcome;

    public Round(String targetWord, int maxAttempts, int attemptsUsed, List<Feedback> history, RoundOutcome outcome, Hint currentHint) {
        this.id = null;
        this.targetWord = targetWord;
        this.maxAttempts = maxAttempts;
        this.attemptsUsed = attemptsUsed;
        this.outcome = outcome;
        this.history = history;
        this.currentHint = currentHint;
    }

    @OneToMany(cascade = CascadeType.ALL)
    private List<Feedback> history;

    @Embedded
    private Hint currentHint;

    protected Feedback guess(String attempt, Dictionary dict) {
        if (isOver()) {
            throw new InvalidActionException("🤔cannot guess after round is over!");
        }

        Feedback feedback = Feedback.generate(targetWord, attempt, dict);
        
        if (feedback.isGuessValid()) {
            this.currentHint = feedback.applyTo(currentHint, this.targetWord);
        }

        this.history.add(feedback);
        this.attemptsUsed = this.attemptsUsed + 1;

        if (feedback.isWordGuessed()) {
            this.outcome = RoundOutcome.WON;
        }

        if (this.attemptsUsed >= this.maxAttempts && !this.isSolved()) {
            this.outcome = RoundOutcome.LOST;
        }

        return feedback;
    }

    protected boolean isSolved() {
        if (outcome.equals(RoundOutcome.WON)) {
            return true;
        } else {
            return false;
        }
    }

    protected boolean isOver() {
        return (outcome != RoundOutcome.IN_PROGRESS);
    }

    protected int getAttemptsRemaining() {
        return maxAttempts - attemptsUsed;
    }

    protected Hint getCurrentHint() {
        return currentHint;
    }

    protected String revealAnswer() {
        return targetWord;
    }
}


