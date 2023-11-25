package com.mcserversoft.commons.structures;

public enum KeepOnline {
    NONE(0),
    ELEVATED(1),
    AGRESSIVE(2);
    
    private final int value;
    
    private KeepOnline(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static KeepOnline fromValue(int value) {
        for(KeepOnline keepOnline : KeepOnline.values()) {
            if(keepOnline.getValue() == value) {
                return keepOnline;
            }
        }
        return null;
    }
}
