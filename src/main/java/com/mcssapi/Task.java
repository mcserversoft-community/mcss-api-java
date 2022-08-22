package com.mcssapi;

import com.mcssapi.exceptions.APIInvalidTaskDetailsException;
import com.mcssapi.exceptions.APINotFoundException;
import com.mcssapi.exceptions.APIUnauthorizedException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task {

    private final MCSSApi api;
    private final String GUID;
    private final String TaskID;
    private String TaskName;
    private boolean Enabled;
    private TaskType TaskType;

    private boolean Deleted = false;


    //Not passing timing information because not all tasks have it.
    protected Task(MCSSApi api, String GUID, String TaskID, String TaskName, boolean Enabled) throws APIUnauthorizedException, IOException, APINotFoundException, APIInvalidTaskDetailsException {
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
        if (timing.has("time")) {
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
     * Check if the task repeats at the set interval/fixed time
     * @return true if the task repeats at the set interval/fixed time
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server or task is not found
     * @throws APIInvalidTaskDetailsException if the task has no timing information
     * @throws IOException if there is an error connecting to the server
     */
    public boolean isRepeating() throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {
        if (TaskType == com.mcssapi.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException("Timeless Tasks cannot repeat.");
        } else if (Deleted) {
            throw new APIInvalidTaskDetailsException("Deleted Tasks cannot repeat.");
        }

        //Create URL
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

        //get the "timing" object from the main JSONObject
        JSONObject timing = json.getJSONObject("timing");

        //get the "repeat" boolean value from the timing object
        return timing.getBoolean("repeat");
    }

    /**
     * Get the timing information for the Task.
     * @return the timing information for the Task
     * @throws IOException if there is an error connecting to the server
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server or task is not found
     * @throws APIInvalidTaskDetailsException if the task has no timing information, or if the task has an invalid timing information
     */
    public LocalTime getTime() throws IOException, APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException {
        if (TaskType == com.mcssapi.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException("Timeless tasks don't have time details.");
        } else if (TaskType == com.mcssapi.TaskType.INTERVAL) {
            throw new APIInvalidTaskDetailsException("Interval tasks don't have time details.");
        } else if (Deleted) {
            throw new APINotFoundException("Cannot get time of a deleted task.");
        }

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

        //Extract the timing JSONObject
        JSONObject timing = json.getJSONObject("timing");

        //Extract the time variable and parse it to a LocalTime
        String time = timing.getString("time");
        Pattern p = Pattern.compile("(\\d{2}):(\\d{2}):(\\d{2})");
        Matcher m = p.matcher(time);
        if (m.find()) {
            int hour = Integer.parseInt(m.group(1));
            int minute = Integer.parseInt(m.group(2));
            int second = Integer.parseInt(m.group(3));
            return LocalTime.of(hour, minute, second);
        } else {
            throw new APIInvalidTaskDetailsException("Could not parse time to LocalTime.");
        }

    }

    /**
     * Get the interval information for the Task.
     * @return Long int of the interval in seconds
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server or task is not found
     * @throws APIInvalidTaskDetailsException if the task has no interval information, or if the task has an invalid interval information
     * @throws IOException if there is an error connecting to the server
     */
    public long getInterval() throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {

        //Check if the task has an interval
        if (TaskType == com.mcssapi.TaskType.FIXED_TIME) {
            throw new APIInvalidTaskDetailsException("Fixed time tasks don't have interval details.");
        } else if (TaskType == com.mcssapi.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException("Timeless tasks don't have interval details.");
        } else if (Deleted) {
            throw new APINotFoundException("Cannot get interval of a deleted task.");
        }

        //Create the URL
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

        //Extract the timing JSONObject
        JSONObject timing = json.getJSONObject("timing");
        return timing.getLong("interval");
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
     * Change the interval of an Interval task
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server or task is not found
     * @throws APIInvalidTaskDetailsException if the task has no interval information, or if the task has an invalid interval information
     * @throws IOException if there is an error connecting to the server
     */
    public void setInterval(long newInterval) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {

        //Check if the task has the interval value and that it's not deleted
        if (TaskType == com.mcssapi.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException("Timeless tasks don't have interval details.");
        } else if (TaskType == com.mcssapi.TaskType.FIXED_TIME) {
            throw new APIInvalidTaskDetailsException("Fixed Time tasks don't have interval details.");
        } else if (Deleted) {
            throw new APIInvalidTaskDetailsException("Cannot set interval of a deleted task.");
        }

        //Check if the interval is valid
        if (newInterval < 1) {
            throw new APIInvalidTaskDetailsException("Interval must be greater than 0.");
        }

        //Create the URL
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/tasks/" + TaskID);

        //Create the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set the request method and request properties
        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        //Create the JSON
        String json = """
                {  "timing": {
                    "interval":"200"
                    }
                }""";

        //Open the connection
        conn.connect();

        //Write the JSON to the output stream
        conn.getOutputStream().write(json.getBytes());

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an error, throw the appropriate exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException("API token is invalid.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Invalid task/server ID.");
        } else if (responseCode == 409) {
            throw new APIInvalidTaskDetailsException("Cannot change timing information.");
        }

        //Close the connection
        conn.disconnect();
    }

    public void setTime(LocalTime newTime) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {

        if (Deleted) {
            throw new APIInvalidTaskDetailsException("Cannot set time of a deleted task.");
        } else if (TaskType == com.mcssapi.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException("Timeless tasks don't have time details.");
        } else if (TaskType == com.mcssapi.TaskType.INTERVAL) {
            throw new APIInvalidTaskDetailsException("Interval tasks don't have time details.");
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

        //Create JSON with the newTime
        String json = "{\"timing\":{\"time\":\"" + newTime.toString() + "\"}}";

        //Open the connection
        conn.connect();

        //Write the JSON to the output stream
        conn.getOutputStream().write(json.getBytes());

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an error, throw the appropriate exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException("API token is invalid.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Invalid task/server ID.");
        } else if (responseCode == 409) {
            throw new APIInvalidTaskDetailsException("Cannot change timing information.");
        }

        //Close the connection
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
     * Set the task to repeat
     * @param repeat boolean of the new repeat value, true or false
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server or task is not found
     * @throws APIInvalidTaskDetailsException if the task has no repeat information, or if the task has an invalid repeat information
     * @throws IOException if there is an error connecting to the server
     */
    public void setRepeating(boolean repeat) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {

        if (Deleted) {
            throw new APIInvalidTaskDetailsException("Cannot set repeating of a deleted task.");
        } else if (TaskType == com.mcssapi.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException("Cannot set repeating of a timeless task.");
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

        //connect to the server
        conn.connect();

        //create the JSON
        String json = " { \"timing\" { \"repeat\": " + repeat + " } }";

        //write the JSON to the output stream
        conn.getOutputStream().write(json.getBytes());

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code indicates an error, throw the appropriate exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when setting repeating.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when setting repeating.");
        } else if (responseCode == 409) {
            throw new APIInvalidTaskDetailsException("Got 409 response code when setting repeating.");
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
