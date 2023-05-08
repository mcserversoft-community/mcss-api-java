package dev.le_app.mcss_api_java;

/** List of endpoints for the API. */
public enum Endpoints {
    /** Root endpoint of the API */
    ROOT("https://{IP}/api/v2"),
    /** Endpoint for getting server list */
    SERVERS("https://{IP}/api/v2/servers"),
    /** Endpoint for getting the user list */
    USERS("https://{IP}/api/v2/users"),
    /** Endpoint for getting the user details */
    USER_INFO("https://{IP}/api/v2/users/{USER_ID}"),
    /** Endpoint for getting server details */
    SERVER_DETAILS("https://{IP}/api/v1/servers/{SERVER_ID}"),
    /** Endpoint for getting server count*/
    SERVER_COUNT("https://{IP}/api/v1/servers/count"),
    /** Execute action on multiple servers */
    MASS_EXECUTE_ACTION("https://{IP}/api/v2/servers/execute/action"),
    /** Execute command(s) on multiple servers */
    MASS_EXECUTE_COMMANDS("https://{IP}/api/v2/servers/execute/commands"),
    /** Endpoint for getting server count with filter */
    SERVER_COUNT_FILTER("https://{IP}/api/v1/servers/count?filter={FILTER}"),
    /** Endpoint for getting server list with filter and servertype */
    SERVER_COUNT_FILTER_SRVTYPE("https://{IP}/api/v1/servers/count?filter={FILTER}&serverTypeID={SRVTYPE}"),
    /** Endpoint for executing a server action */
    EXECUTE_SERVER_ACTION("https://{IP}/api/v1/servers/{SERVER_ID}/execute/action"),
    /** Endpoint for executing a server command */
    EXECUTE_SERVER_COMMAND("https://{IP}/api/v1/servers/{SERVER_ID}/execute/command"),
    /** Endpoint for executing multiple commands */
    EXECUTE_SERVER_COMMANDS("https://{IP}/api/v1/servers/{SERVER_ID}/execute/commands"),
    /** Endpoint for getting server backup list */
    SERVER_BACKUPS("https://{IP}/api/v1/servers/{SERVER_ID}/backups"),
    /** Endpoint for getting server backup details */
    SERVER_BACKUP_DETAILS("https://{IP}/api/v1/servers/{SERVER_ID}/backups/{BACKUP_ID}"),
    /** Endpoint for getting the server stats */
    GET_STATS("https://{IP}/api/v1/servers/{SERVER_ID}/stats"),
    /** Endpoint for getting the server icon */
    GET_ICON("https://{IP}/api/v1/servers/{SERVER_ID}/icon"),
    /** Endpoint for getting the server console */
    GET_CONSOLE("https://{IP}/api/v1/servers/{SERVER_ID}/console?AmountOfLines={AMOUNT_OF_LINES}"),
    /** Endpoint for getting the server console from beginning */
    GET_CONSOLE_FROM_BEGINNING("https://{IP}/api/v1/servers/{SERVER_ID}/console?AmountOfLines={AMOUNT_OF_LINES}" +
            "&takeFromBeginning={BEGINNING}"),
    /** Endpoint for getting the server console reversed */
    GET_CONSOLE_REVERSED("https://{IP}/api/v1/servers/{SERVER_ID}/console?AmountOfLines={AMOUNT_OF_LINES}" +
            "&reversed={REVERSED}"),
    /** Endpoint for getting the server console from beginning reversed */
    GET_CONSOLE_FROM_BEGINNING_REVERSED("https://{IP}/api/v1/servers/{SERVER_ID}/console?AmountOfLines={AMOUNT_OF_LINES}" +
            "&takeFromBeginning={BEGINNING}&reversed={REVERSED}"),

    /** Endpoint for checking if the console is outdated */
    IS_CONSOLE_OUTDATED("https://{IP}/api/v1/servers/{SERVER_ID}/console/outdated?lastLine={LAST_LINE}" +
            "&secondLastLine={SECOND_LAST_LINE}"),

    /** Endpoint for getting the server scheduler */
    GET_SCHEDULER("https://{IP}/api/v1/servers/{SERVER_ID}/scheduler"),

    /** Endpoint for getting the task amount filtered */
    GET_TASK_AMOUNT_FILTER("https://{IP}/api/v1/servers/{SERVER_ID}/scheduler/tasks/count?filter={FILTER}"),

    /** Endpoint for getting the task list */
    GET_TASK_LIST("https://{IP}/api/v1/servers/{SERVER_ID}/scheduler/tasks"),
    /** Endpoint for creating a task */
    CREATE_TASK("https://{IP}/api/v1/servers/{SERVER_ID}/scheduler/tasks"),

    /** Endpoint for getting the task details */
    GET_TASK("https://{IP}/api/v1/servers/{SERVER_ID}/scheduler/tasks/{TASK_ID}"),
    WIPE_SESSIONS("https://{IP}/api/v2/users/wipe/sessions");


    private final String endpoint;

    Endpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Returns the endpoint as string.
     * @return The endpoint as string.
     */
    public String getEndpoint() {
        return endpoint;
    }
}
