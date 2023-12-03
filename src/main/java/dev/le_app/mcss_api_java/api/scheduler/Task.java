package dev.le_app.mcss_api_java.api.scheduler;

import org.json.JSONObject;

public class Task {
    
    private String name;

    private boolean enabled;

    private int playerRequirement;

    private Object timing;

    private Object job;

    public Task(JSONObject json) {
        this.name = json.getString("name");
        this.enabled = json.getBoolean("enabled");
        this.playerRequirement = json.getInt("playerRequirement");
        this.timing = json.get("timing");
        this.job = json.get("job");
    }

    public String getName() { return this.name; }

    public boolean getEnabled() { return this.enabled; }

    public int getPlayerRequirement() { return this.playerRequirement; }

    public Object getTiming() { return this.timing; }

    public Object getJob() { return this.job; }

    public JSONObject toJSON() {
        return new JSONObject()
            .put("name", this.name)
            .put("enabled", this.enabled)
            .put("playerRequirement", this.playerRequirement)
            .put("timing", this.timing)
            .put("job", this.job);
    }
}
