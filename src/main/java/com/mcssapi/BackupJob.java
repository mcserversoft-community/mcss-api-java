package com.mcssapi;

import com.mcssapi.exceptions.APIInvalidTaskDetailsException;
import com.mcssapi.exceptions.APINotFoundException;
import com.mcssapi.exceptions.APIUnauthorizedException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class BackupJob extends Job {

    private MCSSApi api;
    private String GUID;
    private String TaskID;


    public BackupJob(MCSSApi api, String GUID, String TaskID) {
        this.api = api;
        this.GUID = GUID;
        this.TaskID = TaskID;
    }

    /**
     * Get the Backup ID of the backup executed by the task
     * @return The Backup ID of the backup executed by the task
     * @throws APIUnauthorizedException If the API key is not valid.
     * @throws APINotFoundException If the server is not found.
     * @throws IOException If there is an IO error (e.g. server is offline).
     */
    @Override
    public String getBackupGUID() throws APIUnauthorizedException, APINotFoundException, IOException {

        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/" + TaskID);

        //Create the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);

        //Connect to the server
        conn.connect();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an error, throw the appropriate exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException("API Token is invalid or expired.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("TaskID or ServerID invalid.");
        }

        //Get the response body
        JSONObject json = new JSONObject(conn.getOutputStream());

        //Get the job object
        JSONObject job = json.getJSONObject("job");

        //return the backupIdentifier
        return job.getString("backupIdentifier");
    }

    @Override
    public void setBackupGUID(String backupGUID) throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {

        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/" + TaskID);

        //Create the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set the request method and request properties
        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        //Create the json object
        String json = "{\"job\" : {\"backupIdentifier\" : \"" + backupGUID + "\"}}";

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
            throw new APIInvalidTaskDetailsException("Invalid backupIdentifier.");
        }

        //Close the connection
        conn.disconnect();
    }
}
