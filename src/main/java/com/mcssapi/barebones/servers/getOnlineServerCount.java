package com.mcssapi.barebones.servers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class getOnlineServerCount {
    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: SSL (true/false)
     * @return Number of servers, -1 if error during request.
     */
    public static int getOnlineServerCount(String IP, String ApiKey, Boolean SSL) {
        //api/v1/servers/count/online
        URL url;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/servers/count/online");
            } else {
                url = new URL("http://" + IP + "/api/v1/servers/count/online");
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);
            conn.setRequestProperty("APIKey", ApiKey);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Error: " + responseCode);
            }
            return Integer.parseInt(conn.getResponseMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
