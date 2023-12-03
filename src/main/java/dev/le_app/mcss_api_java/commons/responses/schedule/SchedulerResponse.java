package dev.le_app.mcss_api_java.commons.responses.schedule;

import org.json.JSONObject;

import dev.le_app.mcss_api_java.commons.responses.Response;

public class SchedulerResponse extends Response {

    JSONObject json;

    public SchedulerResponse(JSONObject json) {
        super(json);
        this.json = json;
    }

    public int getTasks() {
        return this.json.getInt("tasks");
    }

    public int getInterval() {
        return this.json.getInt("interval");
    }

    public int getFixedTime() {
        return this.json.getInt("fixedTime");
    }

    public int getTimeless() {
        return this.json.getInt("timeless");
    }
    
}
