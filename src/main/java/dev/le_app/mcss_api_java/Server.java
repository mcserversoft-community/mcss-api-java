package dev.le_app.mcss_api_java;

import dev.le_app.mcss_api_java.exceptions.APINoServerAccessException;
import dev.le_app.mcss_api_java.exceptions.APIServerMustBeOfflineException;
import dev.le_app.mcss_api_java.exceptions.APIUnauthorizedException;
import dev.le_app.mcss_api_java.exceptions.APINotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Represents a server.
 */
public class Server {

    private final String GUID;
    private ServerStatus status;
    private String name;
    private String description;
    private String pathToFolder;
    private String folderName;
    private LocalDateTime creationDate;
    private boolean isSetToAutostart;
    private KeepOnline keepOnline;
    private int javaAllocatedMemory;
    private String javaStartupLine;
    private boolean forceSaveOnStop;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final MCSSApi api;

    protected Server(String GUID, MCSSApi api) throws APIUnauthorizedException, IOException, APINoServerAccessException, APINotFoundException {
        this.GUID = GUID;
        this.api = api;
        updateDetails();
    }

    /**
     * @return GUID of the server
     */
    public String getGUID() {
        return GUID;
    }

    /**
     * @return enum of the status of the server. - This value is updated every time you call this method.
     * @throws APIUnauthorizedException if the API token is invalid or expired.
     * @throws APINotFoundException if the serverID is invalid.
     * @throws APINoServerAccessException if you do not have access to this server.
     * @throws IOException if an error occurs while connecting to the API.
     */
    public ServerStatus getStatus() throws APIUnauthorizedException, IOException, APINoServerAccessException, APINotFoundException {
        updateDetails();
        return status;
    }

    /**
     * @return the name of the server
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description of the server
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the path to the folder of the server
     */
    public String getPathToFolder() {
        return pathToFolder;
    }

    /**
     * @return the name of the folder of the server
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * @return the creation date of the server
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    /**
     * @return true if the server is set to autostart, false if not
     */
    public boolean getIsSetToAutostart() {
        return isSetToAutostart;
    }

    /**
     * @return whether the server is forced to /save-all before shutdown
     */
    public boolean getForceSaveOnStop() {
        return forceSaveOnStop;
    }

    /**
     * @return the keep online time of the server
     */
    public KeepOnline getKeepOnline() {
        return keepOnline;
    }

    /**
     * @return the allocated memory of the server, in megabytes
     */
    public int getJavaAllocatedMemory() {
        return javaAllocatedMemory;
    }

    /**
     * @return the startup line of the server
     */
    public String getJavaStartupLine() {
        return javaStartupLine;
    }

    /**
     * @return a scheduler object for this server
     */
    public Scheduler getScheduler() {
        return new Scheduler(api, GUID);
    }

    /**
     * Get a list of backups for this server
     * @return an arraylist of backups
     * @throws APIUnauthorizedException if the API token is invalid or expired.
     * @throws APINoServerAccessException if you do not have access to this server.
     * @throws APINotFoundException if the serverID is invalid.
     * @throws IOException if an error occurs while connecting to the API.
     */
    public ArrayList<Backup> getBackups() throws APIUnauthorizedException, APINoServerAccessException, APINotFoundException, IOException {

        //Create the URL
        URL url = new URL(Endpoints.SERVER_BACKUPS.getEndpoint().replace("{SERVER_ID}", GUID).replace("{IP}", api.IP));

        //Create the connection
        HttpURLConnection connection = createGetConnection(url);

        connection.connect();

        //Get the response code
        int responseCode = connection.getResponseCode();

        //Check the response code for errors
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
        JSONTokener tokener = new JSONTokener(new InputStreamReader(connection.getInputStream()));
        JSONArray response = new JSONArray(tokener);

        ArrayList<Backup> backups = new ArrayList<>();

        //For every item in the JSONArray, get the corresponding JSONObject and create a Backup object
        for (int i = 0; i < response.length(); i++) {
            JSONObject backup = response.getJSONObject(i);
            backups.add(new Backup(api, GUID, backup.getString("backupId")));
        }

        return backups;

    }

    /**
     * Get the server CPU usage - this value is updated with every method run
     * @return the server CPU usage percentage as int
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the API
     * @throws APINoServerAccessException if you do not have access to this server
     */
    public int getCpuUsage() throws APIUnauthorizedException, APINotFoundException, IOException, APINoServerAccessException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

