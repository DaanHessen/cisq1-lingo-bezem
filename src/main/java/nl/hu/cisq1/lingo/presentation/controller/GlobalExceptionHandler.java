package nl.hu.cisq1.lingo.presentation.controller;

import nl.hu.cisq1.lingo.application.exceptions.GameNotFoundException;
import nl.hu.cisq1.lingo.domain.exceptions.InvalidActionException;
import nl.hu.cisq1.lingo.domain.exceptions.InvalidFeedbackException;
import nl.hu.cisq1.lingo.domain.exceptions.InvalidWordException;
import nl.hu.cisq1.lingo.words.domain.exception.WordLengthNotSupportedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<Object> handleGameNotFoundException(
            GameNotFoundException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(InvalidActionException.class)
    public ResponseEntity<Object> handleInvalidActionException(
            InvalidActionException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidWordException.class)
    public ResponseEntity<Object> handleInvalidWordException(
            InvalidWordException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidFeedbackException.class)
    public ResponseEntity<Object> handleInvalidFeedbackException(
            InvalidFeedbackException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(WordLengthNotSupportedException.class)
    public ResponseEntity<Object> handleWordLengthNotSupportedException(
            WordLengthNotSupportedException ex, WebRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(
            Exception ex, WebRequest request) {
        return buildErrorResponse(
            "An unexpected error occurred: " + ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR,
            request
        );
    }

    private ResponseEntity<Object> buildErrorResponse(
            String message, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }
}
