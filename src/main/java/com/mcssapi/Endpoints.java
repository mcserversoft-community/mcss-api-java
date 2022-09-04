package com.mcssapi;

public enum Endpoints {
    ROOT("https://{IP}/api/v1"),
    SERVERS("https://{IP}/api/v1/servers"),
    SERVER_COUNT("https://{IP}/api/v1/servers/count"),
    SERVER_COUNT_FILTER("https://{IP}/api/v1/servers/count?filter={FILTER}"),
    SERVER_COUNT_FILTER_SRVTYPE("https://{IP}/api/v1/servers/count?filter={FILTER}&serverTypeID={SRVTYPE}"),
    EXECUTE_SERVER_ACTION("https://{IP}/api/v1/servers/{SERVER_ID}/execute/action"),
    EXECUTE_SERVER_COMMAND("https://{IP}/api/v1/servers/{SERVER_ID}/execute/command"),
    EXECUTE_SERVER_COMMANDS("https://{IP}/api/v1/servers/{SERVER_ID}/execute/commands"),
    GET_CONSOLE("https://{IP}/api/v1/servers/{SERVER_ID}/console?AmountOfLines={AMOUNT_OF_LINES}"),
    GET_CONSOLE_FROM_BEGINNING("https://{IP}/api/v1/servers/{SERVER_ID}/console?AmountOfLines={AMOUNT_OF_LINES}" +
            "&takeFromBeginning={BEGINNING}"),
    GET_CONSOLE_REVERSED("https://{IP}/api/v1/servers/{SERVER_ID}/console?AmountOfLines={AMOUNT_OF_LINES}" +
            "&reversed={REVERSED}"),
    GET_CONSOLE_FROM_BEGINNING_REVERSED("https://{IP}/api/v1/servers/{SERVER_ID}/console?AmountOfLines={AMOUNT_OF_LINES}" +
            "&takeFromBeginning={BEGINNING}&reversed={REVERSED}"),

    IS_CONSOLE_OUTDATED("https://{IP}/api/v1/servers/{SERVER_ID}/console/outdated?lastLine={LAST_LINE}" +
            "&secondLastLine={SECOND_LAST_LINE}"),

    GET_SCHEDULER("https://{IP}/api/v1/servers/{SERVER_ID}/scheduler"),

    GET_TASK_AMOUNT_FILTER("https://{IP}/api/v1/servers/{SERVER_ID}/scheduler/tasks/count?filter={FILTER}"),

    GET_TASK_LIST("https://{IP}/api/v1/servers/{SERVER_ID}/scheduler/tasks"),
    CREATE_TASK("https://{IP}/api/v1/servers/{SERVER_ID}/scheduler/tasks"),

    GET_TASK("https://{IP}/api/v1/servers/{SERVER_ID}/scheduler/tasks/{TASK_ID}");


    private final String endpoint;

    Endpoints(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
