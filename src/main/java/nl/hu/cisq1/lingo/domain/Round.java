package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.RoundOutcome;

import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String targetWord;
    private int maxAttempts;
    private int attemptsUsed;
    private RoundOutcome outcome;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Feedback> history;

    @Embedded
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


