package dev.le_app;

import dev.le_app.exceptions.APIInvalidTaskDetailsException;
import dev.le_app.exceptions.APINotFoundException;
import dev.le_app.exceptions.APIUnauthorizedException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RunCommandsJob extends Job {

    private MCSSApi api;
    private String GUID;
    private String TaskID;

    public RunCommandsJob(MCSSApi api, String GUID, String TaskID) {
        this.api = api;
        this.GUID = GUID;
        this.TaskID = TaskID;
    }

    @Override
    public ServerAction getAction(){
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }

    /**
     * Get an array list of commands that the task executes
     * @return ArrayList of commands
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an IO error (e.g. server is offline)
     * @throws APIInvalidTaskDetailsException if the task is not found
     */
    @Override
    public ArrayList<String> getCommands() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TaskID}", TaskID));

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

        //if the response code indicates an error, throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            default:
                throw new APINotFoundException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //Save the response in a jsonobject
        JSONObject json = new JSONObject(conn.getOutputStream());

        //Get the Job object
        JSONObject job = json.getJSONObject("job");

        //Get the commands array
        JSONArray commands = job.getJSONArray("commands");

        if (commands.length() == 0) {
            throw new APIInvalidTaskDetailsException(Errors.COMMANDS_NOT_FOUND.getMessage());
        }

        //Get the commands array
        ArrayList<String> commandsArray = new ArrayList<String>();

        for (int i = 0; i < commands.length(); i++) {
            commandsArray.add(commands.getString(i));
        }

        conn.disconnect();

        return commandsArray;

    }

    @Override
    public String getBackupGUID() {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }

    @Override
    public Job setAction(ServerAction action) {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }


    /**
     * Update the commands to be executed by the task
     * @param commands list of commands to be executed by the task
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server is not found
     * @throws APIInvalidTaskDetailsException if the task is not found
     * @throws IOException if there is an IO error (e.g. server is offline)
     */
    @Override
    public Job setCommands(String... commands) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {

        if (commands.length == 0) {
            throw new APIInvalidTaskDetailsException(Errors.COMMANDS_NOT_GIVEN.getMessage());
        }

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TaskID}", TaskID));

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        //Create the json object
        String json = "{\"job\" : {\"commands\" : [";

        for (int i = 0; i < commands.length; i++) {
            json += "\"" + commands[i] + "\"";
            if (i != commands.length - 1) {
                json += ",";
            }
        }

        json += "]}}";

        //Connect to the server
        conn.connect();

        //Send the json object
        conn.getOutputStream().write(json.getBytes());

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an error, throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 409:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            default:
                throw new APINotFoundException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }
        return this;
    }

    @Override
    public Job setBackupGUID(String backupGUID) {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }
}
