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

    public String getGUID() {
        return GUID;
    }

    public int getStatus() {
        return Status;
    }

    public String getName() {
        return Name;
    }

    public String getDescription() {
        return Description;
    }

    public String getPathToFolder() {
        return PathToFolder;
    }

    public String getFolderName() {
        return FolderName;
    }

    public LocalDateTime getCrationDate() {
        return CrationDate;
    }

    public boolean getIsSetToAutostart() {
        return IsSetToAutostart;
    }

    public int getKeepOnline() {
        return KeepOnline;
    }

    public int getJavaAllocatedMemory() {
        return JavaAllocatedMemory;
    }

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

        URL url;

        url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/execute/action");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        String json = "{\"actionId\":" + action.getValue() + "}";
        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        os.close();
        conn.connect();
        int responseCode = conn.getResponseCode();
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
        URL url;

        url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/execute/command");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);

        String json = "{\"command\": \"" + command + "\"}";
        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.flush();
        os.close();
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when executing server command \" " + command +
                    " \" for server " + Name + ".");
        } else if (responseCode == 404) {
            //Might never fire, better safe than sorry
            throw new APINotFoundException("Got 404 response code when executing server action \"" + command +
                    "\" for server " + Name + ".");
        }
    }

}

