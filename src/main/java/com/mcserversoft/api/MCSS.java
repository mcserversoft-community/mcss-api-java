package com.mcserversoft.api;

import java.util.ArrayList;

import org.json.JSONObject;

import com.mcserversoft.api.servers.ServerCountFilter;
import com.mcserversoft.api.servers.ServerFilter;
import com.mcserversoft.api.servers.ServerType;
import com.mcserversoft.api.servers.Servers;
import com.mcserversoft.api.utilities.Request;
import com.mcserversoft.commons.responses.Response;
import com.mcserversoft.commons.responses.client.ServerCountResponse;
import com.mcserversoft.commons.responses.client.ServersResponse;
import com.mcserversoft.commons.responses.client.SettingsResponse;
import com.mcserversoft.commons.responses.client.StatsResponse;
import com.mcserversoft.commons.responses.server.ServerResponse;

public class MCSS {

    private String url;
    private int port;
    private boolean https;
    private String apiKey;

    private Request request;

    public Servers servers;

    public MCSS(String ip, int port, String apiKey, boolean https) {

        this.port = port;
        this.https = https;
        this.apiKey = apiKey;

        String protocol = https ? "https" : "http";
        String portString = (port > 0) ? (":" + port) : "";
        this.url = protocol + "://" + ip + portString + "/api/v2";

        this.request = new Request(url);
        this.request.addHeader("apiKey", apiKey);

        this.servers = new Servers(this.request);
    }

    public MCSS(String ip, int port, String apiKey) {
        this(ip, port, apiKey, false);
    }
    
    public StatsResponse getStats() throws Exception {
        return new StatsResponse(this.request.GET("/"));
    }

    public ArrayList<ServerResponse> getServers() throws Exception {
        ServersResponse servers = new ServersResponse(this.request.GET("/servers"));
        return servers.getServers();
    }

    public ArrayList<ServerResponse> getServers(ServerFilter filter) throws Exception {
        ServersResponse servers = new ServersResponse(this.request.GET("/servers?filter=" + filter));
        return servers.getServers();
    }

    public ArrayList<ServerResponse> getServers(int filter) throws Exception {
        ServersResponse servers = new ServersResponse(this.request.GET("/servers?filter=" + filter));
        return servers.getServers();
    }

    public ServerCountResponse getServerCount() throws Exception {
        return new ServerCountResponse(this.request.GET("/servers/count"));
    }

    public ServerCountResponse getServerCount(ServerCountFilter filter) throws Exception {
        if(filter == ServerCountFilter.BYSERVERTYPE) throw new Exception("ServerCountFilter.BYSERVERTYPE is not supported yet");
        return new ServerCountResponse(this.request.GET("/servers/count?filter=" + filter));
    }

    public ServerCountResponse getServerCount(ServerCountFilter filter, ServerType type) throws Exception {
        return new ServerCountResponse(this.request.GET("/servers/count?filter=" + filter + "&type=" + type));
    }

    public ServerCountResponse getServerCount(int filter) throws Exception {
        if(filter == ServerCountFilter.BYSERVERTYPE.getValue()) throw new Exception("ServerCountFilter.BYSERVERTYPE is not supported yet");
        return new ServerCountResponse(this.request.GET("/servers/count?filter=" + filter));
    }

    public ServerCountResponse getServerCount(int filter, String type) throws Exception {
        return new ServerCountResponse(this.request.GET("/servers/count?filter=" + filter + "&type=" + type));
    }

    public ServerCountResponse getServerCount(int filter, ServerType type) throws Exception {
        return new ServerCountResponse(this.request.GET("/servers/count?filter=" + filter + "&type=" + type));
    }

    public SettingsResponse getSettings() throws Exception {
        return new SettingsResponse(this.request.GET("/mcss/settings/All"));
    }

    public Response updateSettings(int deleteOldBackupsThreshold) throws Exception {
        return new Response(this.request.PATCH("/mcss/settings", new JSONObject().put("deleteOldBackupsThreshold", deleteOldBackupsThreshold)));
    }

    public String getUrl() { return this.url; }
    public int getPort() { return this.port; }
    public boolean isHttps() { return this.https; }
    public String getApiKey() { return this.apiKey; }

    public void setUrl(String ip) {
        String protocol = this.https ? "https" : "http";
        String portString = (this.port > 0) ? (":" + this.port) : "";
        this.url = protocol + "://" + ip + portString + "/api/v2";

        this.request = new Request(url);
        this.servers = new Servers(this.request);
    }
    
    public void setPort(int port) {
        this.port = port;
        String protocol = this.https ? "https" : "http";
        String portString = (this.port > 0) ? (":" + this.port) : "";
        this.url = protocol + "://" + this.url.split("://")[1].split(":")[0] + portString + "/api/v2";

        this.request = new Request(url);
        this.servers = new Servers(this.request);
    }

    public void setHttps(boolean https) {
        this.https = https;
        String protocol = this.https ? "https" : "http";
        String portString = (this.port > 0) ? (":" + this.port) : "";
        this.url = protocol + "://" + this.url.split("://")[1].split(":")[0] + portString + "/api/v2";

        this.request = new Request(url);
        this.servers = new Servers(this.request);
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.request.setHeader("apiKey", apiKey);

        this.servers = new Servers(this.request);
    }

    public Request getRequest() {
        return this.request;
    }
}
