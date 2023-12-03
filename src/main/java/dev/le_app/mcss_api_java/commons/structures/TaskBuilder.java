package dev.le_app.mcss_api_java.commons.structures;

import org.json.JSONObject;

public class TaskBuilder {
    
    private String name;

    private boolean enabled;

    private int playerRequirement;

    private Object timing;

    private Object job;

    public TaskBuilder(JSONObject json) {
        this.name = json.getString("name");
        this.enabled = json.getBoolean("enabled");
        this.playerRequirement = json.getInt("playerRequirement");
        this.timing = json.get("timing");
        this.job = json.get("job");
    }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public boolean getEnabled() { return this.enabled; }

    public TaskBuilder setEnabled(boolean enabled) { this.enabled = enabled; return this; }

    public int getPlayerRequirement() { return this.playerRequirement; }

    public TaskBuilder setPlayerRequirement(int playerRequirement) { this.playerRequirement = playerRequirement; return this; }

    public Object getTiming() { return this.timing; }

    public TaskBuilder setTiming(Object timing, int interval) { 
        if(timing instanceof Boolean) {
            this.timing = new JSONObject().put("repeat", timing).put("interval", interval);
            return this;
        }
        this.timing = timing;
        return this;
    }

    public Object getJob() { return this.job; }

    public TaskBuilder addJob(Object action) {
        if(action instanceof String[]) {
            this.job = new JSONObject().put("commands", action);
            return this;
        }
        if(action instanceof String) {
            this.job = new JSONObject().put("commands", new String[]{(String)action});
            return this;
        }
        this.job = action;
        return this;
    }

    public JSONObject toJSON() {
        return new JSONObject()
            .put("name", this.name)
            .put("enabled", this.enabled)
            .put("playerRequirement", this.playerRequirement)
            .put("timing", this.timing)
            .put("job", this.job);
    }
}

