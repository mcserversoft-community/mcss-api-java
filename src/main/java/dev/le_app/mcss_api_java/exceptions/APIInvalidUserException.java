package dev.le_app.mcss_api_java.exceptions;

public class APIInvalidUserException extends Exception{

    /**
     * Constructs a new exception with the specified detail message.
     * @param message The detail message.
     */
    public APIInvalidUserException(String message) {
        super(message);
    }
}
