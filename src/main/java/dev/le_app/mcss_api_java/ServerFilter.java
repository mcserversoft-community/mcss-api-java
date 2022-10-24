package dev.le_app.mcss_api_java;

/**
 * A list of the possible filters for the server list.
 */
public enum ServerFilter {

    /** No filter */
    NONE(0),
    /** Filter for servers that are online */
    ONLINE(1),
    /** Filter for servers that are offline */
    OFFLINE(2),
    /** Filter for servers that have the same ServerType as a provided ServerID */
    FILTER(3);

    private final int value;

    ServerFilter(final int newValue) {
        value = newValue;
    }

    /**
     * Gets associated value for the server filter - Used for API requests.
     * @return The server filter as INT.
     */
    public int getValue() { return value; }

    /**
     * Gets associated value for the server filter - Used for API requests.
     * @return The server filter as STRING.
     */
    public String getValueStr() { return String.valueOf(value); }
}
