package com.mcserversoft.commons.responses.client;

import org.json.JSONObject;

import com.mcserversoft.commons.responses.Response;

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
