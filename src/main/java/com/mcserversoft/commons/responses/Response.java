package com.mcserversoft.commons.responses;

import org.json.JSONObject;

public class Response {
    
    private JSONObject json;

    public Response(JSONObject response) {
        this.json = response;
    }

    public Response() {
        this.json = new JSONObject();
    }

    public int getStatus() {
        return this.json.getInt("status");
    }

    public JSONObject getRaw() {
        return this.json;
    }

    public String toString() {
        return this.json.toString();
    }
}
