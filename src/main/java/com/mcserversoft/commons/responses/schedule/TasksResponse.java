package com.mcserversoft.commons.responses.schedule;

import java.util.ArrayList;

import org.json.JSONObject;

import com.mcserversoft.api.scheduler.Task;
import com.mcserversoft.commons.responses.Response;

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
