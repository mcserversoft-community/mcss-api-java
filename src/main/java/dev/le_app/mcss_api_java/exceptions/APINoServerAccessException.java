package dev.le_app.mcss_api_java.exceptions;

/**
 * Thrown if the API token doesn't have access to a specified server
 */
public class APINoServerAccessException extends Exception {

    /**
     * Thrown if the API token doesn't have access to a specified server
     * @param message The message to be displayed
     */
    public APINoServerAccessException(String message) {
        super(message);
    }
}
