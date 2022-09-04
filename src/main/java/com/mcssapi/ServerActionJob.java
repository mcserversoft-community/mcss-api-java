package com.mcssapi;

import com.mcssapi.exceptions.APIInvalidTaskDetailsException;
import com.mcssapi.exceptions.APINotFoundException;
import com.mcssapi.exceptions.APIUnauthorizedException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ServerActionJob extends Job {

    private final MCSSApi api;

    private final String GUID;

    private final String TaskID;

    public ServerActionJob(MCSSApi api, String GUID, String TaskID) {
        this.api = api;
        this.GUID = GUID;
        this.TaskID = TaskID;
    }

    /**
     * Get the action of the job.
     * @return The action of the job.
     * @throws APIUnauthorizedException If the API key is not valid.
     * @throws APINotFoundException If the server is not found.
     * @throws IOException If there is an IO error (e.g. server is offline).
     * @throws APIInvalidTaskDetailsException If the task is not found.
     */
    @Override
    public ServerAction getAction() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", TaskID));

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

        //Get the action
        int action = job.getInt("action");

        //close the connection
        conn.disconnect();

        //Search the action
        for (ServerAction a : ServerAction.values()) {
            //if the action is found, return it
            if (a.getValue() == action) {
                return a;
            }
        }

        //if the action is not found, throw an exception
        throw new APIInvalidTaskDetailsException(Errors.ACTION_NOT_FOUND.getMessage());

    }

    @Override
    public ArrayList<String> getCommands() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }

    @Override
    public String getBackupGUID() {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }

    /**
     * The action to be performed on the server.
     * @param action the action to be performed on the server.
     * @throws APIUnauthorizedException If the API key is not valid.
     * @throws APINotFoundException If the server is not found.
     * @throws IOException If there is an IO error (e.g. server is offline).
     * @throws APIInvalidTaskDetailsException If the task is not found.
     */
    public Job setAction(ServerAction action) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{GUID}", GUID)
                .replace("{TASK_ID}", TaskID));

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");

        //create the json object to send
        String json = "{\"job\" : {\"action\" : " + action.getValue() + "}}";

        //set the output stream to the json object
        conn.setDoOutput(true);

        //connect to the server
        conn.connect();

        //write the json object to the output stream
        conn.getOutputStream().write(json.getBytes());

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
            case 409:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            default:
                throw new APINotFoundException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //close the connection
        conn.disconnect();
        return this;
    }

    @Override
    public Job setCommands(String... commands) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }

    @Override
    public Job setBackupGUID(String backupGUID) {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }

}
