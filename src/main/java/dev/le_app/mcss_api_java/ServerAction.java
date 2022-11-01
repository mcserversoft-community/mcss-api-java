package dev.le_app.mcss_api_java;

/**
 * Lists of possible server actions.
 */
public enum ServerAction {
    /** Invalid - reserved */
    INVALID(0),
    /** Stop the server */
    STOP(1),
    /** Start the server */
    START(2),
    /** Kill the server */
    KILL(3),
    /** Restart the server */
    RESTART(4);

    private final int value;

    ServerAction(final int newValue) {
        value = newValue;
    }

    /**
     * Gets associated value for the server action - Used for API requests.
     * @return The server action as INT.
     */
    public int getValue() { return value; }
}
