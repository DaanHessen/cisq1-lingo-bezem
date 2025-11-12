package nl.hu.cisq1.lingo.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidWordExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid word provided";

        InvalidWordException exception = new InvalidWordException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("should be throwable")
    void shouldBeThrowable() {
        String message = "Word length does not match";

        assertThrows(InvalidWordException.class, () -> {
            throw new InvalidWordException(message);
        });
    }

    @Test
    @DisplayName("should preserve message when caught")
    void shouldPreserveMessageWhenCaught() {
        String expectedMessage = "Word contains invalid characters";

        try {
            throw new InvalidWordException(expectedMessage);
        } catch (InvalidWordException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
