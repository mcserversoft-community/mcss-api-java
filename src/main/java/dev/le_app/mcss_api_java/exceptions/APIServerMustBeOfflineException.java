package dev.le_app.mcss_api_java.exceptions;

public class APIServerMustBeOfflineException extends Exception {
    /**
     * Constructs a new exception with the specified detail message.
     * @param message The detail message.
     */
    public APIServerMustBeOfflineException(String message) {
        super(message);
    }
}
