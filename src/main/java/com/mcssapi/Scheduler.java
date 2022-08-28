package com.mcssapi;

import com.mcssapi.exceptions.APIInvalidTaskDetailsException;
import com.mcssapi.exceptions.APINotFoundException;
import com.mcssapi.exceptions.APIUnauthorizedException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;

public class Scheduler {

    private final MCSSApi api;
    private final String GUID;

    protected Scheduler(MCSSApi api, String GUID) {
        this.api = api;
        this.GUID = GUID;
    }


    /**
     * Get total task number
     * @return INT of the total task amount
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an IO error (e.g. server is offline)
     */
    public int getTotalTaskAmount() throws APIUnauthorizedException, APINotFoundException, IOException {

        //GET /api/v1/servers/{GUID}/scheduler/
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/");

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401, throw an APIUnauthorizedException
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when getting info.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when getting info.");
        }

        //save the response in a JSONObject
        JSONObject json = new JSONObject(conn.getOutputStream());

        //close connection
        conn.disconnect();

        //return the total task amount
        return json.getInt("tasks");
    }

    /**
     * Get number of tasks matching filter
     * @param filter The type of task to filter by
     * @return INT of the number of tasks matching the filter
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an IO error (e.g. server is offline)
     */
    public int getTotalTaskAmount(TaskType filter) throws APIUnauthorizedException, APINotFoundException, IOException {

        //GET /api/v1/servers/{GUID}/scheduler/
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/?filter=" + filter.getValue());

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401, throw an APIUnauthorizedException
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when getting info.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when getting info.");
        }

        //save the response in a JSONObject
        JSONObject json = new JSONObject(conn.getOutputStream());

        //close connection
        conn.disconnect();

        //return the total task amount
        return json.getInt("tasks");
    }

    public ArrayList<Task> getTasks() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {
        //GET /api/v1/servers/{GUID}/scheduler/
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/");

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401, throw an APIUnauthorizedException
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when getting info.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when getting info.");
        }

        //save the response in a JSONObject
        JSONObject json = new JSONObject(conn.getOutputStream());
        JSONArray jsonArray = json.getJSONArray("tasks");
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject task = jsonArray.getJSONObject(i);
            tasks.add(new Task(api, GUID, task.getString("taskId"),
                    task.getString("name"), task.getBoolean("enabled")));
        }
        //close connection
        conn.disconnect();
        return tasks;
    }

    public Task createTimelessTask(String Name, Boolean Enabled, Boolean repeating, int interval, Job job) throws APIInvalidTaskDetailsException, APIUnauthorizedException, IOException, APINotFoundException {


        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/tasks");

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        //create a JSONObject to send to the server
        JSONObject mainJson = new JSONObject();
        mainJson.put("name", Name);
        mainJson.put("enabled", Enabled);
        JSONObject timingJson = new JSONObject();
        timingJson.put("repeating", repeating);
        timingJson.put("interval", interval);
        mainJson.put("timing", timingJson);

        //Create the Job JSONObject
        JSONObject jobJson = new JSONObject();

        //Populate the JSONObject appropriately
        if (job instanceof SrvActionJob) {
            jobJson.put("action", job.getAction());
        } else if (job instanceof runCommandsJob) {
            //Create a JSONArray containing the commands, then put it in the jobJson
            JSONArray commands = new JSONArray();
            for (String command : job.getCommands()) {
                commands.put(command);
            }
            jobJson.put("commands", commands);
        } else if (job instanceof BackupJob) {
            jobJson.put("backupIdentifier", job.getBackupGUID());
        } else {
            throw new APIInvalidTaskDetailsException("Invalid job type");
        }

        //Put the jobJson in the mainJson
        mainJson.put("job", jobJson);

        //Connect to the server
        conn.connect();

        //Write the JSONObject to the server
        OutputStream os = conn.getOutputStream();
        os.write(mainJson.toString().getBytes());
        os.flush();
        os.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an error, throw an exception
        if (responseCode == 400) {
            throw new APIInvalidTaskDetailsException("Invalid Task Details (ERR_400)");
        } else if (responseCode == 401) {
            throw new APIUnauthorizedException("API token invalid or expired.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Invalid server GUID (ERR_404)");
        }

        //If the response code is 201, get the task GUID and return a new Task object
        //TODO: STALE DUE TO MISSING API IMPLEMENTATION.

        Task task = new Task(api, GUID, "", "", Enabled);
        return task;
    }

    public Task createFixedTimeTask(String Name, Boolean Enabled, Boolean repeating, LocalTime time, Job job) throws APIInvalidTaskDetailsException, APIUnauthorizedException, IOException, APINotFoundException {
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/tasks");

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        //create a JSONObject to send to the server
        JSONObject mainJson = new JSONObject();
        mainJson.put("name", Name);
        mainJson.put("enabled", Enabled);
        JSONObject timingJson = new JSONObject();
        timingJson.put("timeSpan", time.toString());
        timingJson.put("repeating", repeating);
        mainJson.put("timing", timingJson);

        //Create the Job JSONObject
        JSONObject jobJson = new JSONObject();

        //Populate the JSONObject appropriately
        if (job instanceof SrvActionJob) {
            jobJson.put("action", job.getAction());
        } else if (job instanceof runCommandsJob) {
            //Create a JSONArray containing the commands, then put it in the jobJson
            JSONArray commands = new JSONArray();
            for (String command : job.getCommands()) {
                commands.put(command);
            }
            jobJson.put("commands", commands);
        } else if (job instanceof BackupJob) {
            jobJson.put("backupIdentifier", job.getBackupGUID());
        } else {
            throw new APIInvalidTaskDetailsException("Invalid job type");
        }

        //Put the jobJson in the mainJson
        mainJson.put("job", jobJson);

        //Connect to the server
        conn.connect();

        //Write the JSONObject to the server
        OutputStream os = conn.getOutputStream();
        os.write(mainJson.toString().getBytes());
        os.flush();
        os.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an error, throw an exception
        if (responseCode == 400) {
            throw new APIInvalidTaskDetailsException("Invalid Task Details (ERR_400)");
        } else if (responseCode == 401) {
            throw new APIUnauthorizedException("API token invalid or expired.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Invalid server GUID (ERR_404)");
        }

        //If the response code is 201, get the task GUID and return a new Task object
        //TODO: STALE DUE TO MISSING API IMPLEMENTATION.

        Task task = new Task(api, GUID, "", "", true);
        return task;
    }

    public Task createIntervalTask(String Name, Boolean Enabled, Job job) throws APIInvalidTaskDetailsException, APIUnauthorizedException, IOException, APINotFoundException {
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/tasks");

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        //create a JSONObject to send to the server
        JSONObject mainJson = new JSONObject();
        mainJson.put("name", Name);
        mainJson.put("enabled", Enabled);

        //Create the Job JSONObject
        JSONObject jobJson = new JSONObject();

        //Populate the JSONObject appropriately
        if (job instanceof SrvActionJob) {
            jobJson.put("action", job.getAction());
        } else if (job instanceof runCommandsJob) {
            //Create a JSONArray containing the commands, then put it in the jobJson
            JSONArray commands = new JSONArray();
            for (String command : job.getCommands()) {
                commands.put(command);
            }
            jobJson.put("commands", commands);
        } else if (job instanceof BackupJob) {
            jobJson.put("backupIdentifier", job.getBackupGUID());
        } else {
            throw new APIInvalidTaskDetailsException("Invalid job type");
        }

        //Put the jobJson in the mainJson
        mainJson.put("job", jobJson);

        //Connect to the server
        conn.connect();

        //Write the JSONObject to the server
        OutputStream os = conn.getOutputStream();
        os.write(mainJson.toString().getBytes());
        os.flush();
        os.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an error, throw an exception
        if (responseCode == 400) {
            throw new APIInvalidTaskDetailsException("Invalid Task Details (ERR_400)");
        } else if (responseCode == 401) {
            throw new APIUnauthorizedException("API token invalid or expired.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Invalid server GUID (ERR_404)");
        }

        //If the response code is 201, get the task GUID and return a new Task object
        //TODO: STALE DUE TO MISSING API IMPLEMENTATION.

        Task task = new Task(api, GUID, "", "", true);
        return task;
    }




}
