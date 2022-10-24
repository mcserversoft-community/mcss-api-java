package dev.le_app.mcss_api_java;

/**
 * The type of compression used for backups.
 */
public enum BackupCompressionType {
    /**
     * The backup is very compressed.
     */
    HIGH(0),
    /**
     * The backup is compressed normally.
     */
    LOW(1),
    /**
     * The backup is not compressed.
     */
    NONE(2);

    private final int compressionType;

    BackupCompressionType(int compressionType) {
        this.compressionType = compressionType;
    }

    /**
     * Gets associated valye for the compression type - Used for API requests.
     * @return The compression type as INT.
     */
    public int getValue() {
        return compressionType;
    }

    /**
     * Gets the compression type from the value.
     * @param abbr The value to get the compression type from.
     * @return The compression type.
     */
    public static BackupCompressionType findByVal(int abbr){
        for(BackupCompressionType v : values()){
            if( v.getValue() == abbr ){
                return v;
            }
        }
        throw new IllegalArgumentException("No TaskType with value " + abbr);
    }
}
