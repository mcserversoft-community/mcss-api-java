package dev.le_app.mcss_api_java.commons.responses.backup;

import java.util.ArrayList;

import dev.le_app.mcss_api_java.api.backups.Backup;
import org.json.JSONObject;

import dev.le_app.mcss_api_java.commons.responses.Response;

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
