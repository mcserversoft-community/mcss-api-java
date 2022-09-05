package dev.le_app;

public enum KeepOnline {

    NONE(0),
    ELEVATED(1),
    AGGRESSIVE(2);

    private final int value;

    KeepOnline(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public static KeepOnline findByVal(int abbr){
        for(KeepOnline v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No KeepOnline with value " + abbr);
    }

}
