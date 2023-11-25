package com.mcserversoft.commons.responses.client;

import org.json.JSONObject;

import com.mcserversoft.commons.responses.Response;

public class ServerCountResponse extends Response {

    private JSONObject json;

    public ServerCountResponse(JSONObject json) throws Exception {
        super(json);
    }

    public int getCount() {
        return this.json.getInt("count");
    }
    
}
