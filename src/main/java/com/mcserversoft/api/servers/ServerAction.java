package com.mcserversoft.api.servers;

public enum ServerAction {
    INVALID_OR_EMPTY(0),
    STOP(1),
    START(2),
    KILL(3),
    RESTART(4);
    
    private final int value;
    
    private ServerAction(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static ServerAction fromValue(int value) {
        for(ServerAction serverAction : ServerAction.values()) {
            if(serverAction.getValue() == value) {
                return serverAction;
            }
        }
        return null;
    }
}
