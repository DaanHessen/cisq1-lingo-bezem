package nl.hu.cisq1.lingo.application.exceptions;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) {
        super(message);
    }
}
