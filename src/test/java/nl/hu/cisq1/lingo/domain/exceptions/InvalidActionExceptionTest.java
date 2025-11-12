package nl.hu.cisq1.lingo.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidActionExceptionTest {

    @Test
    @DisplayName("should create exception with message")
    void shouldCreateExceptionWithMessage() {
        String message = "Invalid action performed";

        InvalidActionException exception = new InvalidActionException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("should be throwable")
    void shouldBeThrowable() {
        String message = "Cannot perform action in current state";

        assertThrows(InvalidActionException.class, () -> {
            throw new InvalidActionException(message);
        });
    }

    @Test
    @DisplayName("should preserve message when caught")
    void shouldPreserveMessageWhenCaught() {
        String expectedMessage = "Action not allowed";

        try {
            throw new InvalidActionException(expectedMessage);
        } catch (InvalidActionException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
}
