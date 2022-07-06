package com.mcssapi.barebones.servers;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class getServerStats {

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId of the server to get the stats for
     * @param: SSL (true/false)
     * @return the server stats as a JSON object
     */
    public static JSONObject getServerStats(String IP, String ApiKey, String ServerId, Boolean SSL) {
        //Get the version using the ApiKey from /api
        JSONObject serverStats = new JSONObject();
        StringBuilder sb = new StringBuilder();
        String line;
        URL url;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/servers/" + ServerId + "/stats");
            } else {
                url = new URL("http://" + IP + "/api/v1/servers/" + ServerId + "/stats");
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);

            conn.setRequestProperty("APIKey", ApiKey);

            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode == 401) {
                serverStats = new JSONObject().append("error", "ERR_KEY401");
                return serverStats;
            } else if (responseCode == 403) {
                serverStats = new JSONObject().append("error", "ERR_KEY403");
                return serverStats;
            } else if (responseCode != 200) {
                serverStats = new JSONObject().append("error", "ERR_" + responseCode);
                return serverStats;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            serverStats.clear();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            conn.disconnect();

            serverStats = new JSONObject(sb.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            serverStats.clear();
            serverStats = new JSONObject().append("error", "ERR_URL");
            return serverStats;
        } catch (IOException e) {
            e.printStackTrace();
            serverStats.clear();
            serverStats = new JSONObject().append("error", "ERR_IO");
            return serverStats;
        }
        return serverStats;
    }
}
