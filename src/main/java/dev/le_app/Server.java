package dev.le_app;

import dev.le_app.exceptions.APIUnauthorizedException;
import dev.le_app.exceptions.APINotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.time.LocalDateTime;

public class Server {

    private final String GUID;
    private final ServerStatus Status;
    private final String Name;
    private final String Description;
    private final String PathToFolder;
    private final String FolderName;
    private final LocalDateTime CrationDate;
    private final boolean IsSetToAutostart;
    private final KeepOnline KeepOnline;
    private final int JavaAllocatedMemory;
    private final String JavaStartupLine;

    private final MCSSApi api;

    protected Server(String GUID, ServerStatus Status, String Name, String Description, String PathToFolder, String FolderName, LocalDateTime CrationDate, boolean IsSetToAutostart, KeepOnline KeepOnline, int JavaAllocatedMemory, String JavaStartupLine, MCSSApi api) {
        this.GUID = GUID;
        this.Status = Status;
        this.Name = Name;
        this.Description = Description;
        this.PathToFolder = PathToFolder;
        this.FolderName = FolderName;
        this.CrationDate = CrationDate;
        this.IsSetToAutostart = IsSetToAutostart;
        this.KeepOnline = KeepOnline;
        this.JavaAllocatedMemory = JavaAllocatedMemory;
        this.JavaStartupLine = JavaStartupLine;
        this.api = api;
    }

    /**
     * @return GUID of the server
     */
    public String getGUID() {
        return GUID;
    }

    /**
     * @return int of the status of the server.
     */
    public ServerStatus getStatus() {
        return Status;
    }

    /**
     * @return the name of the server
     */
    public String getName() {
        return Name;
    }

    /**
     * @return the description of the server
     */
    public String getDescription() {
        return Description;
    }

    /**
     * @return the path to the folder of the server
     */
    public String getPathToFolder() {
        return PathToFolder;
    }

    /**
     * @return the name of the folder of the server
     */
    public String getFolderName() {
        return FolderName;
    }

    /**
     * @return the creation date of the server
     */
    public LocalDateTime getCrationDate() {
        return CrationDate;
    }

    /**
     * @return true if the server is set to autostart, false if not
     */
    public boolean getIsSetToAutostart() {
        return IsSetToAutostart;
    }

    /**
     * @return the keep online time of the server
     */
    public KeepOnline getKeepOnline() {
        return KeepOnline;
    }

    /**
     * @return the allocated memory of the server, in megabytes
     */
    public int getJavaAllocatedMemory() {
        return JavaAllocatedMemory;
    }

    /**
     * @return the startup line of the server
     */
    public String getJavaStartupLine() {
        return JavaStartupLine;
    }

    public Scheduler getScheduler() {
        return new Scheduler(api, GUID);
    }

    /**
     * Get the server CPU usage
     * @return the server CPU usage percentage as int
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the API
     */
    public int getCpuUsage() throws APIUnauthorizedException, APINotFoundException, IOException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{GUID}", GUID));

        //Create the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set the request method and connection proprieties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
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
     * Get the server RAM usage
     * @return the server RAM usage in megabytes as int
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the API
     */
    public int getRamUsage() throws APIUnauthorizedException, APINotFoundException, IOException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{GUID}", GUID));

        //Create the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set the request method and connection proprieties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
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

    public int getMemoryLimit() throws APIUnauthorizedException, APINotFoundException, IOException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{GUID}", GUID));

        //Create the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set the request method and connection proprieties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
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
     * Get the number of online players on the server
     * @return the online player number as an int
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the API
     */
    public int getOnlinePlayers() throws APIUnauthorizedException, APINotFoundException, IOException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{GUID}", GUID));

