package dev.le_app.mcss_api_java;

/**
 * List of all possible types of a task job.
 */
public enum TaskJobType {
    /** Task job is a Server Action (Ex restart) */
    SERVER_ACTION,
    /** Task job is a Server Command */
    RUN_COMMANDS,
    /** Task job is to start a backup */
    START_BACKUP;
}
