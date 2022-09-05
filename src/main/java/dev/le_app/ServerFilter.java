package dev.le_app;

public enum ServerFilter {

    NONE(0),
    ONLINE(1),
    OFFLINE(2),
    FILTER(3);

    private final int value;

    ServerFilter(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public String getValueStr() { return String.valueOf(value); }
}
