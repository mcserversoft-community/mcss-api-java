package dev.le_app.mcss_api_java;

/**
 * List of all possible types of a task.
 */
public enum TaskType {

    /** NONE - INVALID - RESERVED */
    NONE(0),
    /** Fixed time (ex Every Day at 9PM) */
    FIXED_TIME(1),
    /** Interval (Every X minutes) */
    INTERVAL(2),
    /** Timeless (Manually run) */
    TIMELESS(3);

    private final int value;

    TaskType(final int newValue) {
        value = newValue;
    }

    /**
     * Gets associated value for the task type - Used for API requests.
     * @return The task type as INT.
     */
    public int getValue() { return value; }

    /**
     * Gets the task type from the value.
     * @param abbr The value to get the task type from.
     * @return The task type.
     */
    public static TaskType findByVal(int abbr){
        for(TaskType v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No TaskType with value " + abbr);
    }
}
