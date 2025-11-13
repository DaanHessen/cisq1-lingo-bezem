package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.GameState;
import nl.hu.cisq1.lingo.domain.enums.RoundOutcome;
import nl.hu.cisq1.lingo.domain.exceptions.InvalidActionException;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Random;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.*;
import lombok.Getter;

@Entity
@Table(name = "games")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class Game {
    static final int MAX_ATTEMPTS = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String username;
    private int score;
    private GameState state = GameState.NEW;

    @OneToOne(cascade = CascadeType.ALL)
    private Round currentRound;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Round> pastRounds;

    private int lastWordLength;

    public void setUsername(String username) {
        this.username = username;
    }

    public void startGame(Dictionary dictionary) {
        if (this.state != GameState.NEW) {
            throw new InvalidActionException("game already started");
        } 

        this.state = GameState.NEW;
        this.score = 0;
        this.lastWordLength = 0;
        this.pastRounds = new ArrayList<>();

        log.info("started a new game");
        this.startNewRound(dictionary);
    }

    public void startNewRound(Dictionary dictionary) {
        startNewRound(dictionary, false);
    }

    public void startNewRound(Dictionary dictionary, boolean randomLength) {
        if (this.state == GameState.ELIMINATED) {
            throw new InvalidActionException("cannot start round, game is over");
        }
        
        if (this.state == GameState.IN_ROUND) {
            throw new InvalidActionException("round already in progress");
        }

        if (this.currentRound != null && this.currentRound.isOver()) {
            this.pastRounds.add(this.currentRound);
        }

        int nextWordLength;
        if (randomLength) {
            int[] validLengths = {5, 6, 7};
            nextWordLength = validLengths[new Random().nextInt(validLengths.length)];
        } else {
            nextWordLength = switch (this.lastWordLength) {
                case 0 -> 5;
                case 5 -> 6;
                case 6 -> 7;
                case 7 -> 5;
                default -> throw new IllegalStateException("🤨🤨 " + this.lastWordLength + " isn't a valid length");
            };
        }

        String target = dictionary.randomWord(nextWordLength);
        List<Feedback> history = new ArrayList<>();
        Hint hint = Hint.initialFor(target);

        log.info("word to guess for this round: " + target);

        this.currentRound = new Round(target, MAX_ATTEMPTS,  0, history, RoundOutcome.IN_PROGRESS, hint);
        this.state = GameState.IN_ROUND;
        this.lastWordLength = nextWordLength;
    }

    public Feedback guess(String attempt, Dictionary dictionary) {
        if (this.state != GameState.IN_ROUND) {
            throw new InvalidActionException("no active round");
        }

        Feedback feedback = currentRound.guess(attempt, dictionary);

        if (currentRound.getOutcome() == RoundOutcome.WON) {
            this.score = score + (5 * (currentRound.getAttemptsRemaining() + 1));
            this.state = GameState.WAITING_FOR_ROUND;
        } else if (currentRound.getOutcome() == RoundOutcome.LOST) {
            this.state = GameState.ELIMINATED;
        }

        return feedback;
    }

    public void forfeit() {
        if (this.state != GameState.IN_ROUND) {
            throw new InvalidActionException("cannot forfeit when not in round");
        }

        this.state = GameState.ELIMINATED;
    }

    protected Hint getCurrentHint() {
        if (this.state != GameState.IN_ROUND) {
            throw new InvalidActionException("no active round");
        }
        return currentRound.getCurrentHint();
    }

    protected int getAttemptsRemaining() {
        if (this.state != GameState.IN_ROUND) {
            throw new InvalidActionException("no active round");
        }
        return currentRound.getAttemptsRemaining();
    }

    protected List<Round> getPastRounds() {
        return pastRounds;
    }
}
