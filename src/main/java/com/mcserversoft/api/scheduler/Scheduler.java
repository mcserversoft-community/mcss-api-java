package com.mcserversoft.api.scheduler;

import java.util.ArrayList;

import org.json.JSONObject;

import com.mcserversoft.api.MCSS;
import com.mcserversoft.api.utilities.Request;
import com.mcserversoft.commons.responses.Response;
import com.mcserversoft.commons.responses.schedule.SchedulerResponse;
import com.mcserversoft.commons.responses.schedule.TasksResponse;
import com.mcserversoft.commons.structures.TaskBuilder;

public class Scheduler {
    
    private Request request;

    private String serverId;

    public Scheduler(String serverId) {
        this.request = MCSS.getRequest();
        this.serverId = null;
    }

    public SchedulerResponse get() throws Exception {
        return new SchedulerResponse(this.request.GET("/servers/" + this.serverId + "/scheduler"));
    }

    public ArrayList<Task> getTasks() throws Exception {
        return new TasksResponse(this.request.GET("/servers/" + this.serverId + "/scheduler/tasks")).getTasks();
    }

    public ArrayList<Task> getTasks(TaskFilter filter) throws Exception {
        return new TasksResponse(this.request.GET("/servers/" + this.serverId + "/scheduler/tasks?filter=" + filter.getValue())).getTasks();
    }

    public ArrayList<Task> getTasks(int filter) throws Exception {
        return new TasksResponse(this.request.GET("/servers/" + this.serverId + "/scheduler/tasks?filter=" + filter)).getTasks();
    }

    public Task getTask(String taskId) throws Exception {
        return new Task(this.request.GET("/servers/" + this.serverId + "/scheduler/tasks/" + taskId));
    }

    public Response create(TaskBuilder task) throws Exception {
        return new Response(this.request.POST("/servers/" + this.serverId + "/scheduler/tasks", task.toJSON()));
    }

    public Response create(JSONObject task) throws Exception {
        return new Response(this.request.POST("/servers/" + this.serverId + "/scheduler/tasks", task));
    }

    public Response update(String taskId, TaskBuilder task) throws Exception {
        return new Response(this.request.PUT("/servers/" + this.serverId + "/scheduler/tasks/" + taskId, task.toJSON()));
    }

    public Response update(String taskId, JSONObject task) throws Exception {
        return new Response(this.request.PUT("/servers/" + this.serverId + "/scheduler/tasks/" + taskId, task));
    }

    public Response delete(String taskId) throws Exception {
        return new Response(this.request.DELETE("/servers/" + this.serverId + "/scheduler/tasks/" + taskId));
    }

}
