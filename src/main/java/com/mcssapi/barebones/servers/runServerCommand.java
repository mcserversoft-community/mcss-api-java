package com.mcssapi.barebones.servers;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class runServerCommand {

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId of the server to run the command on
     * @param: Command to run
     * @param: SSL (true/false)
     * @return the server information as a JSON object
     */
    public static boolean runServerCommand(String IP, String ApiKey, String ServerId, String command, Boolean SSL) {
        //POST /api/v1/servers/{serverId}/execute/command
        //body JSON
        //"command": "command"

        URL url;
        HttpURLConnection conn;
        JSONObject json;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/servers/" + ServerId + "/execute/command");
            } else {
                url = new URL("http://" + IP + "/api/v1/servers/" + ServerId + "/execute/command");
            }
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);
            conn.setRequestProperty("APIKey", ApiKey);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            json = new JSONObject();
            json.put("command", command);
            conn.getOutputStream().write(json.toString().getBytes());
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Error: " + responseCode);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
