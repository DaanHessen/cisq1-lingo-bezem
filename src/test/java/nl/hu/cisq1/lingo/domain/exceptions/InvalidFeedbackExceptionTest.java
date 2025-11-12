package nl.hu.cisq1.lingo.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidFeedbackExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid feedback provided";

        InvalidFeedbackException exception = new InvalidFeedbackException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("should be throwable")
    void shouldBeThrowable() {
        String message = "Feedback does not match word length";

        assertThrows(InvalidFeedbackException.class, () -> {
            throw new InvalidFeedbackException(message);
        });
    }

    @Test
    @DisplayName("should preserve message when caught")
    void shouldPreserveMessageWhenCaught() {
        String expectedMessage = "Marks list is invalid";

        try {
            throw new InvalidFeedbackException(expectedMessage);
        } catch (InvalidFeedbackException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
