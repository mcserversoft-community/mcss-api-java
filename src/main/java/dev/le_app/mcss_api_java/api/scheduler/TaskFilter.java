package dev.le_app.mcss_api_java.api.scheduler;

public enum TaskFilter {
    NONE(0),
    FIXED_TIME(1),
    INTERVAL(2),
    TIMELESS(3);

    private final int value;

    private TaskFilter(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
