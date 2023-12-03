package dev.le_app.mcss_api_java.commons.responses.client;

import java.util.ArrayList;

import dev.le_app.mcss_api_java.commons.responses.server.ServerResponse;
import org.json.JSONObject;

import dev.le_app.mcss_api_java.commons.responses.Response;

public class ServersResponse extends Response {

    private JSONObject json;

    private ArrayList<ServerResponse> servers;

    public ServersResponse(JSONObject responses) {
        super(responses);
        this.json = responses;
        this.servers = new ArrayList<ServerResponse>();
        for (int i = 0; i < json.getJSONArray("data").length(); i++) {
            servers.add(new ServerResponse(json.getJSONArray("data").getJSONObject(i)));
        }
    }

    public ArrayList<ServerResponse> getServers() {
        return this.servers;
    }

}
