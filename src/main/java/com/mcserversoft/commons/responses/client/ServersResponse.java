package com.mcserversoft.commons.responses.client;

import java.util.ArrayList;

import org.json.JSONObject;

import com.mcserversoft.commons.responses.Response;
import com.mcserversoft.commons.responses.server.ServerResponse;

public class ServersResponse extends Response {

    private JSONObject json;

    private ArrayList<ServerResponse> servers;

    public ServersResponse(JSONObject responses) {
        super(responses);
        this.json = responses;
        this.servers = new ArrayList<ServerResponse>();
        for (int i = 0; i < json.getJSONArray("servers").length(); i++) {
            servers.add(new ServerResponse(json.getJSONArray("servers").getJSONObject(i)));
        }
    }

    public ArrayList<ServerResponse> getServers() {
        return this.servers;
    }

}
