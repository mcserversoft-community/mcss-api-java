package com.mcssapi;

import com.mcssapi.exceptions.APIInvalidTaskDetailsException;
import com.mcssapi.exceptions.APINotFoundException;
import com.mcssapi.exceptions.APIUnauthorizedException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class runCommandsJob extends Job {

    private MCSSApi api;
    private String GUID;
    private String TaskID;

    public runCommandsJob(MCSSApi api, String GUID, String TaskID) {
        this.api = api;
        this.GUID = GUID;
        this.TaskID = TaskID;
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

        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/" + TaskID);

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
        if (responseCode == 401) {
            throw new APIUnauthorizedException("API Token is invalid or expired.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("TaskID or ServerID invalid.");
        }

        //Save the response in a jsonobject
        JSONObject json = new JSONObject(conn.getOutputStream());

        //Get the Job object
        JSONObject job = json.getJSONObject("job");

        //Get the commands array
        JSONArray commands = job.getJSONArray("commands");

        if (commands.length() == 0) {
            throw new APIInvalidTaskDetailsException("No commands found for this task.");
        }

        //Get the commands array
        ArrayList<String> commandsArray = new ArrayList<String>();

        for (int i = 0; i < commands.length(); i++) {
            commandsArray.add(commands.getString(i));
        }

        conn.disconnect();

        return commandsArray;

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
    public void setCommands(String... commands) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {

        if (commands.length == 0) {
            throw new APIInvalidTaskDetailsException("No commands supplied for this task.");
        }

        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/" + TaskID);

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
        if (responseCode == 401) {
            throw new APIUnauthorizedException("API Token is invalid or expired.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("TaskID or ServerID invalid.");
        } else if (responseCode == 409) {
            throw new APIInvalidTaskDetailsException("Invalid Job Details.");
        }
    }
}
