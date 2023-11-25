package com.mcserversoft.commons.structures;

import org.json.JSONObject;

public class ServerBuilder {
    
    private String name;
    private String description; 
    private boolean isSetToAutoStart;
    private boolean forceSaveOnStop;
    private int javaAllocatedMemory;
    private KeepOnline keepOnline;

    public ServerBuilder(String name, String description, boolean isSetToAutoStart, boolean forceSaveOnStop, int javaAllocatedMemory, KeepOnline keepOnline) {
        this.name = name;
        this.description = description;
        this.isSetToAutoStart = isSetToAutoStart;
        this.forceSaveOnStop = forceSaveOnStop;
        this.javaAllocatedMemory = javaAllocatedMemory;
        this.keepOnline = keepOnline;
    }

    public ServerBuilder(JSONObject json) {
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.isSetToAutoStart = json.getBoolean("isSetToAutoStart");
        this.forceSaveOnStop = json.getBoolean("forceSaveOnStop");
        this.javaAllocatedMemory = json.getInt("javaAllocatedMemory");
        this.keepOnline = KeepOnline.fromValue(json.getInt("keepOnline"));
    }

    public ServerBuilder() {
        this.name = "";
        this.description = "";
        this.isSetToAutoStart = false;
        this.forceSaveOnStop = false;
        this.javaAllocatedMemory = 0;
        this.keepOnline = KeepOnline.NONE;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isSetToAutoStart() { return isSetToAutoStart; }
    public boolean isForceSaveOnStop() { return forceSaveOnStop; }
    public int getJavaAllocatedMemory() { return javaAllocatedMemory; }
    public KeepOnline getKeepOnline() { return keepOnline; }


    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setAutoStart(boolean isSetToAutoStart) { this.isSetToAutoStart = isSetToAutoStart; }
    public void setForceSaveOnStop(boolean forceSaveOnStop) { this.forceSaveOnStop = forceSaveOnStop; }

    public void setJavaAllocatedMemory(int javaAllocatedMemory) {
        if(javaAllocatedMemory < 0) throw new IllegalArgumentException("javaAllocatedMemory cannot be negative");
        this.javaAllocatedMemory = javaAllocatedMemory;
    }

    public void setKeepOnline(KeepOnline keepOnline) { this.keepOnline = keepOnline; }
    public void setKeepOnline(int keepOnline) { this.keepOnline = KeepOnline.fromValue(keepOnline); }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("description", this.description);
        json.put("isSetToAutoStart", this.isSetToAutoStart);
        json.put("forceSaveOnStop", this.forceSaveOnStop);
        json.put("javaAllocatedMemory", this.javaAllocatedMemory);
        json.put("keepOnline", this.keepOnline.getValue());
        return json;
    }
}
