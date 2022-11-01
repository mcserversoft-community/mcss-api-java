package dev.le_app.mcss_api_java;

/**
 * The state of the last backup.
 */
public enum LastBackupState {

    /** The backup has never run */
    NEVER_RUN(0),
    /** The backup is running */
    IN_PROGRESS(1),
    /** The backup has finished successfully */
    COMPLETED(2),
    /** The backup has failed */
    FAILED(3),
    /** The backup has been cancelled */
    CANCELLED(4);

    private final int value;

    LastBackupState(final int newValue) {
        value = newValue;
    }

    /**
     * Gets associated value for the last backup state - Used for API requests.
     * @return The last backup state as INT.
     */
    public int getValue() { return value; }

    /**
     * Gets the last backup state from the value.
     * @param abbr The value to get the last backup state from.
     * @return The last backup state.
     */
    public static LastBackupState findByVal(int abbr){
        for(LastBackupState v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No LastBackupState with value " + abbr);
    }
}
