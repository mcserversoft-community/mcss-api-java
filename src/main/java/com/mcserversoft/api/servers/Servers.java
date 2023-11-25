package com.mcserversoft.api.servers;

import org.json.JSONObject;

import com.mcserversoft.api.utilities.Request;

public class Servers {

    private Request request;

    public Servers(Request request) {
        this.request = request;
    }

    public Server getServer(String id) throws Exception {
        JSONObject response = this.request.GET("/servers/" + id);
        switch(response.getInt("status")) {
            case 200:
                return new Server(this.request, response);
            case 401:
                throw new Exception("Unauthorized");
            case 403: 
                throw new Exception("Forbidden");
            case 404:
                throw new Exception("Server not found");
            default:
                throw new Exception("Unknown error");
        }
    }
    
}
