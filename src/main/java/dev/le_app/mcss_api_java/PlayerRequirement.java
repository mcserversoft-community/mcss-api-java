package dev.le_app.mcss_api_java;

public enum PlayerRequirement {
    EMPTY(1),
    AT_LEAST_ONE(2),
    NONE(0);

    private final Integer value;

    PlayerRequirement(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

}
