package dev.le_app.mcss_api_java.commons.responses.client;

import org.json.JSONObject;

import dev.le_app.mcss_api_java.commons.responses.Response;

public class ServerCountResponse extends Response {

    private JSONObject json;

    public ServerCountResponse(JSONObject json) throws Exception {
        super(json);
        this.json = json;
    }

    public int getCount() {
        return this.json.getInt("count");
    }
    
}
