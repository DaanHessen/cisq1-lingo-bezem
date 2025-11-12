package nl.hu.cisq1.lingo.domain;

import java.util.List;
import nl.hu.cisq1.lingo.domain.enums.Mark;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Embeddable
@Getter
@AllArgsConstructor
public class Hint {
    private String value;

    private String asString() {
        return null;
    }

    private int length() {
        return value.length();
    }

    public static Hint initialFor(String word) {
        return null;
    }

    public static Hint from(Hint prev, String target, List<Mark> marks) {
        return null;
    }
}
