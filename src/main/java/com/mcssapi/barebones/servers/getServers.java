package com.mcssapi.barebones.servers;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class getServers {

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @return JSONObject containing the servers, or null if error during request.
     */
    public static JSONObject get(String IP, String ApiKey, @Nullable String filter) {
        //api/v1/servers
        URL url;
        try {
            if (filter == null)
            url = new URL("https://" + IP + "/api/v1/servers");
            else
            url = new URL("https://" + IP + "/api/v1/servers?filter=" + filter);

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
            //return the jsonobject
            return new JSONObject(conn.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}
