package com.mcssapi;

import com.mcssapi.exceptions.APIUnauthorizedException;
import com.mcssapi.exceptions.APINotFoundException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

public class Server {

    private String GUID;
    private int Status;
    private String Name;
    private String Description;
    private String PathToFolder;
    private String FolderName;
    private LocalDateTime CrationDate;
    private boolean IsSetToAutostart;
    private int KeepOnline;
    private int JavaAllocatedMemory;
    private String JavaStartupLine;

    private mcssapi api;

    protected Server(String GUID, int Status, String Name, String Description, String PathToFolder, String FolderName, LocalDateTime CrationDate, boolean IsSetToAutostart, int KeepOnline, int JavaAllocatedMemory, String JavaStartupLine, mcssapi api) {
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
    public int getStatus() {
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
    public int getKeepOnline() {
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

    /**
     * Execute a power action on the server.
     * @param action 0 invalid, 1 stop, 2 start, 3 kill, 4 restart
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     * @throws APIUnauthorizedException if the API key is invalid/expired
     */
    public void executeServerAction(ServerAction action) throws APIUnauthorizedException, IOException, APINotFoundException {

        //Create the URL
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/execute/action");

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
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when executing server action " + action.name() +
                    " for server " + Name + ".");
        } else if (responseCode == 404) {
            //Might never fire, better safe than sorry
            throw new APINotFoundException("Got 404 response code when executing server action " + action.name() +
                    " for server " + Name + ".");
        }
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
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/execute/command");

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
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when executing server command \" " + command +
                    " \" for server " + Name + ".");
        } else if (responseCode == 404) {
            //Might never fire, better safe than sorry
            throw new APINotFoundException("Got 404 response code when executing server action \"" + command +
                    "\" for server " + Name + ".");
        }
    }

    /**
     * Executes multiple commands on the server
     * @param commands Array of strings of the commands to execute
     * @throws APIUnauthorizedException if the API key is invalid/expired
     * @throws IOException if there is an error with the connection
     * @throws APINotFoundException if the server is not found
     */
    public void executeServerCommands(String[] commands) throws APIUnauthorizedException, IOException, APINotFoundException {
        //for every string execute the command singularly
        for (String command : commands) {
            //call the execute server command method
            executeServerCommand(command);
        }
    }

}

