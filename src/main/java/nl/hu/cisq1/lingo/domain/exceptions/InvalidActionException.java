package nl.hu.cisq1.lingo.domain.exceptions;

public class InvalidActionException extends RuntimeException {
    public InvalidActionException(String message) {
        super(message);
    }
}
