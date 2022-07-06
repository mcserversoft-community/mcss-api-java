package com.mcssapi.barebones.servers;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class getServer {

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId
     * @param: SSL (true/false)
     * @return the server information as a JSON object
     */
    public static JSONObject getServer(String IP, String ApiKey, String ServerId, Boolean SSL) {
        //Get the version using the ApiKey from /api/v1
        JSONObject serverInfo = new JSONObject();
        StringBuilder sb = new StringBuilder();
        String line;
        URL url;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/servers/" + ServerId);
            } else {
                url = new URL("http://" + IP + "/api/v1/servers/" + ServerId);
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);
            conn.setRequestProperty("APIKey", ApiKey);

            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 401) {
                serverInfo = new JSONObject().append("error", "ERR_KEY401");
                return serverInfo;
            } else if (responseCode == 403) {
                serverInfo = new JSONObject().append("error", "ERR_KEY403");
                return serverInfo;
            } else if (responseCode != 200) {
                serverInfo = new JSONObject().append("error", "ERR_" + responseCode);
                return serverInfo;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            serverInfo.clear();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            conn.disconnect();
            serverInfo = new JSONObject(sb.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            serverInfo.clear();
            serverInfo = new JSONObject().append("error", "URL_ERR");
            return serverInfo;
        } catch (IOException e) {
            e.printStackTrace();
            serverInfo.clear();
            serverInfo = new JSONObject().append("error", "IO_ERR");
            return serverInfo;
        }

        return serverInfo;
    }


    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId
     * @param: SSL (true/false)
     * @param: Filter (none, minimal, status) see documentation for more information
     * @return the server information as a JSON object
     */
    public static JSONObject getServer(String IP, String ApiKey, String ServerId, Boolean SSL, String Filter) {
        //Get the version using the ApiKey from /api/v1
        JSONObject serverInfo = new JSONObject();
        StringBuilder sb = new StringBuilder();
        String line;
        URL url;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/server/" + ServerId + "?filter=" + Filter);
            } else {
                url = new URL("http://" + IP + "/api/v1/server/" + ServerId + "?filter=" + Filter);
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);
            conn.setRequestProperty("APIKey", ApiKey);

            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 401) {
                serverInfo = new JSONObject().append("error", "ERR_KEY401");
                return serverInfo;
            } else if (responseCode == 403) {
                serverInfo = new JSONObject().append("error", "ERR_KEY403");
                return serverInfo;
            } else if (responseCode != 200) {
                serverInfo = new JSONObject().append("error", "ERR_" + responseCode);
                return serverInfo;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            serverInfo.clear();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            conn.disconnect();
            serverInfo = new JSONObject(sb.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            serverInfo.clear();
            serverInfo = new JSONObject().append("error", "URL_ERR");
            return serverInfo;
        } catch (IOException e) {
            e.printStackTrace();
            serverInfo.clear();
            serverInfo = new JSONObject().append("error", "IO_ERR");
            return serverInfo;
        }

        return serverInfo;
    }
}
