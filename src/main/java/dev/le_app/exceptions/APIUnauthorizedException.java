package dev.le_app.exceptions;

public class APIUnauthorizedException extends Exception {
    public APIUnauthorizedException(String message) {
        super(message);
    }
}
