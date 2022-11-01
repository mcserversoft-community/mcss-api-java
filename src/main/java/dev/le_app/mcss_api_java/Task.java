package dev.le_app.mcss_api_java;

import dev.le_app.mcss_api_java.exceptions.APIInvalidTaskDetailsException;
import dev.le_app.mcss_api_java.exceptions.APINoServerAccessException;
import dev.le_app.mcss_api_java.exceptions.APINotFoundException;
import dev.le_app.mcss_api_java.exceptions.APIUnauthorizedException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task {

    private final MCSSApi api;
    private final String GUID;
    private final String taskID;
    private String taskName;
    private boolean enabled;
    private final TaskType taskType;
    private PlayerRequirement playerRequirement;

    private final TaskJobType taskJobType;

    private boolean Deleted = false;


    //Not passing timing information because not all tasks have it.
    protected Task(MCSSApi api, String GUID, String taskID, String taskName, boolean enabled) throws APIUnauthorizedException, IOException, APINotFoundException, APIInvalidTaskDetailsException, APINoServerAccessException {
        this.api = api;
        this.GUID = GUID;
        this.taskID = taskID;
        this.taskName = taskName;
        this.enabled = enabled;
        this.taskType = figureOutTaskType();
        this.taskJobType = figureOutTaskJobType();
        this.playerRequirement = figureOutPlayerRequirement();
    }

    private PlayerRequirement figureOutPlayerRequirement() throws APIUnauthorizedException, APINoServerAccessException, APINotFoundException, IOException {

        //Create the URL
        URL url = new URL(Endpoints.GET_TASK.getEndpoint()
                .replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID)
                .replace("{TASK_ID}", taskID));

        //Create the connection
        HttpURLConnection conn = createGetConnection(url);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check for errors
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //Get the response
        JSONObject response = new JSONObject(new JSONTokener(new InputStreamReader(conn.getInputStream())));
        conn.disconnect();

        //Get the player requirement
        return PlayerRequirement.findByVal(response.getInt("playerRequirement"));

    }

    private TaskJobType figureOutTaskJobType() throws APIInvalidTaskDetailsException, APIUnauthorizedException, IOException, APINotFoundException, APINoServerAccessException {

        if (Deleted) {
            throw new APIInvalidTaskDetailsException(Errors.TASK_DELETED.getMessage());
        }

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID).replace("{TASK_ID}", taskID));

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401, throw an APIUnauthorizedException
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

        //close connection
        conn.disconnect();

        JSONObject jobJson = json.getJSONObject("job");

        if (jobJson.has("action")) {
            return dev.le_app.mcss_api_java.TaskJobType.SERVER_ACTION;
        } else if (jobJson.has("commands")) {
            return dev.le_app.mcss_api_java.TaskJobType.RUN_COMMANDS;
        } else if (jobJson.has("backupIdentifier")) {
            return dev.le_app.mcss_api_java.TaskJobType.START_BACKUP;
        } else {
            throw new APIInvalidTaskDetailsException(Errors.INVALID_JOB_TYPE.getMessage());
        }

    }

    private TaskType figureOutTaskType() throws IOException, APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, APINoServerAccessException {


        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401 or 404, throw the relevant exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }
        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

        //close connection
        conn.disconnect();

        //parse the task type from the JSONObject "timing"
        JSONObject timing = json.getJSONObject("timing");
        if (timing.has("time")) {
            return dev.le_app.mcss_api_java.TaskType.FIXED_TIME;
        } else if (timing.has("interval")) {
            return dev.le_app.mcss_api_java.TaskType.INTERVAL;
        } else if (timing.has("timeless")) {
            return dev.le_app.mcss_api_java.TaskType.TIMELESS;
        } else {
            throw new APIInvalidTaskDetailsException(Errors.NO_TIMING_INFORMATION.getMessage());
        }
    }

    /**
     * @return the Task ID
     */
    public String getTaskID() {
        return taskID;
    }

    /**
     * @return the Task Name
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @return the Task Type
     */
    public TaskType getTaskType() {
        return taskType;
    }

    /**
     * @return the Task Job Type
     */
    public TaskJobType getTaskJobType() {
        return taskJobType;
    }

    /**
     * @return the Task Player Requirement
     */
    public PlayerRequirement getPlayerRequirement() {
        return playerRequirement;
    }
    /**
     * @return the enabled status of the Task
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Check if the task repeats at the set interval/fixed time
     * @return true if the task repeats at the set interval/fixed time
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server or task is not found
     * @throws APIInvalidTaskDetailsException if the task has no timing information
     * @throws IOException if there is an error connecting to the server
     */
    public boolean isRepeating() throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException, APINoServerAccessException {
        if (taskType == dev.le_app.mcss_api_java.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException(Errors.REPEAT_TIMELESS.getMessage());
        } else if (Deleted) {
            throw new APIInvalidTaskDetailsException(Errors.REPEAT_DELETED.getMessage());
        }

        //Create URL
        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //create a connection
        HttpURLConnection conn = createGetConnection(url);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401 or 404, throw the relevant exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

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
    public LocalTime getTime() throws IOException, APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, APINoServerAccessException {
        if (taskType == dev.le_app.mcss_api_java.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException(Errors.TIME_TIMELESS.getMessage());
        } else if (taskType == dev.le_app.mcss_api_java.TaskType.INTERVAL) {
            throw new APIInvalidTaskDetailsException(Errors.TIME_INTERVAL.getMessage());
        } else if (Deleted) {
            throw new APINotFoundException(Errors.TIME_DELETED.getMessage());
        }

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //create a connection
        HttpURLConnection conn = createGetConnection(url);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401 or 404, throw the relevant exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

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
            throw new APIInvalidTaskDetailsException(Errors.COULD_NOT_PARSE_TIME.getMessage());
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
    public long getInterval() throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException, APINoServerAccessException {

        //Check if the task has an interval
        if (taskType == dev.le_app.mcss_api_java.TaskType.FIXED_TIME) {
            throw new APIInvalidTaskDetailsException(Errors.INTERVAL_FIXED_TIME.getMessage());
        } else if (taskType == dev.le_app.mcss_api_java.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException(Errors.INTERVAL_TIMELESS.getMessage());
        } else if (Deleted) {
            throw new APINotFoundException(Errors.INTERVAL_DELETED.getMessage());
        }

        //Create the URL
        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //create a connection
        HttpURLConnection conn = createGetConnection(url);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401 or 404, throw the relevant exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

        //close connection
        conn.disconnect();

        //Extract the timing JSONObject
        JSONObject timing = json.getJSONObject("timing");
        return timing.getLong("interval");
    }

    public Job getJob() throws APINotFoundException {
        if (Deleted) {
            throw new APINotFoundException(Errors.JOB_DELETED.getMessage());
        }
        if (taskJobType == dev.le_app.mcss_api_java.TaskJobType.SERVER_ACTION) {
            return new ServerActionJob(api, GUID, taskID);
        } else if (taskJobType == dev.le_app.mcss_api_java.TaskJobType.RUN_COMMANDS) {
            return new RunCommandsJob(api, GUID, taskID);
        } else if (taskJobType == dev.le_app.mcss_api_java.TaskJobType.START_BACKUP) {
            return new BackupJob(api, GUID, taskID);
        } else {
            throw new APINotFoundException(Errors.INVALID_JOB_TYPE.getMessage());
        }
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
    public void setEnabled() throws IOException, APINotFoundException, APIUnauthorizedException, APIInvalidTaskDetailsException, APINoServerAccessException {

        if (taskType == dev.le_app.mcss_api_java.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException(Errors.ENABLE_TIMELESS.getMessage());
        } else if (Deleted) {
            throw new APIInvalidTaskDetailsException(Errors.ENABLE_DELETED.getMessage());
        }

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //create a connection
        HttpURLConnection conn = createPutConnection(url);


        String json = "{\"enabled\":true}";

        //connect to the server
        conn.connect();

        //write the JSON to the output stream
        conn.getOutputStream().write(json.getBytes());

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code indicates an error, throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 409:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        enabled = true;

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
    public void setDisabled() throws IOException, APINotFoundException, APIUnauthorizedException, APIInvalidTaskDetailsException, APINoServerAccessException {

        if (taskType == dev.le_app.mcss_api_java.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException(Errors.DISABLE_TIMELESS.getMessage());
        } else if (Deleted) {
            throw new APIInvalidTaskDetailsException(Errors.DISABLE_DELETED.getMessage());
        }
        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //create a connection
        HttpURLConnection conn = createPutConnection(url);

        String json = "{\"enabled\":false}";

        //connect to the server
        conn.connect();

        //write the JSON to the output stream
        conn.getOutputStream().write(json.getBytes());

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code indicates an error, throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 409:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        enabled = false;

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
    public void setInterval(long newInterval) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException, APINoServerAccessException {

        //Check if the task has the interval value and that it's not deleted
        if (taskType == dev.le_app.mcss_api_java.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException(Errors.INTERVAL_TIMELESS.getMessage());
        } else if (taskType == dev.le_app.mcss_api_java.TaskType.FIXED_TIME) {
            throw new APIInvalidTaskDetailsException(Errors.INTERVAL_FIXED_TIME.getMessage());
        } else if (Deleted) {
            throw new APIInvalidTaskDetailsException(Errors.INTERVAL_DELETED.getMessage());
        }

        //Check if the interval is valid
        if (newInterval < 1) {
            throw new APIInvalidTaskDetailsException(Errors.INTERVAL_GREATER_0.getMessage());
        }

        //Create the URL
        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //Create the connection
        HttpURLConnection conn = createPutConnection(url);

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
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 409:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //Close the connection
        conn.disconnect();
    }

    public void setTime(LocalTime newTime) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException, APINoServerAccessException {

        if (Deleted) {
            throw new APIInvalidTaskDetailsException(Errors.TIME_DELETED.getMessage());
        } else if (taskType == dev.le_app.mcss_api_java.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException(Errors.TIME_TIMELESS.getMessage());
        } else if (taskType == dev.le_app.mcss_api_java.TaskType.INTERVAL) {
            throw new APIInvalidTaskDetailsException(Errors.TIME_INTERVAL.getMessage());
        }

        //Create URL
        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //Create connection
        HttpURLConnection conn = createPutConnection(url);

        //Create JSON with the newTime
        String json = "{\"timing\":{\"time\":\"" + newTime.toString() + "\"}}";

        //Open the connection
        conn.connect();

        //Write the JSON to the output stream
        conn.getOutputStream().write(json.getBytes());

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an error, throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 409:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
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
    public void runTask() throws IOException, APINotFoundException, APIUnauthorizedException, APIInvalidTaskDetailsException, APINoServerAccessException {

        if (Deleted) {
            throw new APIInvalidTaskDetailsException(Errors.RUN_DELETED.getMessage());
        }

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //create a connection
        HttpURLConnection conn = createPostConnection(url);

        //connect to the server
        conn.connect();
        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code indicates an error, throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
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
    public void setRepeating(boolean repeat) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException, APINoServerAccessException {

        if (Deleted) {
            throw new APIInvalidTaskDetailsException(Errors.REPEAT_DELETED.getMessage());
        } else if (taskType == dev.le_app.mcss_api_java.TaskType.TIMELESS) {
            throw new APIInvalidTaskDetailsException(Errors.REPEAT_TIMELESS.getMessage());
        }

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //create a connection
        HttpURLConnection conn = createPutConnection(url);

        //connect to the server
        conn.connect();

        //create the JSON
        String json = " { \"timing\" { \"repeat\": " + repeat + " } }";

        //write the JSON to the output stream
        conn.getOutputStream().write(json.getBytes());

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code indicates an error, throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 409:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
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
    public void changeName(String newName) throws APIInvalidTaskDetailsException, APIUnauthorizedException, APINotFoundException, IOException, APINoServerAccessException {

        if (Deleted) {
            throw new APIInvalidTaskDetailsException(Errors.CHANGE_NAME_DELETED.getMessage());
        }

        //Check if new name contains special characters
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(newName);
        if (m.find()) {
            throw new APIInvalidTaskDetailsException(Errors.NAME_SPECIAL_CHAR.getMessage());
        }

        //Create URL
        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //Create connection
        HttpURLConnection conn = createPutConnection(url);

        //Create JSON
        String json = "{\"name\":\"" + newName + "\"}";

        //Connect to server
        conn.connect();

        //Write JSON to output stream
        conn.getOutputStream().write(json.getBytes());

        //Get response code
        int responseCode = conn.getResponseCode();

        //If response code indicates an error, throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 409:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            default:
                throw new APIInvalidTaskDetailsException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //Close connection
        conn.disconnect();

        taskName = newName;

    }

    public void setPlayerRequirement(PlayerRequirement playerRequirement) throws APIUnauthorizedException, APINotFoundException, APINoServerAccessException, APIInvalidTaskDetailsException, IOException {
        //Create the URL
        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

        //Create connection
        HttpURLConnection conn = createPutConnection(url);

        //Create JSON - field name is playerRequirement - value is INTEGER
        String json = "{\"playerRequirement\":\"" + playerRequirement.getValue() + "\"}";

        //Connect to server
        conn.connect();

        //Write JSON to output stream
        conn.getOutputStream().write(json.getBytes());

        //Get response code
        int responseCode = conn.getResponseCode();

        //If response code indicates an error, throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 409:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //Close connection
        conn.disconnect();

        this.playerRequirement = playerRequirement;
    }



    /**
     * Delete the task from the API
     * @throws IOException if there is an error connecting to the server
     * @throws APINotFoundException if the server returns a 404 response code
     * @throws APIUnauthorizedException if the server returns a 401 response code
     */
    public void deleteTask() throws IOException, APINotFoundException, APIUnauthorizedException, APINoServerAccessException {

        if (Deleted) {
            throw new APINotFoundException(Errors.TASK_ALREADY_DELETED.getMessage());
        }

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", taskID));

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
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new APINotFoundException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        Deleted = true;
        //close connection
        conn.disconnect();
    }

    private HttpURLConnection createGetConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);

        return conn;
    }

    private HttpURLConnection createPostConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        return conn;
    }

    private HttpURLConnection createPutConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        return conn;
    }

    private HttpURLConnection createDeleteConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("DELETE");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoOutput(true);

        return conn;
    }
}
