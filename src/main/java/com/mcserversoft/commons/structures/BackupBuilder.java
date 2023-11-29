package com.mcserversoft.commons.structures;

import org.json.JSONObject;

import com.mcserversoft.api.backups.Compression;

public class BackupBuilder {
    
    private String name;
    private String destination;
    private boolean suspend;
    private Compression compression;
    private boolean deleteOldBackups;
    private boolean runBackupAfterCreation;
    private String[] fileBlacklist;
    private String[] folderBlacklist;

    public BackupBuilder(JSONObject json) {
        this.name = json.getString("name");
        this.destination = json.getString("destination");
        this.suspend = json.getBoolean("suspend");
        this.compression = Compression.valueOf(json.getString("compression"));
        this.deleteOldBackups = json.getBoolean("deleteOldBackups");
        this.runBackupAfterCreation = json.getBoolean("runBackupAfterCreation");
        this.fileBlacklist = json.getJSONArray("fileBlacklist").toList().toArray(new String[0]);
        this.folderBlacklist = json.getJSONArray("folderBlacklist").toList().toArray(new String[0]);
    }

    public BackupBuilder() {
        this.name = null;
        this.destination = null;
        this.suspend = false;
        this.compression = Compression.NONE;
        this.deleteOldBackups = false;
        this.runBackupAfterCreation = false;
        this.fileBlacklist = new String[0];
        this.folderBlacklist = new String[0];
    }

    public String getName() { return this.name; }

    public String getDestination() { return this.destination; }

    public boolean getSuspend() { return this.suspend; }

    public Compression getCompression() { return this.compression; }

    public boolean getDeleteOldBackups() { return this.deleteOldBackups; }

    public boolean getRunBackupAfterCreation() { return this.runBackupAfterCreation; }

    public String[] getFileBlacklist() { return this.fileBlacklist; }

    public String[] getFolderBlacklist() { return this.folderBlacklist; }

    public BackupBuilder setName(String name) { this.name = name; return this; }

    public BackupBuilder setDestination(String destination) { this.destination = destination; return this; }

    public BackupBuilder setSuspend(boolean suspend) { this.suspend = suspend; return this; }

    public BackupBuilder setCompression(Compression compression) { this.compression = compression; return this; }

    public BackupBuilder setDeleteOldBackups(boolean deleteOldBackups) { this.deleteOldBackups = deleteOldBackups; return this; }

    public BackupBuilder setRunBackupAfterCreation(boolean runBackupAfterCreation) { this.runBackupAfterCreation = runBackupAfterCreation; return this; }

    public BackupBuilder setFileBlacklist(String[] fileBlacklist) { this.fileBlacklist = fileBlacklist; return this; }

    public BackupBuilder setFolderBlacklist(String[] folderBlacklist) { this.folderBlacklist = folderBlacklist; return this; }

    public BackupBuilder addFileToBlacklist(String file) {
        String[] newBlacklist = new String[this.fileBlacklist.length + 1];
        System.arraycopy(this.fileBlacklist, 0, newBlacklist, 0, this.fileBlacklist.length);
        newBlacklist[this.fileBlacklist.length] = file;
        this.fileBlacklist = newBlacklist;
        return this;
    }

    public BackupBuilder addFolderToBlacklist(String folder) {
        String[] newBlacklist = new String[this.folderBlacklist.length + 1];
        System.arraycopy(this.folderBlacklist, 0, newBlacklist, 0, this.folderBlacklist.length);
        newBlacklist[this.folderBlacklist.length] = folder;
        this.folderBlacklist = newBlacklist;
        return this;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("destination", this.destination);
        json.put("suspend", this.suspend);
        json.put("compression", this.compression);
        json.put("deleteOldBackups", this.deleteOldBackups);
        json.put("runBackupAfterCreation", this.runBackupAfterCreation);
        json.put("fileBlacklist", this.fileBlacklist);
        json.put("folderBlacklist", this.folderBlacklist);
        return json;
    }

}
