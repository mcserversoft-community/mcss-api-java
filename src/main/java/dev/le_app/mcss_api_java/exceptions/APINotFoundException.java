package dev.le_app.mcss_api_java.exceptions;

/**
 * Thrown when the task details are invalid.
 */
public class APINotFoundException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.
     * @param message The detail message.
     */
    public APINotFoundException(String message) {
        super(message);
    }
}