        //Create the connection
        HttpURLConnection conn = createGetConnection(url);

        conn.connect();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
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
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject response = new JSONObject(new JSONTokener(reader));

        //Close the connection
        conn.disconnect();

        JSONObject stats = response.getJSONObject("latest");
        return stats.getInt("cpu");

    }

    /**
     * Get the server RAM usage - this value is updated with every method run
     * @return the server RAM usage in megabytes as int
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the API
     * @throws APINoServerAccessException if you do not have access to this server
     */
    public int getRamUsage() throws APIUnauthorizedException, APINotFoundException, IOException, APINoServerAccessException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

        //Create the connection
        HttpURLConnection conn = createGetConnection(url);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
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
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject response = new JSONObject(new JSONTokener(reader));

        //Close the connection
        conn.disconnect();

        JSONObject stats = response.getJSONObject("latest");
        return stats.getInt("memoryUsed");
    }

    /**
     * Get the server memory limit - this value is updated with every method run
     * @return the server memory limit in megabytes as int
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the API
     * @throws APINoServerAccessException if you do not have access to this server
     */
    public int getMemoryLimit() throws APIUnauthorizedException, APINotFoundException, IOException, APINoServerAccessException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

        //Create the connection
        HttpURLConnection conn = createGetConnection(url);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
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
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject response = new JSONObject(new JSONTokener(reader));

        //Close the connection
        conn.disconnect();

        JSONObject stats = response.getJSONObject("latest");
        return stats.getInt("memoryLimit");

    }


    /**
     * Get the number of online players on the server - this value is updated with every method run
     * @return the online player number as an int
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the API
     * @throws APINoServerAccessException if the API token does not have access to the server
     */
    public int getOnlinePlayers() throws APIUnauthorizedException, APINotFoundException, IOException, APINoServerAccessException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

        //Create the connection
        HttpURLConnection conn = createGetConnection(url);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
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
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject response = new JSONObject(new JSONTokener(reader));

        //Close the connection
        conn.disconnect();

        JSONObject stats = response.getJSONObject("latest");
        return stats.getInt("playersOnline");
    }

    /**
     * Get the player limit on the server - this value is updated with every method run
     * @return the player limit as an int
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the
     * @throws APINoServerAccessException if the API token does not have access to the server
     */
    public int getPlayerLimit() throws APIUnauthorizedException, APINotFoundException, IOException, APINoServerAccessException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

        //Create the connection
        HttpURLConnection conn = createGetConnection(url);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
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
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject response = new JSONObject(new JSONTokener(reader));

        //Close the connection
        conn.disconnect();

        JSONObject stats = response.getJSONObject("latest");
        return stats.getInt("playerLimit");
    }

    /**
     * Get the server start time - Uses default system timezone to calculate! - this value is updated with every method run
     * @return The LocalDateTime of when the server was started
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the API
     * @throws APINoServerAccessException if the API token does not have access to the server
     */
    public LocalDateTime getStartDate() throws APIUnauthorizedException, APINoServerAccessException, APINotFoundException, IOException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

        //Create the connection
        HttpURLConnection conn = createGetConnection(url);

        conn.connect();

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
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
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject response = new JSONObject(new JSONTokener(reader));

        //Close the connection
        conn.disconnect();

        JSONObject stats = response.getJSONObject("latest");
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(stats.getLong("startDate")), ZoneId.systemDefault());

    }

    /**
     * Get the server icon - this value is updated with every method run
     * @return server icon as a BufferedImage
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the API
     * @throws APINoServerAccessException if the API token does not have access to the server
     */
    public BufferedImage getServerIcon() throws APIUnauthorizedException, APINotFoundException, IOException, APINoServerAccessException {

        //Create the URL
        URL url = new URL(Endpoints.GET_ICON.getEndpoint().replace("{IP}", api.IP)
                .replace("{SERVER_ID}", GUID));

        //Create the connection
        HttpURLConnection conn = createGetConnection(url);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
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
        BufferedImage response = ImageIO.read(conn.getInputStream());

        //Close the connection
        conn.disconnect();

        return response;
    }



    /**
     * Execute a power action on the server.
     * @param action 0 invalid, 1 stop, 2 start, 3 kill, 4 restart
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws APINoServerAccessException if the API key does not have access to the server
     */
    public void executeServerAction(ServerAction action) throws APIUnauthorizedException, IOException, APINotFoundException, APINoServerAccessException {

        //Create the URL
        URL url = new URL(Endpoints.EXECUTE_SERVER_ACTION.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        //Create and open the connection
        HttpURLConnection conn = createPostConnection(url);

        //Connect to the API
        conn.connect();

        //Create the json object to send
        String json = "{\"action\":" + action.getValue() + "}";
        //Get the outputstream of the connection
        OutputStream os = conn.getOutputStream();
        //Write the json to the outputstream
        os.write(json.getBytes());
        //Flush and close the outputstream
        os.flush();
        os.close();

        //Get the response code of the connection
        int responseCode = conn.getResponseCode();

        //if the responsecode is an error, throw an exception
        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //close connection
        conn.disconnect();
    }

    /**
     * Executes a command on a server
     * @param command String of the command to execute
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API token does not have access to the server
     */
    public void executeServerCommand(String command) throws APIUnauthorizedException, IOException, APINotFoundException, APINoServerAccessException {

        //Create the URL
        URL url = new URL(Endpoints.EXECUTE_SERVER_COMMAND.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        //Create and open the connection
        HttpURLConnection conn = createPostConnection(url);

        //Connect to the API
        conn.connect();

        //Create the json object to send
        String json = "{\"command\": \"" + command + "\"}";
        //Get the outputstream of the connection
        OutputStream os = conn.getOutputStream();
        //Write the json to the outputstream
        os.write(json.getBytes());
        //Flush and close the outputstream
        os.flush();
        os.close();

        //Get the response code of the connection
        int responseCode = conn.getResponseCode();

        //if the responsecode is an error, throw an exception
        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //close connection
        conn.disconnect();

    }

    /**
     * Executes multiple commands on the server
     * @param commands Array of strings of the commands to execute
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API token does not have access to the server
     */
    public void executeServerCommands(String... commands) throws APIUnauthorizedException, IOException, APINotFoundException, APINoServerAccessException {
        //Create the URL
        URL url = new URL(Endpoints.EXECUTE_SERVER_COMMANDS.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        //Create and open the connection
        HttpURLConnection conn = createPostConnection(url);

        //Connect to the API
        conn.connect();

        //Create the json object to send
        StringBuilder json = new StringBuilder("{\"commands\": [");
        for (int i = 0; i < commands.length; i++) {
            json.append("\"").append(commands[i]).append("\"");
            if (i != commands.length - 1) {
                json.append(",");
            }
        }
        json.append("]}");
        //Get the outputstream of the connection
        OutputStream os = conn.getOutputStream();
        //Write the json to the outputstream
        os.write(json.toString().getBytes());
        //Flush and close the outputstream
        os.flush();
        os.close();

        //Get the response code of the connection
        int responseCode = conn.getResponseCode();

        //if the responsecode is an error, throw an exception
        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //close connection
        conn.disconnect();
    }

    /**
     * Get last console lines
     * @param lines amount of lines to get
     * @return string array of the last console lines
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API token does not have access to the server
     */
    public String[] getConsole(int lines) throws APIUnauthorizedException, IOException, APINotFoundException, APINoServerAccessException {

        //Create URL with query parameter
        URL url = new URL(Endpoints.GET_CONSOLE.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP).replace("{AMOUNT_OF_LINES}", String.valueOf(lines)));

        //Create and open the connection
        HttpURLConnection conn = createGetConnection(url);

        //Connect to the API
        conn.connect();

        //Get the response code of the connection
        int responseCode = conn.getResponseCode();

        //if the responsecode is an error, throw an exception
        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //get the JSON array from the object
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONArray consoleJsonArray = new JSONArray(new JSONTokener(reader));

        //Create a string array to store the console
        String[] console = new String[consoleJsonArray.length()];

        //Loop through the array and add the strings to the array
        for (int i = 0; i < consoleJsonArray.length(); i++) {
            console[i] = consoleJsonArray.getString(i);
        }

        //close connection
        conn.disconnect();

        //return the console
        return console;
    }

    /**
     * Get last console lines, with the ability to take the lines from the beginning of the console instead of the end
     * @param lines amount of lines to get
     * @param takeFromBeginning true if you want to get the lines from the beginning of the console, false if you want to get the lines from the end of the console
     * @return string array of the last console lines
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API token does not have access to the server
     */
    public String[] getConsoleFromBeginning(int lines, boolean takeFromBeginning) throws APIUnauthorizedException, IOException, APINotFoundException, APINoServerAccessException {

        //parse boolean to the written version of it
        String t;
        if (takeFromBeginning) t = "true"; else t = "false";

        //Create URL with query parameters
        URL url = new URL(Endpoints.GET_CONSOLE_FROM_BEGINNING.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP).replace("{AMOUNT_OF_LINES}", String.valueOf(lines))
                .replace("{BEGINNING}", t));

        //Create and open the connection
        HttpURLConnection conn = createGetConnection(url);

        //Connect to the API
        conn.connect();

        //Get the response code of the connection
        int responseCode = conn.getResponseCode();

        //if the responsecode is an error, throw an exception
        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //get the JSON array from the object
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONArray consoleJsonArray = new JSONArray(new JSONTokener(reader));

        //Create a string array to store the console
        String[] console = new String[consoleJsonArray.length()];

        //Loop through the array and add the strings to the array
        for (int i = 0; i < consoleJsonArray.length(); i++) {
            console[i] = consoleJsonArray.getString(i);
        }

        //close connection
        conn.disconnect();

        //return the console
        return console;
    }

    /**
     * Get last console lines, with the ability reverse the lines of the console
     * @param lines amount of lines to get
     * @param reversed true if you want to reverse the lines of the console, false if you want to get the lines in the normal order
     * @return string array of the last console lines
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API token does not have access to the server
     */
    public String[] getConsoleReversed(int lines, boolean reversed) throws APIUnauthorizedException, IOException, APINotFoundException, APINoServerAccessException {

        //parse boolean to the written version of it
        String t;
        if (reversed) t = "true"; else t = "false";

        //Create URL with query parameters
        URL url = new URL(Endpoints.GET_CONSOLE_REVERSED.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP).replace("{AMOUNT_OF_LINES}", String.valueOf(lines))
                .replace("{REVERSED}", t));

        //Create and open the connection
        HttpURLConnection conn = createGetConnection(url);

        //Connect to the API
        conn.connect();

        //Get the response code of the connection
        int responseCode = conn.getResponseCode();

        //if the responsecode is an error, throw an exception
        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //get the JSON array from the object
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONArray consoleJsonArray = new JSONArray(new JSONTokener(reader));

        //Create a string array to store the console
        String[] console = new String[consoleJsonArray.length()];

        //Loop through the array and add the strings to the array
        for (int i = 0; i < consoleJsonArray.length(); i++) {
            console[i] = consoleJsonArray.getString(i);
        }

        //close connection
        conn.disconnect();

        //return the console
        return console;
    }

    /**
     * Get last console lines, with the ability to take the lines from the beginning of the console instead of the end and the ability to reverse the console lines
     * @param lines amount of lines to get
     * @param takeFromBeginning true if you want to get the lines from the beginning of the console, false if you want to get the lines from the end of the console
     * @param reversed true if you want to reverse the lines of the console, false if you want to get the lines in the normal order
     * @return string array of the last console lines
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API token does not have access to the server
     */
    public String[] getConsole(int lines, boolean takeFromBeginning, boolean reversed) throws APIUnauthorizedException, IOException, APINotFoundException, APINoServerAccessException {

        //parse boolean to the written version of it
        String fromBeginning;
        String reverse;
        if (takeFromBeginning) fromBeginning = "true"; else fromBeginning = "false";
        if (reversed) reverse = "true"; else reverse = "false";

        //Create URL with query parameters
        URL url = new URL(Endpoints.GET_CONSOLE.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP).replace("{AMOUNT_OF_LINES}", String.valueOf(lines))
                .replace("{BEGINNING}", fromBeginning).replace("{REVERSED}", reverse));

        //Create and open the connection
        HttpURLConnection conn = createGetConnection(url);

        //Connect to the API
        conn.connect();

        //Get the response code of the connection
        int responseCode = conn.getResponseCode();

        //if the responsecode is an error, throw an exception
        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //get the JSON array from the object
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONArray consoleJsonArray = new JSONArray(new JSONTokener(reader));

        //Create a string array to store the console
        String[] console = new String[consoleJsonArray.length()];

        //Loop through the array and add the strings to the array
        for (int i = 0; i < consoleJsonArray.length(); i++) {
            console[i] = consoleJsonArray.getString(i);
        }

        //close connection
        conn.disconnect();

        //return the console
        return console;
    }

    /**
     * Check if the console is outdated
     * @param secondLastLine Second to last line for comparison
     * @param lastLine Last line for comparison
     * @return true if the console is outdated, false if the console is not outdated
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API key does not have access to the server
     */
    public boolean isConsoleOutdated(String secondLastLine, String lastLine ) throws APIUnauthorizedException, IOException, APINotFoundException, APINoServerAccessException {
        URL url = new URL(Endpoints.IS_CONSOLE_OUTDATED.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP).replace("{SECOND_LAST_LINE}", secondLastLine)
                .replace("{LAST_LINE}", lastLine));

        HttpURLConnection conn = createGetConnection(url);

        conn.connect();
        int responseCode = conn.getResponseCode();

        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //save the response in a JSONObject
        JSONObject json = new JSONObject(conn.getInputStream());

        //close connection
        conn.disconnect();

        //return the boolean value of the response
        return json.getBoolean("isOutdated");
    }



    //Now it's time for the SETTERS

    /**
     * Set the server name
     * @param name new name of the server
     * @throws APINotFoundException if the server is not found
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws APINoServerAccessException if the API key does not have access to the server
     * @throws IOException if there is an error with the connection
     */
    public void setName(String name) throws APINotFoundException, APIUnauthorizedException, APINoServerAccessException, IOException, APIServerMustBeOfflineException {

        //Create URL
        URL url = new URL(Endpoints.SERVER_DETAILS.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        //Create and open the connection
        HttpURLConnection conn = createPutConnection(url);

        //Create JSON object to send
        String json = "{\"name\": \"" + name + "\"}";

        //Connect
        conn.connect();

        //Write the jsonobject
        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json);
        writer.flush();

        //Get the response code of the connection
        int responseCode = conn.getResponseCode();

        //if the responsecode is an error, throw an exception
        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 405:
                throw new APIServerMustBeOfflineException(Errors.SERVER_MUST_BE_OFFLINE.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //close connection
        conn.disconnect();

        this.name = name;
    }

    /**
     * Set the server description
     * @param description new description of the server
     * @throws APINotFoundException if the server is not found
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws APINoServerAccessException if the API key does not have access to the server
     * @throws IOException if there is an error with the connection
     */
    public void setDescription(String description) throws APINotFoundException, APIUnauthorizedException, APINoServerAccessException, IOException, APIServerMustBeOfflineException {
        URL url = new URL(Endpoints.SERVER_DETAILS.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        HttpURLConnection conn = createPutConnection(url);

        String json = "{\"description\": \"" + description + "\"}";

        conn.connect();

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json);
        writer.flush();

        int responseCode = conn.getResponseCode();

        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 405:
                throw new APIServerMustBeOfflineException(Errors.SERVER_MUST_BE_OFFLINE.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        conn.disconnect();

        this.description = description;
    }

    /**
     * Set if the server autostarts with mcss
     * @param autostart the new autostart value
     * @throws APINotFoundException if the server is not found
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws APINoServerAccessException if the API key does not have access to the server
     * @throws IOException if there is an error with the connection
     */
    public void setAutostart(boolean autostart) throws APINotFoundException, APIUnauthorizedException, APINoServerAccessException, IOException, APIServerMustBeOfflineException {
        URL url = new URL(Endpoints.SERVER_DETAILS.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        HttpURLConnection conn = createPutConnection(url);

        String json = "{\"isSetToAutoStart\": " + autostart + "}";

        conn.connect();

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json);
        writer.flush();

        int responseCode = conn.getResponseCode();

        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 405:
                throw new APIServerMustBeOfflineException(Errors.SERVER_MUST_BE_OFFLINE.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        conn.disconnect();

        this.isSetToAutostart = autostart;
    }

    /**
     * Change if the server is forced to save when stopping
     * @param forceSaveOnStop the new value
     * @throws APINotFoundException if the server is not found
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws APINoServerAccessException if the API key does not have access to the server
     * @throws IOException if there is an error with the connection
     */
    public void setForceSaveOnStop(boolean forceSaveOnStop) throws APINotFoundException, APIUnauthorizedException, APINoServerAccessException, IOException, APIServerMustBeOfflineException {
        URL url = new URL(Endpoints.SERVER_DETAILS.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        HttpURLConnection conn = createPutConnection(url);

        String json = "{\"forceSaveOnStop\": " + forceSaveOnStop + "}";

        conn.connect();

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json);
        writer.flush();

        int responseCode = conn.getResponseCode();

        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 405:
                throw new APIServerMustBeOfflineException(Errors.SERVER_MUST_BE_OFFLINE.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        conn.disconnect();

        this.forceSaveOnStop = forceSaveOnStop;
    }

    /**
     * Change the java allocated memory - This applies after a server restart
     * @param javaAllocatedMemory new memory size in MegaBytes
     * @throws APINotFoundException if the server is not found
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws APINoServerAccessException if the API key does not have access to the server
     * @throws IOException if there is an error with the connection
     */
    public void setJavaAllocatedMemory(int javaAllocatedMemory) throws APINotFoundException, APIUnauthorizedException, APINoServerAccessException, IOException, APIServerMustBeOfflineException {
        URL url = new URL(Endpoints.SERVER_DETAILS.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        HttpURLConnection conn = createPutConnection(url);

        String json = "{\"javaAllocatedMemory\": " + javaAllocatedMemory + "}";

        conn.connect();

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json);
        writer.flush();

        int responseCode = conn.getResponseCode();

        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 405:
                throw new APIServerMustBeOfflineException(Errors.SERVER_MUST_BE_OFFLINE.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        conn.disconnect();

        this.javaAllocatedMemory = javaAllocatedMemory;
    }

    /**
     * Set the new KeepOnline behaviour
     * @param keepOnline the new KeepOnline behaviour
     * @throws APINotFoundException if the server is not found
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws APINoServerAccessException if the API key does not have access to the server
     * @throws IOException if there is an error with the connection
     */
    public void setKeepOnline(KeepOnline keepOnline) throws APINotFoundException, APIUnauthorizedException, APINoServerAccessException, IOException, APIServerMustBeOfflineException {
        URL url = new URL(Endpoints.SERVER_DETAILS.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        HttpURLConnection conn = createPutConnection(url);

        String json = "{\"keepOnline\": " + keepOnline.getValue() + "}";

        conn.connect();

        OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
        writer.write(json);
        writer.flush();

        int responseCode = conn.getResponseCode();

        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            case 405:
                throw new APIServerMustBeOfflineException(Errors.SERVER_MUST_BE_OFFLINE.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        conn.disconnect();

        this.keepOnline = keepOnline;
    }

    /**
     * Update the server details
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     * @throws APINoServerAccessException if the API key does not have access to the server
     */
    public void updateDetails() throws APINotFoundException, APIUnauthorizedException, IOException, APINoServerAccessException {

        //create URL
        URL url = new URL(Endpoints.SERVER_DETAILS.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        //create and open connection
        HttpURLConnection conn = createGetConnection(url);

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is an error, throw an exception
        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403:
                throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //save the response in a JSONObject
        JSONObject json = new JSONObject(new JSONTokener(new InputStreamReader(conn.getInputStream())));

        //close connection
        conn.disconnect();

        //set the variables
        this.name = json.getString("name");
        this.description = json.getString("description");
        this.status = ServerStatus.findByVal(json.getInt("status"));
        this.pathToFolder = json.getString("pathToFolder");
        this.folderName = json.getString("folderName");
        this.creationDate = LocalDateTime.parse(json.getString("creationDate"), formatter);
        this.isSetToAutostart = json.getBoolean("isSetToAutoStart");
        this.keepOnline = KeepOnline.findByVal(json.getInt("keepOnline"));
        this.javaAllocatedMemory = json.getInt("javaAllocatedMemory");
        this.javaStartupLine = json.getString("javaStartupLine");
        this.forceSaveOnStop = json.getBoolean("forceSaveOnStop");

    }

    //Create HTTP GET Connection
    private HttpURLConnection createGetConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);
        return conn;
    }

    //Create HTTP POST Connection
    private HttpURLConnection createPostConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        return conn;
    }

    //Create HTTP PUT Connection
    private HttpURLConnection createPutConnection(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        return conn;
    }




    @Override
    public String toString() {
        return "Server{" +
                "GUID='" + GUID + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", pathToFolder='" + pathToFolder + '\'' +
                ", folderName='" + folderName + '\'' +
                ", creationDate=" + creationDate +
                ", isSetToAutostart=" + isSetToAutostart +
                ", keepOnline=" + keepOnline +
                ", javaAllocatedMemory=" + javaAllocatedMemory +
                ", javaStartupLine='" + javaStartupLine + '\'' +
                ", forceSaveOnStop=" + forceSaveOnStop +
                '}';
    }

}

