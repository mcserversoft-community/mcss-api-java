package dev.le_app.mcss_api_java.api.servers;

public enum ServerCountFilter {
    NONE(0),
    ONLINE(1),
    OFFLINE(2),
    BYSERVERTYPE(3);

    private final int value;

    private ServerCountFilter(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
