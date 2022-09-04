package com.mcssapi;

public enum Errors {

    //General Purpose
    UNAUTHORIZED("API Token is invalid or expired. (ERR_401)"),
    NOT_FOUND("TaskID / ServerID invalid. (ERR_404)"),
    VERSION_MISMATCH("API version mismatch. Expected version: {VERSION}, got {GOT}"),
    NOT_RECOGNIZED("Error code not recognized. (ERR_UNKNOWN): "),


    //Tasks - Job Messages
    INVALID_TASK_DETAILS("Task details are invalid. (ERR_409)"),
    INVALID_TASK_OPERATION("Task operation is invalid."),
    NAME_SPECIAL_CHAR("Task name cannot contain special characters."),

    TASK_ALREADY_DELETED("Task has already been deleted."),
    TASK_DELETED("Cannot edit/execute a deleted task."),
    JOB_DELETED("Cannot get Job of a deleted task."),
    ENABLE_DELETED("Cannot enable a deleted task."),
    DISABLE_DELETED("Cannot disable a deleted task."),
    REPEAT_DELETED("Cannot repeat a deleted task."),
    RUN_DELETED("Cannot run a deleted task."),
    CHANGE_NAME_DELETED("Cannot change name of a deleted task."),
    INTERVAL_DELETED("Cannot get/set interval of a deleted task."),
    TIME_DELETED("Cannot get/set time of a deleted task."),

    INVALID_JOB_TYPE("Task has invalid Job type."),
    NO_TIMING_INFORMATION("Task has no timing information."),
    COULD_NOT_PARSE_TIME("Could not parse time to LocalTime."),

    REPEAT_TIMELESS("Timeless tasks cannot be repeated."),
    TIME_TIMELESS("Timeless tasks don't have time details."),
    INTERVAL_TIMELESS("Timeless tasks don't have an interval."),
    ENABLE_TIMELESS("Timeless tasks cannot be enabled."),
    DISABLE_TIMELESS("Timeless tasks cannot be disabled."),

    TIME_INTERVAL("Interval tasks don't have time details."),
    INTERVAL_GREATER_0("Interval must be greater than 0."),

    INTERVAL_FIXED_TIME("Fixed time tasks don't have an interval.");

    private final String message;

    Errors(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
