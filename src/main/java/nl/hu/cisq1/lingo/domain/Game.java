package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.GameState;
import nl.hu.cisq1.lingo.domain.enums.RoundOutcome;
import nl.hu.cisq1.lingo.domain.exceptions.InvalidActionException;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Entity
@Table(name = "games")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    static final int MAX_ATTEMPTS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private int score;
    private GameState state;

    @OneToOne(cascade = CascadeType.ALL)
    private Round currentRound;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Round> pastRounds;

    private int lastWordLength;
    
    @Transient
    private Dictionary dict;

    protected void startGame() {
        if (this.state != GameState.NEW) {
            throw new InvalidActionException("🤔🤔 you're already in a game!");
        } 

        this.state = GameState.NEW;
        this.score = 0;
        this.lastWordLength = 0;
        this.pastRounds = new ArrayList<>();

        this.startNewRound();
    }

    protected void startNewRound() {
        if (this.state == GameState.ELIMINATED) {
            throw new InvalidActionException("🤔🤔 you are dead..");
        }
        
        if (this.state == GameState.IN_ROUND) {
            throw new InvalidActionException("🤔🤔 one round at once isn't enough for you??");
        }

        int nextWordLength = switch (this.lastWordLength) {
            case 0 -> 5;
            case 5 -> 6;
            case 6 -> 7;
            case 7 -> 5;
            default -> throw new IllegalStateException("🤨🤨 " + this.lastWordLength + " isn't a valid length!");
        };

        String target = dict.randomWord(nextWordLength);
        List<Feedback> history = new ArrayList<>();
        Hint hint = Hint.initialFor(target);

        this.currentRound = new Round(target, MAX_ATTEMPTS,  0, history, RoundOutcome.IN_PROGRESS, hint);
        this.state = GameState.IN_ROUND;
        this.lastWordLength = nextWordLength;
    }

    protected Feedback guess(String attempt) {
        if (this.state != GameState.IN_ROUND) {
            throw new InvalidActionException("🤔🤔 why are you trying to guess without being in a round????");
        }

        Feedback feedback = currentRound.guess(attempt, dict);

        if (currentRound.getOutcome() == RoundOutcome.WON) {
            this.score = score + (5 * (currentRound.getAttemptsRemaining() + 1));
            this.pastRounds.add(currentRound);
            this.currentRound = null;
            this.state = GameState.WAITING_FOR_ROUND;
        } else if (currentRound.getOutcome() == RoundOutcome.LOST) {
            this.pastRounds.add(currentRound);
            this.currentRound = null;
            this.state = GameState.ELIMINATED;
        }

        return feedback;
    }

    protected void forfeit() {
        if (this.state != GameState.IN_ROUND) {
            throw new InvalidActionException("you cannot forfeit when you're not even playing, fool.");
        }

        this.pastRounds.add(currentRound);
        this.currentRound = null;
        this.state = GameState.ELIMINATED;
    }

    protected int getScore() {
        return score;
    }

    protected Hint getCurrentHint() {
        if (this.state != GameState.IN_ROUND) {
            throw new InvalidActionException("??? there is no hint if there is no game??");
        }
        return currentRound.getCurrentHint();
    }

    protected int getAttemptsRemaining() {
        if (this.state != GameState.IN_ROUND) {
            throw new InvalidActionException("I'd say your attempts remaining are '0' but you're not even playing.");
        }
        return currentRound.getAttemptsRemaining();
    }

    protected List<Round> getPastRounds() {
        return pastRounds;
    }
}
