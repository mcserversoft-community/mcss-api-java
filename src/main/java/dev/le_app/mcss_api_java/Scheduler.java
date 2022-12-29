package dev.le_app.mcss_api_java;

import dev.le_app.mcss_api_java.exceptions.APIInvalidTaskDetailsException;
import dev.le_app.mcss_api_java.exceptions.APINoServerAccessException;
import dev.le_app.mcss_api_java.exceptions.APINotFoundException;
import dev.le_app.mcss_api_java.exceptions.APIUnauthorizedException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scheduler of a server
 * Used to create and manage tasks
 */
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
     * @throws APINoServerAccessException if the API key does not have access to the server
     */
    public int getTotalTaskAmount() throws APIUnauthorizedException, APINotFoundException, IOException, APINoServerAccessException {

        //GET /api/v1/servers/{SERVER_ID}/scheduler/
        URL url = new URL(Endpoints.GET_SCHEDULER.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

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
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

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
     * @throws APINoServerAccessException if the API key does not have access to the server
     */
    public int getTotalTaskAmount(TaskType filter) throws APIUnauthorizedException, APINotFoundException, IOException, APINoServerAccessException {

        //GET /api/v1/servers/{SERVER_ID}/scheduler/
        URL url = new URL(Endpoints.GET_SCHEDULER.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID).replace("{FILTER}", filter.toString()));

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

        //if the response code indicates an error, throw an exception
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
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

        //close connection
        conn.disconnect();

        //return the total task amount
        return json.getInt("tasks");
    }

    /**
     * Get an arraylist of all the tasks
     * @return ArrayList of all the tasks
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an IO error (e.g. server is offline)
     * @throws APIInvalidTaskDetailsException if the task details are invalid
     * @throws APINoServerAccessException if the API key does not have access to the server
     */
    public ArrayList<Task> getTasks() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException, APINoServerAccessException {
        //GET /api/v1/servers/{SERVER_ID}/scheduler/
        URL url = new URL(Endpoints.GET_TASK_LIST.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

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

        //if the response code indicates an error, throw an exception
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


        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONArray jsonArray = new JSONArray(new JSONTokener(reader));

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

    /**
     * Create a new Interval Task
     * @param Name Name of the task to create
     * @param Enabled Whether the task should be enabled or not
     * @param repeating Whether the task should repeat or not
     * @param interval The interval between each task
     * @param job The ENUM of the job type to run
     * @param jobData The data of the job to run
     *                for backup: the backup ID
     *                for command: the command(s) to run, separated with a semicolon (;)
     *                for action: the action to run (start, stop, restart, kill)
     * @return The created task
     * @throws APIInvalidTaskDetailsException if the task details are invalid
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an IO error (e.g. server is offline)
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API key does not have access to the server
     */
    public Task createIntervalTask(String Name, Boolean Enabled, Boolean repeating, int interval, TaskJobType job, String jobData)
            throws APIInvalidTaskDetailsException, APIUnauthorizedException, IOException,
            APINotFoundException, APINoServerAccessException {

        //Check if the name contains special characters
        Pattern p = Pattern.compile("^[a-zA-Z0-9 ]*$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(Name);
        if (m.find()) {
            throw new APIInvalidTaskDetailsException(Errors.NAME_SPECIAL_CHAR.getMessage());
        }

        URL url = new URL(Endpoints.CREATE_TASK.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

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

        //jobJSON is for commands
        // "job": {
        //   "commands": [
        //      "command"
        //   ]
        // }

        //Populate the JSONObject appropriately
        switch (job) {
            case RUN_COMMANDS:
                JSONArray commands = new JSONArray();
                //Split the commands by semicolon
                String[] commandArray = jobData.split(";");
                for (String command : commandArray) {
                    commands.put(command);
                }
                jobJson.put("commands", commands);
                break;
            case SERVER_ACTION:
                //parse the action to the ServerAction ENUM
                ServerAction action = ServerAction.valueOf(jobData.toUpperCase());
                jobJson.put("action", action.getValue());
                break;
            case START_BACKUP:
                jobJson.put("backupIdentifier", jobData);
                break;
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
        switch (responseCode) {
            case 201:
                break;
            case 400:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //If the response code is 201, get the task GUID and return a new Task object
        JSONObject json = new JSONObject(conn.getOutputStream());
        String taskGUID = json.getString("taskId");

        return new Task(api, GUID, taskGUID, Name, Enabled);
    }

    /**
     * Create a new Fixed Time task
     * @param Name Name of the task to create
     * @param Enabled Whether the task should be enabled or not
     * @param repeating Whether the task should repeat or not
     * @param time The time to run the task
     * @param job The ENUM of the job type to run
     * @param jobData The data of the job to run
     *                for backup: the backup ID
     *                for command: the command(s) to run, separated with a semicolon (;)
     *                for action: the action to run (start, stop, restart, kill) (case insensitive - no spaces)
     * @return The created task
     * @throws APIInvalidTaskDetailsException if the task details are invalid
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an IO error (e.g. server is offline)
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API key does not have access to the server
     */
    public Task createFixedTimeTask(String Name, Boolean Enabled, Boolean repeating, LocalTime time, TaskJobType job, String jobData) throws APIInvalidTaskDetailsException, APIUnauthorizedException, IOException, APINotFoundException, APINoServerAccessException {

        //Check if the name contains special characters
        Pattern p = Pattern.compile("^[a-zA-Z0-9 ]*$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(Name);
        if (m.find()) {
            throw new APIInvalidTaskDetailsException(Errors.NAME_SPECIAL_CHAR.getMessage());
        }

        URL url = new URL(Endpoints.CREATE_TASK.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

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
        switch (job) {
            case RUN_COMMANDS:
                JSONArray commands = new JSONArray();
                //Split the commands by semicolon
                String[] commandArray = jobData.split(";");
                for (String command : commandArray) {
                    commands.put(command);
                }
                jobJson.put("commands", commands);
                break;
            case SERVER_ACTION:
                //parse the action to the ServerAction ENUM
                ServerAction action = ServerAction.valueOf(jobData.toUpperCase());
                jobJson.put("action", action.getValue());
                break;
            case START_BACKUP:
                jobJson.put("backupIdentifier", jobData);
                break;
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
        switch (responseCode) {
            case 201:
                break;
            case 400:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //If the response code is 201, get the task GUID and return a new Task object
        JSONObject json = new JSONObject(conn.getOutputStream());
        String taskGUID = json.getString("taskId");

        return new Task(api, GUID, taskGUID, Name, Enabled);
    }

    /**
     * Create a new Interval Task
     * @param Name Name of the task to create
     * @param Enabled Whether the task should be enabled or not
     * @param job The job type to run (ENUM)
     * @param jobData The data of the job to run
     *                for backup: the backup ID /
     *                for command: the command(s) to run, separated with a semicolon (;) /
     *                for action: the action to run (start, stop, restart, kill) (case insensitive - no spaces)
     * @return The created task
     * @throws APIInvalidTaskDetailsException if the task details are invalid
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an IO error (e.g. server is offline)
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API key does not have access to the server
     */
    public Task createTimelessTask(String Name, Boolean Enabled, TaskJobType job, String jobData)
            throws APIInvalidTaskDetailsException, APIUnauthorizedException, IOException, APINotFoundException,
            APINoServerAccessException {

        //Check if the name contains special characters
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(Name);
        if (m.find()) {
            throw new APIInvalidTaskDetailsException(Errors.NAME_SPECIAL_CHAR.getMessage());
        }

        URL url = new URL(Endpoints.CREATE_TASK.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        //create a JSONObject to send to the server
        JSONObject mainJson = new JSONObject();
        mainJson.put("name", Name);
        mainJson.put("enabled", Enabled);

        //Create the Job JSONObject
        JSONObject jobJson = new JSONObject();

        //Populate the JSONObject appropriately
        switch (job) {
            case RUN_COMMANDS:
                JSONArray commands = new JSONArray();
                //Split the commands by semicolon
                String[] commandArray = jobData.split(";");
                for (String command : commandArray) {
                    commands.put(command);
                }
                jobJson.put("commands", commands);
                break;
            case SERVER_ACTION:
                //parse the action to the ServerAction ENUM
                ServerAction action = ServerAction.valueOf(jobData.toUpperCase());
                jobJson.put("action", action.getValue());
                break;
            case START_BACKUP:
                jobJson.put("backupIdentifier", jobData);
                break;
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
        switch (responseCode) {
            case 201:
                break;
            case 400:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //If the response code is 201, get the task GUID and return a new Task object
        JSONObject json = new JSONObject(conn.getOutputStream());
        String taskGUID = json.getString("taskId");

        return new Task(api, GUID, taskGUID, Name, Enabled);
    }

    @Override
    public String toString() {
        return "Scheduler{" +
                "GUID='" + GUID + '\'' +
                '}';
    }
}
