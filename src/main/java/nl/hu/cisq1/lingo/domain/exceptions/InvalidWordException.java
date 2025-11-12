package nl.hu.cisq1.lingo.domain.exceptions;

public class InvalidWordException extends RuntimeException {
    public InvalidWordException(String message) {
        super(message);
    }
}
