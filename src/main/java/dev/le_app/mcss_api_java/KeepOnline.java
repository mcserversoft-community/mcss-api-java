package dev.le_app.mcss_api_java;

/**
 * The keep online behaviour of a server
 */
public enum KeepOnline {

    /** Server can shutdown / crash */
    NONE(0),
    /** Server will be restarted if it crashes */
    ELEVATED(1),
    /** Server will be restarted if it crashes or is shutdown */
    AGGRESSIVE(2);

    private final int value;

    KeepOnline(final int newValue) {
        value = newValue;
    }

    /**
     * Gets associated value for the keep online behaviour - Used for API requests.
     * @return The keep online behaviour as INT.
     */
    public int getValue() { return value; }

    /**
     * Gets the keep online behaviour from the value.
     * @param abbr The value to get the keep online behaviour from.
     * @return The keep online behaviour.
     */
    public static KeepOnline findByVal(int abbr){
        for(KeepOnline v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No KeepOnline with value " + abbr);
    }

}
