package nl.hu.cisq1.lingo.presentation.dto.response;

import java.util.List;
import nl.hu.cisq1.lingo.domain.enums.Mark;

public record AttemptResponse(
    String attempt,
    List<Mark> marks
) {
}
