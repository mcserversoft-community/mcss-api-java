package dev.le_app.exceptions;

public class APIVersionMismatchException extends Exception {
    public APIVersionMismatchException(String message) {
        super(message);
    }
}
