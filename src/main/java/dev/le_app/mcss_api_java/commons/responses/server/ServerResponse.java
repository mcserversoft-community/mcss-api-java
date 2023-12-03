package dev.le_app.mcss_api_java.commons.responses.server;

import org.json.JSONObject;

import dev.le_app.mcss_api_java.commons.responses.Response;
import dev.le_app.mcss_api_java.commons.structures.KeepOnline;

public class ServerResponse extends Response {
    
    private String serverId;
    private int status;
    private String name;
    private String description;
    private String pathToFolder;
    private String folderName;
    private String serverType;
    private String creationDate;
    private boolean isSetToAutoStart;
    private boolean forceSaveOnStop;
    private KeepOnline keepOnline;
    private int javaAllocatedMemory;
    private String javaStartupLine;

    public ServerResponse(JSONObject json) {
        super(json);
        this.serverId = json.getString("serverId");
        this.status = json.getInt("status");
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.pathToFolder = json.getString("pathToFolder");
        this.folderName = json.getString("folderName");
        this.serverType = json.getString("type");
        this.creationDate = json.getString("creationDate");
        this.isSetToAutoStart = json.getBoolean("isSetToAutoStart");
        this.forceSaveOnStop = json.getBoolean("forceSaveOnStop");
        this.keepOnline = KeepOnline.fromValue(json.getInt("keepOnline"));
        this.javaAllocatedMemory = json.getInt("javaAllocatedMemory");
        this.javaStartupLine = json.getString("javaStartupLine");
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isSetToAutoStart() { return isSetToAutoStart; }
    public boolean isForceSaveOnStop() { return forceSaveOnStop; }
    public int getJavaAllocatedMemory() { return javaAllocatedMemory; }
    public KeepOnline getKeepOnline() { return keepOnline; }
    public String getServerId() { return serverId; }
    public int getStatus() { return status; }
    public String getPathToFolder() { return pathToFolder; }
    public String getFolderName() { return folderName; }
    public String getServerType() { return serverType; }
    public String getCreationDate() { return creationDate; }
    public String getJavaStartupLine() { return javaStartupLine; }


    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("serverId", this.serverId);
        json.put("status", this.status);
        json.put("name", this.name);
        json.put("description", this.description);
        json.put("pathToFolder", this.pathToFolder);
        json.put("folderName", this.folderName);
        json.put("type", this.serverType);
        json.put("creationDate", this.creationDate);
        json.put("isSetToAutoStart", this.isSetToAutoStart);
        json.put("forceSaveOnStop", this.forceSaveOnStop);
        json.put("javaAllocatedMemory", this.javaAllocatedMemory);
        json.put("keepOnline", this.keepOnline.getValue());
        json.put("javaStartupLine", this.javaStartupLine);
        return json;
    }
}
