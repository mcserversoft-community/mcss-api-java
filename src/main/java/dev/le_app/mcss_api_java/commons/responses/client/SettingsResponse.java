package dev.le_app.mcss_api_java.commons.responses.client;

import org.json.JSONObject;

import dev.le_app.mcss_api_java.commons.responses.Response;

public class SettingsResponse extends Response {

    JSONObject json;

    public SettingsResponse(JSONObject json) {
        super(json);
        this.json = json;
    }

    public int getDeleteOldBackupsThreshold() {
        return this.json.getInt("deleteOldBackupsThreshold");
    }

    public String getPreviousBackupLocation() {
        return this.json.getString("previousBackupLocation");
    }

}
