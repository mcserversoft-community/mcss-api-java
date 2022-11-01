package dev.le_app.mcss_api_java;

/**
 * The player requirement for a task to be executed
 */
public enum PlayerRequirement {
    /** The taks will be executed only if the server is empty */
    EMPTY(1),
    /** The task will be executed only if the server is not empty */
    AT_LEAST_ONE(2),
    /** The task will be executed regardless of the player count */
    NONE(0);

    private final Integer value;

    PlayerRequirement(Integer value) {
        this.value = value;
    }

    /**
     * Gets associated value for the player requirement - Used for API requests.
     * @return The player requirement as INT.
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Gets the player requirement from the value.
     * @param abbr The value to get the player requirement from.
     * @return The player requirement.
     */
    public static PlayerRequirement findByVal(Integer abbr){
        for(PlayerRequirement v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No PlayerRequirement with value " + abbr);
    }

}
