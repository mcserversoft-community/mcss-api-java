package com.mcssapi.barebones.scheduler;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class getSchedulerTasks {

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @Param: ServerId of the server to get the info of
     * @param: SSL (true/false)
     * @return the server information as a JSON object. Null if error during request.
     */
    public static JSONObject getSchedulerTasks(String IP, String ApiKey, String ServerId, Boolean SSL) {
        //GET /api/v1/servers/{serverId}/scheduler

        URL url;
        HttpURLConnection conn;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/servers/" + ServerId + "/scheduler");
            } else {
                url = new URL("http://" + IP + "/api/v1/servers/" + ServerId + "/scheduler");
            }
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);
            conn.setRequestProperty("APIKey", ApiKey);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Error: " + responseCode);
            }
            return new JSONObject(conn.getResponseMessage());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @Param: ServerId of the server to get the info of
     * @param: Filter (None, FixedTime, Interval, Timeless)
     * @param: SSL (true/false)
     * @return the server information as a JSON object. Null if error during request.
     */
    public static JSONObject getSchedulerTasks(String IP, String ApiKey, String ServerId, String Filter, Boolean SSL) {
        //GET /api/v1/servers/{serverId}/scheduler

        URL url;
        HttpURLConnection conn;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/servers/" + ServerId + "/scheduler?filter=" + Filter);
            } else {
                url = new URL("http://" + IP + "/api/v1/servers/" + ServerId + "/scheduler?filter=" + Filter);
            }
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);
            conn.setRequestProperty("APIKey", ApiKey);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Error: " + responseCode);
            }
            return new JSONObject(conn.getResponseMessage());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
