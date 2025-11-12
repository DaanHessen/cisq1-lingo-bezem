package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.Mark;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "feedback")
@Getter
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private String attempt;
    
    @ElementCollection
    private List<Mark> marks;

    private Feedback(String attempt, List<Mark> marks) {
        this.attempt = attempt;
        this.marks = marks;
    }

    protected boolean isWordGuessed() {
        if (marks == null) {
            return false;
        }
        for (Mark mark : marks) {
            if (mark != Mark.CORRECT) {
                return false;
            }
        }
        return true;
    }

    protected boolean isGuessValid() {
        if (marks == null) {
            return false;
        }
        return !marks.contains(Mark.INVALID);
    }

    private Hint applyTo(Hint prev, String target) {
        return Hint.from(prev, target, this.marks);
    }

    public static Feedback of(String attempt, List<Mark> marks) {
        return new Feedback(attempt, marks);
    }
    public static Feedback correct (String word) {
        int length = word.length();
        List<Mark> marks = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            marks.add(Mark.CORRECT);
        }
        return new Feedback(word, marks);
    }

    public static Feedback invalid(String word) {
        int length = word.length();
        List<Mark> marks = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            marks.add(Mark.INVALID);
        }
        return new Feedback(word, marks);
    }

    public static Feedback generate(String target, String attempt, Dictionary dict) {
        if (!dict.exists(attempt) || target.length() != attempt.length()) {
            return Feedback.invalid(attempt);
        }

        int length = target.length();
        boolean[] used = new boolean[target.length()];
        List<Mark> marks = new ArrayList<>();
        
        for (int i = 0; i < length; i++) {
            marks.add(Mark.ABSENT);
        }

        for (int i = 0; i < length; i++) {
            if (attempt.charAt(i) == target.charAt(i)) {
                used[i] = true;
                marks.set(i, Mark.CORRECT);
            }
        }

        for (int i = 0; i < length; i++) {
            if (marks.get(i) == Mark.CORRECT) {
                continue;
            }

            for (int j = 0; j < length; j++) {
                if (attempt.charAt(i) == target.charAt(j) && !used[j]) {
                    marks.set(i, Mark.PRESENT);
                    used[j] = true;
                    break;
                }
            }
        }

        return new Feedback(attempt, marks);
    }
}
