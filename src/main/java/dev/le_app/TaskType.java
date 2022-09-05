package dev.le_app;

public enum TaskType {

    NONE(0),
    FIXED_TIME(1),
    INTERVAL(2),
    TIMELESS(3);

    private final int value;

    TaskType(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }

    public static TaskType findByVal(int abbr){
        for(TaskType v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No TaskType with value " + abbr);
    }
}
