package com.mcssapi.barebones.servers;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class getServerCount {

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @return Number of servers, -1 if error during request.
     */
    public static int get(String IP, String ApiKey, @Nullable String filter, @Nullable String serverTypeID) {
        //api/v1/servers/count
        URL url;
        try {

            if (filter == null)
            url = new URL("https://" + IP + "/api/v1/servers/count");
            else if (filter.equals("3"))
            url = new URL("https://" + IP + "/api/v1/servers/count?filter=" + filter + "?serverTypeID=" + serverTypeID);
            else
            url = new URL("https://" + IP + "/api/v1/servers/count?filter=" + filter);

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

            //save the response in a JSONObject
            JSONObject json = new JSONObject(conn.getOutputStream());

            //parse the json object to get the count
            return json.getInt("count");

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

    }
}
