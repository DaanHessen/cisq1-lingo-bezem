package nl.hu.cisq1.lingo.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.api.DisplayName;
import nl.hu.cisq1.lingo.domain.enums.Mark;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class HintTest {

    @Test
    @DisplayName("Initial hint is given correctly")
    void initialHintIsCorrect() {
        Hint res = Hint.initialFor("bruhh");

        assertEquals("b....", res.getValue());
    }

    @ParameterizedTest
    @MethodSource("edgeCases")
    @DisplayName("Hints are correctly updated after new guess")
    void hintUpdatesAfterNewGuess(Hint prev, String target, List<Mark> marks, String expected) {
        Hint res = Hint.from(prev, target, marks);

        assertEquals(expected, res.getValue());
    }

    static Stream<Arguments> edgeCases() {
        return Stream.of(
            Arguments.of(
                new Hint("A...."),
                "APPLE",
                List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT),
                "APPLE"
            ),
            Arguments.of(
                new Hint("A...."),
                "APPLE",
                List.of(Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.ABSENT),
                "A...."
            ),
            Arguments.of(
                new Hint("A...."),
                "APPLE",
                List.of(Mark.CORRECT, Mark.ABSENT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT),
                "A.P.."
            ),
            Arguments.of(
                new Hint("APP.."),
                "APPLE",
                List.of(Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT, Mark.CORRECT),
                "APPLE"
            ),
            Arguments.of(
                new Hint("A...."),
                "APPLE",
                List.of(Mark.ABSENT, Mark.ABSENT, Mark.ABSENT, Mark.CORRECT, Mark.CORRECT),
                "A..LE"
            ),
            Arguments.of(
                new Hint("B...."),
                "BRAKE",
                List.of(Mark.CORRECT, Mark.PRESENT, Mark.INVALID, Mark.ABSENT, Mark.CORRECT),
                "B...E"
            ),
            Arguments.of(
                new Hint("B....."),
                "BANANA",
                List.of(Mark.CORRECT, Mark.CORRECT, Mark.ABSENT, Mark.CORRECT, Mark.ABSENT, Mark.CORRECT),
                "BA.A.A"
            ),
            Arguments.of(
                new Hint("C......"),
                "COCONUT",
                List.of(Mark.CORRECT, Mark.ABSENT, Mark.CORRECT, Mark.CORRECT, Mark.ABSENT, Mark.ABSENT, Mark.CORRECT),
                "C.CO..T"
            )
        );
    }
}
