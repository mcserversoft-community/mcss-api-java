package com.mcserversoft.api.backups;

import org.json.JSONObject;

public class Backup {
    
    private String name;
    private String destination;
    private boolean suspend;
    private Compression compression;
    private boolean deleteOldBackups;
    private boolean runBackupAfterCreation;
    private String[] fileBlacklist;
    private String[] folderBlacklist;

    public Backup(JSONObject json) {
        this.name = json.getString("name");
        this.destination = json.getString("destination");
        this.suspend = json.getBoolean("suspend");
        this.compression = Compression.valueOf(json.getString("compression"));
        this.deleteOldBackups = json.getBoolean("deleteOldBackups");
        this.runBackupAfterCreation = json.getBoolean("runBackupAfterCreation");
        this.fileBlacklist = json.getJSONArray("fileBlacklist").toList().toArray(new String[0]);
        this.folderBlacklist = json.getJSONArray("folderBlacklist").toList().toArray(new String[0]);
    }

    public String getName() { return this.name; }

    public String getDestination() { return this.destination; }

    public boolean getSuspend() { return this.suspend; }

    public Compression getCompression() { return this.compression; }

    public boolean getDeleteOldBackups() { return this.deleteOldBackups; }

    public boolean getRunBackupAfterCreation() { return this.runBackupAfterCreation; }

    public String[] getFileBlacklist() { return this.fileBlacklist; }

    public String[] getFolderBlacklist() { return this.folderBlacklist; }

}
