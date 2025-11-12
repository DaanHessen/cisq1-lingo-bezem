package nl.hu.cisq1.lingo.domain;

import java.util.List;
import nl.hu.cisq1.lingo.domain.enums.Mark;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Hint {
    private String value;

    public static Hint initialFor(String word) {
        String first = String.valueOf(word.charAt(0));

        for (int i = 1; i < word.length(); i++) {
            first += '.';
        }
        return new Hint(first);
    }

    public static Hint from(Hint prev, String target, List<Mark> marks) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < target.length(); i++) {
            if (marks.get(i) == Mark.CORRECT) {
                builder.append(target.charAt(i));
            } else {
                builder.append(prev.getValue().charAt(i));
            }
        }
        return new Hint(builder.toString());
    }
}
