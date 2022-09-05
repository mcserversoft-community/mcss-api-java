package dev.le_app;

public enum ServerStatus {

    OFFLINE(0),
    ONLINE(1),
    RESTARTING(2),
    STARTING(3),
    STOPPING(4);

    private final int value;

    ServerStatus(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public static ServerStatus findByVal(int abbr){
        for(ServerStatus v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No ServerStatus with value " + abbr);
    }

}
