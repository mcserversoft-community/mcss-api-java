package dev.le_app.mcss_api_java.api.backups;

public enum Compression {
    HIGH(0),
    LOW(1),
    NONE(2);

    private int value;

    private Compression(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
