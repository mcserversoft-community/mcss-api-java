package com.mcssapi;

public enum ServerStatus {

    NONE(0),
    ONLINE(1),
    OFFLINE(2),
    FILTER(3);

    private final int value;

    ServerStatus(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
