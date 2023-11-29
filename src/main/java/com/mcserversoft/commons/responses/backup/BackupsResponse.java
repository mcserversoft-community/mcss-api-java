package com.mcserversoft.commons.responses.backup;

import java.util.ArrayList;

import org.json.JSONObject;

import com.mcserversoft.api.backups.Backup;
import com.mcserversoft.commons.responses.Response;

public class BackupsResponse extends Response {
    
    private JSONObject json;

    public BackupsResponse(JSONObject json) {
        super(json);
        this.json = json;
    }

    public ArrayList<Backup> getBackups() {
        ArrayList<Backup> backups = new ArrayList<Backup>();
        for (int i = 0; i < this.json.getJSONArray("backups").length(); i++) {
            backups.add(new Backup(this.json.getJSONArray("backups").getJSONObject(i)));
        }
        return backups;
    }
}
