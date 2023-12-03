package dev.le_app.mcss_api_java.commons.responses.client;

import org.json.JSONObject;

import dev.le_app.mcss_api_java.commons.responses.Response;

public class StatsResponse extends Response {

    private JSONObject json;

    public StatsResponse(JSONObject response) {
        super(response);
        this.json = response;
    }

    public boolean isDevBuild() {
        return this.json.getBoolean("isDevBuild");
    }

    public String getMcssVersion() {
        return this.json.getString("mcssVersion");
    }

    public String getAPIVersion() {
        return this.json.getString("mcssApiVersion");
    }

    public String getUniqueID() {
        return this.json.getString("uniqueIdentifier");
    }

    public boolean youAreAwesome() {
        return this.json.getBoolean("youAreAwesome");
    }

}
