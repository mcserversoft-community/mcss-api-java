package com.mcssapi;

import com.mcssapi.exceptions.APIInvalidTaskDetailsException;
import com.mcssapi.exceptions.APINotFoundException;
import com.mcssapi.exceptions.APIUnauthorizedException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task {

    private final MCSSApi api;
    private final String GUID;
    private final String TaskID;
    private String TaskName;
    private boolean Enabled;
    private TaskType TaskType;

    private boolean Deleted;


    //Not passing timing information because not all tasks have it.
    public Task(MCSSApi api, String GUID, String TaskID, String TaskName, boolean Enabled) throws APIUnauthorizedException, IOException, APINotFoundException, APIInvalidTaskDetailsException {
        this.api = api;
        this.GUID = GUID;
        this.TaskID = TaskID;
        this.TaskName = TaskName;
        this.Enabled = Enabled;
        this.TaskType = figureOutTaskType();
    }

    private TaskType figureOutTaskType() throws IOException, APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException {


        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/tasks/" + TaskID);

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

        //if the response code is 401 or 404, throw the relevant exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when getting task info.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when getting task info.");
        }

        //save the response in a JSONObject
        JSONObject json = new JSONObject(conn.getOutputStream());

        //close connection
        conn.disconnect();

        //parse the task type from the JSONObject "timing"
        JSONObject timing = json.getJSONObject("timing");
        if (timing.has("timeSpan")) {
            return com.mcssapi.TaskType.FIXED_TIME;
        } else if (timing.has("interval")) {
            return com.mcssapi.TaskType.INTERVAL;
        } else if (timing.has("timeless")) {
            return com.mcssapi.TaskType.TIMELESS;
        } else {
            throw new APIInvalidTaskDetailsException("Task has no timing information.");
        }
    }

    /**
     * @return the Task ID
     */
    public String getTaskID() {
        return TaskID;
    }

    /**
     * @return the Task Name
     */
    public String getTaskName() {
        return TaskName;
    }

    /**
     * @return the Task Type
     */
    public TaskType getTaskType() {
        return TaskType;
    }

    /**
     * @return the Enabled status of the Task
     */
    public boolean isEnabled() {
        return Enabled;
    }

    /**
     * @return true if the task has been deleted from the API
     */
    public boolean isDeleted() {
        return Deleted;
    }

    /**
     * Enables the task
     * @throws IOException if there is an error connecting to the server
     * @throws APINotFoundException if the server returns a 404 response code
     * @throws APIUnauthorizedException if the server returns a 401 response code
     * @throws APIInvalidTaskDetailsException if the server returns a 409 response code
     */
    public void setEnabled() throws IOException, APINotFoundException, APIUnauthorizedException, APIInvalidTaskDetailsException {

        if (TaskType == com.mcssapi.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException("Cannot enable a timeless task.");
        } else if (Deleted) {
            throw new APIInvalidTaskDetailsException("Cannot enable a deleted task.");
        }

        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/tasks/" + TaskID);

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);


        String json = "{\"enabled\":true}";

        //connect to the server
        conn.connect();

        //write the JSON to the output stream
        conn.getOutputStream().write(json.getBytes());

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code indicates an error, throw the appropriate exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when enabling task.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when enabling task.");
        } else if (responseCode == 409 ) {
            throw new APIInvalidTaskDetailsException("Got 409 response code when enabling task.");
        }

        Enabled = true;

        //close connection
        conn.disconnect();

    }

    /**
     * Disables the task
     * @throws IOException if there is an error connecting to the server
     * @throws APINotFoundException if the server returns a 404 response code
     * @throws APIUnauthorizedException if the server returns a 401 response code
     * @throws APIInvalidTaskDetailsException if the server returns a 409 response code
     */
    public void setDisabled() throws IOException, APINotFoundException, APIUnauthorizedException, APIInvalidTaskDetailsException {

        if (TaskType == com.mcssapi.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException("Cannot disable a timeless task.");
        } else if (Deleted) {
            throw new APIInvalidTaskDetailsException("Cannot disable a deleted task.");
        }
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/tasks/" + TaskID);

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String json = "{\"enabled\":false}";

        //connect to the server
        conn.connect();

        //write the JSON to the output stream
        conn.getOutputStream().write(json.getBytes());

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code indicates an error, throw the appropriate exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when disabling task.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when disabling task.");
        } else if (responseCode == 409) {
            throw new APIInvalidTaskDetailsException("Got 409 response code when disabling task.");
        }

        Enabled = false;

        //close connection
        conn.disconnect();

    }

    /**
     * Manually run the task
     * @throws IOException if there is an error connecting to the server
     * @throws APINotFoundException if the server returns a 404 response code
     * @throws APIUnauthorizedException if the server returns a 401 response code
     */
    public void runTask() throws IOException, APINotFoundException, APIUnauthorizedException, APIInvalidTaskDetailsException {

        if (Deleted) {
            throw new APIInvalidTaskDetailsException("Cannot run a deleted task.");
        }

        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/tasks/" + TaskID);

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);

        //connect to the server
        conn.connect();
        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code indicates an error, throw the appropriate exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when running task.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when running task.");
        }
        //close connection
        conn.disconnect();
    }

    /**
     * Change the task name
     * @param newName the new name for the task
     * @throws APIInvalidTaskDetailsException if the server returns a 409 response code
     * @throws APIUnauthorizedException if the server returns a 401 response code
     * @throws APINotFoundException if the server returns a 404 response code
     * @throws IOException if there is an error connecting to the server
     */
    public void changeName(String newName) throws APIInvalidTaskDetailsException, APIUnauthorizedException, APINotFoundException, IOException {

        if (Deleted) {
            throw new APIInvalidTaskDetailsException("Cannot change name of a deleted task.");
        }

        //Check if new name contains special characters
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(newName);
        if (m.find()) {
            throw new APIInvalidTaskDetailsException("Task name cannot contain special characters.");
        }

        //Create URL
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/tasks/" + TaskID);

        //Create connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set request method and request properties
        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        //Create JSON
        String json = "{\"name\":\"" + newName + "\"}";

        //Connect to server
        conn.connect();

        //Write JSON to output stream
        conn.getOutputStream().write(json.getBytes());

        //Get response code
        int responseCode = conn.getResponseCode();

        //If response code indicates an error, throw the appropriate exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when changing task name.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when changing task name.");
        } else if (responseCode == 409) {
            throw new APIInvalidTaskDetailsException("Got 409 response code when changing task name.");
        }

        //Close connection
        conn.disconnect();

        TaskName = newName;

    }



    /**
     * Delete the task from the API
     * @throws IOException if there is an error connecting to the server
     * @throws APINotFoundException if the server returns a 404 response code
     * @throws APIUnauthorizedException if the server returns a 401 response code
     */
    public void deleteTask() throws IOException, APINotFoundException, APIUnauthorizedException {
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/tasks/" + TaskID);

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("DELETE");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoOutput(true);
        conn.connect();
        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code indicates an error, throw the appropriate exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when deleting task.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when deleting task.");
        }

        Deleted = true;
        //close connection
        conn.disconnect();
    }

}
