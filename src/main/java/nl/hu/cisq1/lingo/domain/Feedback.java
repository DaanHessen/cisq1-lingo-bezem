package nl.hu.cisq1.lingo.domain;

import nl.hu.cisq1.lingo.domain.enums.Mark;

import java.util.List;

import lombok.Getter;

@Getter
public class Feedback {
    private String attempt;
    private List<Mark> marks;

    private boolean isWordGuessed() {
        return true;
    }

    private boolean isGuessValid() {
        return true;
    }

    private Hint applyTo(Hint prev, String target) {
        return null;
    }

    public static Feedback of (String attempt, List<Mark> marks) {
        return new Feedback(attempt, marks);
    }

    public static Feedback correct (String word) {
        return null;
    }

    public static Feedback invalid(String word) {
        return null;
    }

    public static Feedback generate (String target, String attempt, Dictionary dict) {
        return null;
    }
}
