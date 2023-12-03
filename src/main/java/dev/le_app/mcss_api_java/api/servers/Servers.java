package dev.le_app.mcss_api_java.api.servers;

import dev.le_app.mcss_api_java.api.MCSS;
import dev.le_app.mcss_api_java.api.utilities.Request;
import org.json.JSONObject;

public class Servers {

    private Request request;

    public Servers() {
        this.request = MCSS.getRequest();
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
