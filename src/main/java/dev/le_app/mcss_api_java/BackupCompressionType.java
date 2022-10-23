package dev.le_app.mcss_api_java;

public enum BackupCompressionType {
    HIGH(0),
    LOW(1),
    NONE(2);

    private final int compressionType;

    BackupCompressionType(int compressionType) {
        this.compressionType = compressionType;
    }

    public int getValue() {
        return compressionType;
    }

    public static BackupCompressionType findByVal(int abbr){
        for(BackupCompressionType v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No TaskType with value " + abbr);
    }
}
