package com.mcserversoft.api.servers;

import org.json.JSONObject;

public class ServerStats {

    private JSONObject json;

    public ServerStats(JSONObject response) {
        this.json = response;
    }

    public int getCPU() {
        return this.json.getJSONObject("latest").getInt("cpu");
    }

    public int getMemoryUsed() {
        return this.json.getJSONObject("latest").getInt("memoryUsed");
    }

    public int getMemoryLimit() {
        return this.json.getJSONObject("latest").getInt("memoryLimit");
    }

    public int getPlayersOnline() {
        return this.json.getJSONObject("latest").getInt("playersOnline");
    }

    public int getPlayerLimit() {
        return this.json.getJSONObject("latest").getInt("playerLimit");
    }

    public int getStartDate() {
        return this.json.getJSONObject("latest").getInt("startDate");
    }
    
}
