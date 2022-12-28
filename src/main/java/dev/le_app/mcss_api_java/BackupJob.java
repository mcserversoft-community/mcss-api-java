package dev.le_app.mcss_api_java;

import dev.le_app.mcss_api_java.exceptions.APIInvalidTaskDetailsException;
import dev.le_app.mcss_api_java.exceptions.APINotFoundException;
import dev.le_app.mcss_api_java.exceptions.APIUnauthorizedException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Represents a backup job.
 */
public class BackupJob extends Job {

    private final MCSSApi api;
    private final String GUID;
    private final String TaskID;


    /**
     * Constructs a new BackupJob.
     * @param api instance of MCSSApi
     * @param GUID GUID of the backup job
     * @param TaskID TaskID of the backup job
     */
    public BackupJob(MCSSApi api, String GUID, String TaskID) {
        this.api = api;
        this.GUID = GUID;
        this.TaskID = TaskID;
    }

    @Override
    public ServerAction getAction() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }

    @Override
    public ArrayList<String> getCommands() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
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

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{SERVER_ID}", GUID)
                .replace("{TASK_ID}", TaskID));

        //Create the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);

        //Connect to the server
        conn.connect();

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
            default:
                throw new APINotFoundException(Errors.NOT_RECOGNIZED.getMessage()+responseCode);
        }

        //Get the response body
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

        //Get the job object
        JSONObject job = json.getJSONObject("job");

        //return the backupIdentifier
        return job.getString("backupIdentifier");
    }

    @Override
    public Job setAction(ServerAction action) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }

    @Override
    public Job setCommands(String... commands) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException {
        throw new UnsupportedOperationException(Errors.METHOD_NOT_SUPPORTED.getMessage());
    }

    @Override
    public Job setBackupGUID(String backupGUID) throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {

        URL url = new URL(Endpoints.GET_TASK.getEndpoint().replace("{IP}", api.IP).replace("{SERVER_ID}", GUID)
                .replace("{TASK_ID}", TaskID));

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
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 400:
                throw new APIInvalidTaskDetailsException(Errors.INVALID_TASK_DETAILS.getMessage());
            default:
                throw new APINotFoundException(Errors.NOT_RECOGNIZED.getMessage()+responseCode);
        }

        //Close the connection
        conn.disconnect();
        return this;
    }
}
