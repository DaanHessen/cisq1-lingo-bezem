package nl.hu.cisq1.lingo.presentation.dto.response;

import java.util.List;
import nl.hu.cisq1.lingo.domain.enums.Mark;

public record FeedbackResponse(
    List<Mark> marks,
    boolean correct,
    boolean valid
) {
}
