package dev.le_app.mcss_api_java;

public enum LastBackupState {

    NEVER_RUN(0),
    IN_PROGRESS(1),
    COMPLETED(2),
    FAILED(3),
    CANCELLED(4);

    private final int value;

    LastBackupState(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public static LastBackupState findByVal(int abbr){
        for(LastBackupState v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No LastBackupState with value " + abbr);
    }
}
