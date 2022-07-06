package com.mcssapi.barebones.servers;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class runServerCommands {

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId of the server to get the icon of
     * @param: CommandS to run
     * @param: separator of each command
     * @param: SSL (true/false)
     * @return True if successful, false if not
     */
    public static boolean runServerCommands(String IP, String ApiKey, String ServerId, String CommandS, String separator, Boolean SSL) {
        //POST /api/v1/servers/{serverId}/execute/commands
        //Body
        //"commands" : CommandS
        // "separator" : separator

        URL url;
        HttpURLConnection conn;
        JSONObject json;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/servers/" + ServerId + "/execute/commands");
            } else {
                url = new URL("http://" + IP + "/api/v1/servers/" + ServerId + "/execute/commands");
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
            json.put("commands", CommandS);
            json.put("separator", separator);
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
