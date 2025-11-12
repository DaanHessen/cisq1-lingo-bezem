package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.GameState;

import java.util.List;
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
import lombok.Getter;

@Entity
@Table(name = "games")
@Getter
@AllArgsConstructor
public class Game {
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
    private Dictionary dictionary;

    private void startGame() {
    }

    private void startNewRound() {
    }

    private Feedback guess(String attempt) {
        return null;
    }

    private void forfeit() {
    }

    private int getScore() {
        return score;
    }

    private Hint getCurrentHint() {
        return null;
    }

    private int getAttemptsRemaining() {
        return 0;
    }

    private List<Round> getPastRounds() {
        return null;
    }
}
