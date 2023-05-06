package dev.le_app.mcss_api_java;

/** List of possible errors of the API */
public enum Errors {

    //General Purpose

    /** Unauthorized */
    UNAUTHORIZED("API Token is invalid or expired. (ERR_401)"),
    /** Not Found */
    NOT_FOUND("TaskID / ServerID / BackupID invalid. (ERR_404)"),
    /** Version not supported */
    VERSION_MISMATCH("API version mismatch. Expected version: {VERSION}, got {GOT}"),
    /** Error code was not recognized */
    NOT_RECOGNIZED("Error code not recognized. (ERR_UNKNOWN): "),
    /** API doesn't have access to this server */
    NO_SERVER_ACCESS("You do not have access to this server. (ERR_403)"),

    /** Filter error */
    ID_FILTER_ERROR("The serverTypeID is only required if the filter is set to FILTER"),

    //Tasks - Job Messages
    /** Task details are invalid */
    INVALID_TASK_DETAILS("Task details are invalid. (ERR_409)"),
    /** Task name contains special chars */
    NAME_SPECIAL_CHAR("Task name cannot contain special characters."),

    /** Task is already deleted */
    TASK_ALREADY_DELETED("Task has already been deleted."),
    /** Task has been deleted */
    TASK_DELETED("Cannot edit/execute a deleted task."),
    /** Task Deleted - JOB */
    JOB_DELETED("Cannot get Job of a deleted task."),
    /** Task Deleted - ENABLE */
    ENABLE_DELETED("Cannot enable a deleted task."),
    /** Task Deleted - DISABLE */
    DISABLE_DELETED("Cannot disable a deleted task."),
    /** Task Deleted - REPEAT */
    REPEAT_DELETED("Cannot repeat a deleted task."),
    /** Task Deleted - RUN */
    RUN_DELETED("Cannot run a deleted task."),
    /** Task Deleted - CHANGE NAME */
    CHANGE_NAME_DELETED("Cannot change name of a deleted task."),
    /** Task Deleted - TIMING */
    INTERVAL_DELETED("Cannot get/set interval of a deleted task."),
    /** Task Deleted - TIMING */
    TIME_DELETED("Cannot get/set time of a deleted task."),

    /** Task doesn't have a valid job type */
    INVALID_JOB_TYPE("Task has invalid Job type."),
    /** Task has no timing information */
    NO_TIMING_INFORMATION("Task has no timing information."),
    /** Task time was invalid */
    COULD_NOT_PARSE_TIME("Could not parse time to LocalTime."),

    /** Cannot repeat a timeless task */
    REPEAT_TIMELESS("Timeless tasks cannot be repeated."),
    /** Cannot get time for a timeless task */
    TIME_TIMELESS("Timeless tasks don't have time details."),
    /** Cannot get interval for a timeless task */
    INTERVAL_TIMELESS("Timeless tasks don't have an interval."),
    /** Cannot enable a timeless task */
    ENABLE_TIMELESS("Timeless tasks cannot be enabled."),
    /** Cannot disable a timeless task */
    DISABLE_TIMELESS("Timeless tasks cannot be disabled."),

    /** Cannot get time of interval task */
    TIME_INTERVAL("Interval tasks don't have time details."),
    /** Interval must be more than 0 */
    INTERVAL_GREATER_0("Interval must be greater than 0."),

    /** Cannot get interval of time task */
    INTERVAL_FIXED_TIME("Fixed time tasks don't have an interval."),

    /** No Commands found for task */
    COMMANDS_NOT_FOUND("No commands found for this task."),
    /** No Commands given for task */
    COMMANDS_NOT_GIVEN("No commands were supplied for this task."),

    /** Method not available for this job type */
    METHOD_NOT_SUPPORTED("This method is not supported for this job type."),
    SERVER_MUST_BE_OFFLINE("Server must be offline to perform this action."),

    /** Task doesn't have an action */
    ACTION_NOT_FOUND("No action found for this task."),

    /** No servers provided to execute on */
    NO_SERVERS("No servers provided to execute the task/commands on."),
    /** Unable to execute an invalid action */
    INVALID_ACTION("Cannot execute an invalid action."),
    /** No commands provided */
    NO_COMMANDS("No commands provided."),


    /** User is not administrator */
    NOT_ADMIN("API token provided is not an admin token."),
    /** The MCSS returned an error 500 */
    API_ERROR("The MCSS API reutned a server-side error. Please try again later. (ERR_500)");


    private final String message;

    Errors(String message) {
        this.message = message;
    }

    /**
     * Gets the message of the error.
     * @return The message of the error.
     */
    public String getMessage() {
        return message;
    }

}
