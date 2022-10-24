package dev.le_app.mcss_api_java;

/**
 * List of all possible statuses of a server.
 */
public enum ServerStatus {

    /** The server is offline */
    OFFLINE(0),
    /** The server is online */
    ONLINE(1),
    /** The server is restarting */
    RESTARTING(2),
    /** The server is starting */
    STARTING(3),
    /** The server is stopping */
    STOPPING(4);

    private final int value;

    ServerStatus(final int newValue) {
        value = newValue;
    }

    /**
     * Gets associated value for the server status - Used for API requests.
     * @return The server status as INT.
     */
    public int getValue() { return value; }

    /**
     * Gets the server status from the value.
     * @param abbr The value to get the server status from.
     * @return The server status.
     */
    public static ServerStatus findByVal(int abbr){
        for(ServerStatus v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No ServerStatus with value " + abbr);
    }

}