        //Create the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set the request method and connection proprieties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
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
     * Get the player limit on the server
     * @return the player limit as an int
     * @throws APIUnauthorizedException if the API token is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an error while connecting to the API
     */
    public int getPlayerLimit() throws APIUnauthorizedException, APINotFoundException, IOException {

        //Create the URL
        URL url = new URL(Endpoints.GET_STATS.getEndpoint().replace("{IP}", api.IP)
                .replace("{GUID}", GUID));

        //Create the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set the request method and connection proprieties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
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

    //Get server icon
    public BufferedImage getServerIcon() throws APIUnauthorizedException, APINotFoundException, IOException {

        //Create the URL
        URL url = new URL(Endpoints.GET_ICON.getEndpoint().replace("{IP}", api.IP)
                .replace("{GUID}", GUID));

        //Create the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //Set the request method and connection proprieties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoInput(true);

        //Get the response code
        int responseCode = conn.getResponseCode();

        //If the response code indicates an issue throw the appropriate exception
        switch (responseCode) {
            case 200:
                break;
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
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
     */
    public void executeServerAction(ServerAction action) throws APIUnauthorizedException, IOException, APINotFoundException {

        //Create the URL
        URL url = new URL(Endpoints.EXECUTE_SERVER_ACTION.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        //Create and open the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the connection variables, request proprieties and request method
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        //Connect to the API
        conn.connect();

        //Create the json object to send
        String json = "{\"actionId\":" + action.getValue() + "}";
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
     */
    public void executeServerCommand(String command) throws APIUnauthorizedException, IOException, APINotFoundException {

        //Create the URL
        URL url = new URL(Endpoints.EXECUTE_SERVER_COMMAND.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        //Create and open the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the connection variables, request proprieties and request method
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

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
     */
    public void executeServerCommands(String... commands) throws APIUnauthorizedException, IOException, APINotFoundException {
        //Create the URL
        URL url = new URL(Endpoints.EXECUTE_SERVER_COMMANDS.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP));

        //Create and open the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the connection variables, request proprieties and request method
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

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
     */
    public String[] getConsole(int lines) throws APIUnauthorizedException, IOException, APINotFoundException {

        //Create URL with query parameter
        URL url = new URL(Endpoints.GET_CONSOLE.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP).replace("{AMOUNT_OF_LINES}", String.valueOf(lines)));

        //Create and open the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the connection variables, request proprieties and request method
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoOutput(true);
        conn.setDoInput(true);

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
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //Get response in a JSON object
        JSONObject json = new JSONObject(conn.getOutputStream());
        //get the JSON array from the object
        JSONArray consoleJsonArray = new JSONArray(json);

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
     */
    public String[] getConsoleFromBeginning(int lines, boolean takeFromBeginning) throws APIUnauthorizedException, IOException, APINotFoundException {

        //parse boolean to the written version of it
        String t;
        if (takeFromBeginning) t = "true"; else t = "false";

        //Create URL with query parameters
        URL url = new URL(Endpoints.GET_CONSOLE_FROM_BEGINNING.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP).replace("{AMOUNT_OF_LINES}", String.valueOf(lines))
                .replace("{BEGINNING}", t));

        //Create and open the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the connection variables, request proprieties and request method
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoOutput(true);
        conn.setDoInput(true);

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
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //Get response in a JSON object
        JSONObject json = new JSONObject(conn.getOutputStream());
        //get the JSON array from the object
        JSONArray consoleJsonArray = new JSONArray(json);

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
     */
    public String[] getConsoleReversed(int lines, boolean reversed) throws APIUnauthorizedException, IOException, APINotFoundException {

        //parse boolean to the written version of it
        String t;
        if (reversed) t = "true"; else t = "false";

        //Create URL with query parameters
        URL url = new URL(Endpoints.GET_CONSOLE_REVERSED.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP).replace("{AMOUNT_OF_LINES}", String.valueOf(lines))
                .replace("{REVERSED}", t));

        //Create and open the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the connection variables, request proprieties and request method
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoOutput(true);
        conn.setDoInput(true);

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
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //Get response in a JSON object
        JSONObject json = new JSONObject(conn.getOutputStream());
        //get the JSON array from the object
        JSONArray consoleJsonArray = new JSONArray(json);

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
     */
    public String[] getConsole(int lines, boolean takeFromBeginning, boolean reversed) throws APIUnauthorizedException, IOException, APINotFoundException {

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
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the connection variables, request proprieties and request method
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setDoOutput(true);
        conn.setDoInput(true);

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
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //Get response in a JSON object
        JSONObject json = new JSONObject(conn.getOutputStream());
        //get the JSON array from the object
        JSONArray consoleJsonArray = new JSONArray(json);

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
     */
    public boolean isConsoleOutdated(String secondLastLine, String lastLine ) throws APIUnauthorizedException, IOException, APINotFoundException {
        URL url = new URL(Endpoints.IS_CONSOLE_OUTDATED.getEndpoint().replace("{SERVER_ID}", GUID)
                .replace("{IP}", api.IP).replace("{SECOND_LAST_LINE}", secondLastLine)
                .replace("{LAST_LINE}", lastLine));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);

        conn.connect();
        int responseCode = conn.getResponseCode();

        switch (responseCode) {
            case 200:
                break;
            case 404:
                throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 401:
                throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            default:
                throw new IOException(Errors.NOT_RECOGNIZED.getMessage() + responseCode);
        }

        //save the response in a JSONObject
        JSONObject json = new JSONObject(conn.getOutputStream());

        //close connection
        conn.disconnect();

        //return the boolean value of the response
        return json.getBoolean("isOutdated");
    }

}

