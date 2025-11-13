package nl.hu.cisq1.lingo.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GuessRequest(
    @NotBlank(message = "attempt required")
    String attempt
) {
}
