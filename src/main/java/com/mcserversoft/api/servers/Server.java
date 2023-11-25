package com.mcserversoft.api.servers;

import org.json.JSONObject;

import com.mcserversoft.api.utilities.Request;
import com.mcserversoft.commons.responses.Response;
import com.mcserversoft.commons.responses.server.ServerResponse;
import com.mcserversoft.commons.structures.ServerBuilder;

public class Server extends ServerResponse {

    private Request request;

    public Server(Request request, JSONObject response) {
        super(response);
        this.request = request;
    }

    public ServerStats getStats() throws Exception {
        return new ServerStats(this.request.GET("/servers/" + this.getServerId() + "/stats"));
    }

    public Response execute(String command) throws Exception {
        return new Response(this.request.POST("/servers/" + this.getServerId() + "/execute/command", new JSONObject().put("command", command)));
    }

    public Response execute(String[] commands) throws Exception {
        return new Response(this.request.POST("/servers/" + this.getServerId() + "/execute/commands", new JSONObject().put("commands", commands)));
    }

    public Response execute(ServerAction action) throws Exception {
        return new Response(this.request.POST("/servers/" + this.getServerId() + "/execute/action", new JSONObject().put("action", action.toString())));
    }

    public Response execute(int action) throws Exception {
        return new Response(this.request.POST("/servers/" + this.getServerId() + "/execute/action", new JSONObject().put("action", action)));
    }

    public Response start() throws Exception {
        return this.execute(ServerAction.START);
    }

    public Response stop() throws Exception {
        return this.execute(ServerAction.STOP);
    }

    public Response restart() throws Exception {
        return this.execute(ServerAction.RESTART);
    }

    public Response kill() throws Exception {
        return this.execute(ServerAction.KILL);
    }

    public Response edit(ServerBuilder builder) throws Exception {
        return new Response(this.request.PUT("/servers/" + this.getServerId(), builder.toJSON()));
    }

    public Response edit(JSONObject json) throws Exception {
        return new Response(this.request.PUT("/servers/" + this.getServerId(), json));
    }

    public String[] getConsole() throws Exception {
        return this.request.GET("/servers/" + this.getServerId() + "/console").getJSONArray("console").toList().toArray(new String[0]);
    }

    public String[] getConsole(int lines) throws Exception {
        return this.request.GET("/servers/" + this.getServerId() + "/console?lines=" + lines).getJSONArray("console").toList().toArray(new String[0]);
    }

    public String[] getConsole(boolean reversed) throws Exception {
        return this.request.GET("/servers/" + this.getServerId() + "/console?reversed=" + reversed).getJSONArray("console").toList().toArray(new String[0]);
    }

    public String[] getConsole(int lines, boolean reversed) throws Exception {
        return this.request.GET("/servers/" + this.getServerId() + "/console?lines=" + lines + "&reversed=" + reversed).getJSONArray("console").toList().toArray(new String[0]);
    }

    public String[] getConsole(int lines, boolean reversed, boolean takeFromBeginning) throws Exception {
        return this.request.GET("/servers/" + this.getServerId() + "/console?lines=" + lines + "&reversed=" + reversed + "&takeFromBeginning=" + takeFromBeginning).getJSONArray("console").toList().toArray(new String[0]);
    }

    public boolean isConsoleOutdated(String secondLastLine, String lastLine) throws Exception {
        return this.request.GET("/servers/" + this.getServerId() + "/console?secondLastLine=" + secondLastLine + "&lastLine=" + lastLine).getBoolean("outdated");
    }
    
}
