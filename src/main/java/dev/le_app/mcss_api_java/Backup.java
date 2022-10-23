package dev.le_app.mcss_api_java;

import dev.le_app.mcss_api_java.exceptions.APINoServerAccessException;
import dev.le_app.mcss_api_java.exceptions.APINotFoundException;
import dev.le_app.mcss_api_java.exceptions.APIUnauthorizedException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Backup {

    //BACKUP CLASS

    private final String backupID;
    private String backupName;
    private String destination;
    private boolean suspended;
    private boolean deleteOldBackups;
    private BackupCompressionType compressionType;
    private ArrayList<String> fileBlacklist = new ArrayList<>();
    private ArrayList<String> folderBlacklist = new ArrayList<>();
    private LocalDateTime completedAt;
    private LastBackupState lastBackupState;

    private boolean deleted;
    private final String serverID;

    MCSSApi api;

    /**
     * Creates a new Backup object - ONLY MADE BY THE SERVER OBJECT
     *
     * @param api      MCSS API instance
     * @param serverID Server ID of the origin server
     * @param backupID Backup ID of the backup
     * @throws APIUnauthorizedException   If the API key is invalid
     * @throws IOException                If there is an error connecting to the API
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException       If the server or backup does not exist
     */
    protected Backup(MCSSApi api, String serverID, String backupID) throws APIUnauthorizedException, IOException, APINoServerAccessException, APINotFoundException {
        this.api = api;
        this.backupID = backupID;
        this.serverID = serverID;

        updateDetails();
    }

    //GETTERS

    /**
     * Gets the backup ID
     *
     * @return STRING Backup ID
     * @throws IllegalStateException If the backup has been deleted
     */
    public String getBackupID() throws IllegalStateException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }
        return backupID;
    }

    /**
     * Gets the backup name
     *
     * @return STRING Backup name
     * @throws IllegalStateException If the backup has been deleted
     */
    public String getBackupName() throws IllegalStateException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }
        return backupName;
    }

    /**
     * Gets the destination of the backup
     *
     * @return STRING Destination of the backup
     * @throws IllegalStateException If the backup has been deleted
     */
    public String getDestination() throws IllegalStateException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }
        return destination;
    }

    /**
     * Gets whether the backup is suspended
     *
     * @return BOOLEAN Whether the backup is suspended
     * @throws IllegalStateException If the backup has been deleted
     */
    public boolean isSuspended() throws IllegalStateException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }
        return suspended;
    }

    /**
     * Gets whether the backup deletes old backups
     *
     * @return BOOLEAN Whether the backup deletes old backups
     * @throws IllegalStateException If the backup has been deleted
     */
    public boolean isDeleteOldBackups() throws IllegalStateException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }
        return deleteOldBackups;
    }

    /**
     * Gets the compression type of the backup
     *
     * @return BackupCompressionType Compression type of the backup
     * @throws IllegalStateException If the backup has been deleted
     */
    public BackupCompressionType getCompressionType() throws IllegalStateException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }
        return compressionType;
    }

    /**
     * Gets the file blacklist of the backup
     *
     * @return ARRAYLIST File blacklist of the backup
     * @throws IllegalStateException If the backup has been deleted
     */
    public ArrayList<String> getFileBlacklist() throws IllegalStateException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }
        return fileBlacklist;
    }

    /**
     * Gets the folder blacklist of the backup
     *
     * @return ARRAYLIST Folder blacklist of the backup
     * @throws IllegalStateException If the backup has been deleted
     */
    public ArrayList<String> getFolderBlacklist() throws IllegalStateException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }
        return folderBlacklist;
    }

    /**
     * Gets the last completion date/time of the backup
     *
     * @return LOCALDATETIME Completed at date of the backup
     * @throws IllegalStateException If the backup has been deleted
     */
    public LocalDateTime getCompletedAt() throws IllegalStateException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }
        return completedAt;
    }

    /**
     * Gets the last backup state of the backup
     * This value is updated every time the method is run, hence why all the other exceptions
     *
     * @return LastBackupState Last backup state of the backup
     * @throws IllegalStateException If the backup has been deleted
     * @throws APIUnauthorizedException   If the API key is invalid
     * @throws IOException                If there is an error connecting to the API
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException       If the server or backup does not exist
     */
    public LastBackupState getLastBackupState() throws IllegalStateException, APIUnauthorizedException, IOException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }

        updateDetails();

        return lastBackupState;
    }

    //SETTERS
    //Unfortunately, the best way to do all the setters is to send a request to the API to update the backup every time

    /**
     * Sets the backup name
     *
     * @param backupName Backup name
     * @throws IllegalStateException If the backup has been deleted
     * @throws APIUnauthorizedException If the API key is invalid
     * @throws IOException If there is an error connecting to the API
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException If the server or backup does not exist
     */
    public void setBackupName(String backupName) throws IllegalStateException, APIUnauthorizedException, IOException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }


        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{serverID}", serverID).
                replace("{backupID}", backupID).replace("{IP}", api.IP));

        //Create the PUT connection
        HttpURLConnection conn = createPutConnection(url);

        //Create the JSON object
        JSONObject json = new JSONObject();

        json.put("name", backupName);

        //Open connection
        conn.connect();

        //Write the JSON to the connection
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

        writer.write(json.toString());

        writer.flush();

        writer.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check the response code
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

        //Close the connection
        conn.disconnect();

        //Update the name with the new edit
        this.backupName = backupName;

    }

    /**
     * Sets the destination of the backup
     *
     * @param destination Absolute PATH - destination of the backup - Use double \\ for folders
     * @throws IllegalStateException If the backup has been deleted
     * @throws APIUnauthorizedException If the API key is invalid
     * @throws IOException If there is an error connecting to the API
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException If the server or backup does not exist
     */
    public void setDestination(String destination) throws IllegalStateException, APIUnauthorizedException, IOException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }

        //check if the destination is valid (WINDOWS PATH)
        if (!destination.matches("^[A-Z]:\\\\.*")) {
            throw new IllegalArgumentException("Invalid destination");
        }


        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{serverID}", serverID).
                replace("{backupID}", backupID).replace("{IP}", api.IP));

        //Create the PUT connection
        HttpURLConnection conn = createPutConnection(url);

        //Create the JSON object
        JSONObject json = new JSONObject();

        json.put("destination", destination);

        //Open connection
        conn.connect();

        //Write the JSON to the connection
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json.toString());
        writer.flush();
        writer.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check the response code
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

        //Close the connection
        conn.disconnect();

        //Update the destination with the new edit
        this.destination = destination;
    }

    /**
     * Sets the compression type of the backup
     *
     * @param compressionType Compression type of the backup
     * @throws IllegalStateException If the backup has been deleted
     * @throws APIUnauthorizedException If the API key is invalid
     * @throws IOException If there is an error connecting to the API
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException If the server or backup does not exist
     */
    public void setCompressionType(BackupCompressionType compressionType) throws IllegalStateException, APIUnauthorizedException, IOException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }

        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{serverID}", serverID).
                replace("{backupID}", backupID).replace("{IP}", api.IP));

        //Create the PUT connection
        HttpURLConnection conn = createPutConnection(url);

        //Create the JSON object
        JSONObject json = new JSONObject();

        json.put("compression", compressionType.getValue());

        //Open connection
        conn.connect();

        //Write the JSON to the connection
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json.toString());
        writer.flush();
        writer.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check the response code
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

        //Close the connection
        conn.disconnect();

        //Update the compression type with the new edit
        this.compressionType = compressionType;
    }

    /**
     * Sets if the backup should suspend the server (shutdown) before creating the backup
     * @param suspendServer If the server should be suspended
     * @throws IllegalStateException If the backup has been deleted
     * @throws APIUnauthorizedException If the API key is invalid
     * @throws IOException If there is an error connecting to the API
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException If the server or backup does not exist
     */
    public void setSuspendServer(boolean suspendServer) throws IllegalStateException, APIUnauthorizedException, IOException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }

        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{serverID}", serverID).
                replace("{backupID}", backupID).replace("{IP}", api.IP));

        //Create the PUT connection
        HttpURLConnection conn = createPutConnection(url);

        //Create the JSON object
        JSONObject json = new JSONObject();

        json.put("suspend", suspendServer);

        //Open connection
        conn.connect();

        //Write the JSON to the connection
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json.toString());
        writer.flush();
        writer.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check the response code
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

        //Close the connection
        conn.disconnect();

        //Update the "suspend server" variable with the new edit
        this.suspended = suspendServer;
    }

    /**
     * Sets if old backups should be deleted (following MCSS config)
     *
     * @param deleteOldBackups If old backups should be deleted
     * @throws IllegalStateException If the backup has been deleted
     * @throws APIUnauthorizedException If the API key is invalid
     * @throws IOException If there is an error connecting to the API
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException If the server or backup does not exist
     */
    public void setDeleteOldBackups(boolean deleteOldBackups) throws IllegalStateException, APIUnauthorizedException, IOException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }

        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{serverID}", serverID).
                replace("{backupID}", backupID).replace("{IP}", api.IP));

        //Create the PUT connection
        HttpURLConnection conn = createPutConnection(url);

        //Create the JSON object
        JSONObject json = new JSONObject();

        json.put("deleteOld", deleteOldBackups);

        //Open connection
        conn.connect();

        //Write the JSON to the connection
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json.toString());
        writer.flush();
        writer.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check the response code
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

        //Close the connection
        conn.disconnect();

        //Update the "delete old backups" variable with the new edit
        this.deleteOldBackups = deleteOldBackups;
    }

    /**
     * Sets the file blacklist for the backup
     * @param fileBlacklist ArrayList of the new blacklist
     * @throws IOException If there is an error connecting to the API
     * @throws APIUnauthorizedException If the API key is invalid
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException If the server or backup does not exist
     */
    public void setFileBlacklist(ArrayList<String> fileBlacklist) throws IOException, APIUnauthorizedException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }

        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{serverID}", serverID).
                replace("{backupID}", backupID).replace("{IP}", api.IP));

        //Create the PUT connection
        HttpURLConnection conn = createPutConnection(url);

        //Create the JSON object
        JSONObject json = new JSONObject();

        //Create the JSON array
        JSONArray array = new JSONArray();

        //Add all the files to the array
        for (String file : fileBlacklist) {
            array.put(file);
        }

        //Add the array to the JSON object
        json.put("fileBlacklist", array);

        //Open connection
        conn.connect();

        //Write the JSON to the connection
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json.toString());
        writer.flush();
        writer.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check the response code
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

        //Close the connection
        conn.disconnect();

        //Update the "file blacklist" variable with the new edit
        this.fileBlacklist = fileBlacklist;

    }

    /**
     * Sets the file blacklist for the backup
     * @param fileBlacklist collection of strings of the new blacklist
     * @throws IOException If there is an error connecting to the API
     * @throws APIUnauthorizedException If the API key is invalid
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException If the server or backup does not exist
     */
    public void setFileBlacklist(String... fileBlacklist) throws IOException, APIUnauthorizedException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }

        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{serverID}", serverID).
                replace("{backupID}", backupID).replace("{IP}", api.IP));

        //Create the PUT connection
        HttpURLConnection conn = createPutConnection(url);

        //Create the JSON object
        JSONObject json = new JSONObject();

        //Create the JSON array
        JSONArray array = new JSONArray();

        //create the arraylist of the blacklist
        ArrayList<String> blacklist = new ArrayList<>();

        //Add all the files to the array
        for (String file : fileBlacklist) {
            blacklist.add(file);
            array.put(file);
        }

        //Add the array to the JSON object
        json.put("fileBlacklist", array);

        //Open connection
        conn.connect();

        //Write the JSON to the connection
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json.toString());
        writer.flush();
        writer.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check the response code
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

        //Close the connection
        conn.disconnect();

        //Update the "file blacklist" variable with the new edit
        this.fileBlacklist = blacklist;

    }

    /**
     * Sets the folder blacklist for the backup
     * @param folderBlacklist ArrayList of the new folder blacklist
     * @throws IOException If there is an error connecting to the API
     * @throws APIUnauthorizedException If the API key is invalid
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException If the server or backup does not exist
     */
    public void setFolderBlacklist(ArrayList<String> folderBlacklist) throws IOException, APIUnauthorizedException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }

        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{serverID}", serverID).
                replace("{backupID}", backupID).replace("{IP}", api.IP));

        //Create the PUT connection
        HttpURLConnection conn = createPutConnection(url);

        //Create the JSON object
        JSONObject json = new JSONObject();

        //Create the JSON array
        JSONArray array = new JSONArray();

        //Add all the files to the array
        for (String file : folderBlacklist) {
            array.put(file);
        }

        //Add the array to the JSON object
        json.put("folderBlacklist", array);

        //Open connection
        conn.connect();

        //Write the JSON to the connection
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json.toString());
        writer.flush();
        writer.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check the response code
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

        //Close the connection
        conn.disconnect();

        //Update the "folder blacklist" variable with the new edit
        this.folderBlacklist = folderBlacklist;

    }

    /**
     * Sets the folder blacklist for the backup
     * @param folderBlacklist Collection of strings of the new folder blacklist
     * @throws IOException If there is an error connecting to the API
     * @throws APIUnauthorizedException If the API key is invalid
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException If the server or backup does not exist
     */
    public void setFolderBlacklist(String... folderBlacklist) throws IOException, APIUnauthorizedException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }

        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{serverID}", serverID).
                replace("{backupID}", backupID).replace("{IP}", api.IP));

        //Create the PUT connection
        HttpURLConnection conn = createPutConnection(url);

        //Create the JSON object
        JSONObject json = new JSONObject();

        //Create the JSON array
        JSONArray array = new JSONArray();

        //create the arraylist of the blacklist
        ArrayList<String> blacklist = new ArrayList<>();

        //Add all the files to the array
        for (String file : folderBlacklist) {
            blacklist.add(file);
            array.put(file);
        }

        //Add the array to the JSON object
        json.put("folderBlacklist", array);

        //Open connection
        conn.connect();

        //Write the JSON to the connection
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json.toString());
        writer.flush();
        writer.close();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check the response code
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

        //Close the connection
        conn.disconnect();

        //Update the "folder blacklist" variable with the new edit
        this.folderBlacklist = blacklist;

    }

    /**
     * Updates all details of the backup from the API
     *
     * @throws IOException                If there is an error connecting to the API
     * @throws APIUnauthorizedException   If the API key is invalid
     * @throws APINoServerAccessException If the API key does not have access to the server
     * @throws APINotFoundException       If the server or backup does not exist
     */
    public void updateDetails() throws IOException, APIUnauthorizedException, APINoServerAccessException, APINotFoundException {
        if (deleted) {
            throw new IllegalStateException("Backup has been deleted");
        }

        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", serverID).replace("{BACKUP_ID}", backupID));

        HttpURLConnection conn = createGetConnection(url);

        conn.connect();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check response code
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

        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject response = new JSONObject(new JSONTokener(reader));

        //Close the connection
        conn.disconnect();

        //Update all values with the new ones got from the API
        backupName = response.getString("name");
        destination = response.getString("destination");
        suspended = response.getBoolean("suspended");
        deleteOldBackups = response.getBoolean("deleteOldBackups");
        compressionType = BackupCompressionType.findByVal(response.getInt("compressionType"));
        completedAt = LocalDateTime.parse(response.getString("completedAt"));
        lastBackupState = LastBackupState.findByVal(response.getInt("lastBackupState"));

        //get the two JSONArrays for the blacklists
        JSONArray fileBlacklistArray = response.getJSONArray("fileBlacklist");
        JSONArray folderBlacklistArray = response.getJSONArray("folderBlacklist");

        //put all the values in the JSONArrays into the ArrayLists
        for (int i = 0; i < fileBlacklistArray.length(); i++) {
            fileBlacklist.add(fileBlacklistArray.getString(i));
        }
        for (int i = 0; i < folderBlacklistArray.length(); i++) {
            folderBlacklist.add(folderBlacklistArray.getString(i));
        }
    }



    /**
     * Creates a get connection
     *
     * @param url URL to create the connection for
     * @return HttpURLConnection The connection
     * @throws IOException If there is an error when creating the connection
     */
    private HttpURLConnection createGetConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("ApiKey", api.token);
        conn.setDoInput(true);

        return conn;
    }

    private HttpURLConnection createPostConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("ApiKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        return conn;
    }

    private HttpURLConnection createDeleteConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("DELETE");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("ApiKey", api.token);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        return conn;
    }

    private HttpURLConnection createPutConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("ApiKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        return conn;
    }


    public void deleteBackup() throws APIUnauthorizedException, APINotFoundException, APINoServerAccessException, IOException {
        if (deleted) {
            throw new IllegalStateException("Backup has already been deleted");
        }

        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", serverID).replace("{BACKUP_ID}", backupID));

        //Create the connection
        HttpURLConnection conn = createDeleteConnection(url);

        //Connect to the API
        conn.connect();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check response code
        switch (responseCode) {
            case 200 -> this.deleted = true;
            case 401 -> throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 404 -> throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 403 -> throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default -> throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }
    }

    public void runBackup() throws APIUnauthorizedException, APINotFoundException, APINoServerAccessException, IOException {
        if (deleted) {
            throw new IllegalStateException("Backup has already been deleted");
        }

        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUP_DETAILS.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", serverID).replace("{BACKUP_ID}", backupID));

        //Create the connection
        HttpURLConnection conn = createPostConnection(url);

        //Connect to the API
        conn.connect();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //Check response code
        switch (responseCode) {
            case 200 -> updateDetails();
            case 401 -> throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 404 -> throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 403 -> throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default -> throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }
    }
}
