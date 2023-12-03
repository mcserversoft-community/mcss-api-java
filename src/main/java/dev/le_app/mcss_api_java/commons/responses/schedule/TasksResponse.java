package dev.le_app.mcss_api_java.commons.responses.schedule;

import java.util.ArrayList;

import org.json.JSONObject;

import dev.le_app.mcss_api_java.api.scheduler.Task;
import dev.le_app.mcss_api_java.commons.responses.Response;

public class TasksResponse extends Response {
    
    private JSONObject json;

    public TasksResponse(JSONObject json) {
        super(json);
        this.json = json;
    }

    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<Task>();
        for(int i = 0; i < this.json.getJSONArray("tasks").length(); i++) {
            tasks.add(new Task(this.json.getJSONArray("tasks").getJSONObject(i)));
        }
        return tasks;
    }
}
