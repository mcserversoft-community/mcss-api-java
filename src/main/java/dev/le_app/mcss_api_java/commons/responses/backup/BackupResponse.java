package dev.le_app.mcss_api_java.commons.responses.backup;

import org.json.JSONObject;

import dev.le_app.mcss_api_java.commons.responses.Response;

public class BackupResponse extends Response {

    private JSONObject json;

    public BackupResponse(JSONObject json) {
        super(json);
        this.json = json;
    }

    public int getScheduled() {
        return this.json.getInt("scheduled");
    }

    public int getCompleted() {
        return this.json.getInt("completed");
    }

    public int getCanceled() {
        return this.json.getInt("canceled");
    }

    public int getFailed() {
        return this.json.getInt("failed");
    }

    public int getTotal() {
        return this.getScheduled() + this.getCompleted() + this.getCanceled() + this.getFailed();
    }
    
}
