package com.mcssapi.barebones.servers;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class runServerAction {

    /**
     * @param: IP address of the MCSS API server, including the port
     * @param: ApiKey of the MCSS API server
     * @param: ServerId of the server to execute the action
     * @param: Action ID of the action to run (1-stop,2-start,3-kill,4-restart)
     * @param: SSL (true/false)
     * @return: true if action executed correctly
     */
    public static boolean runServerAction(String IP, String ApiKey, String ServerId, int actionId, Boolean SSL) {

        URL url;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/servers/" + ServerId + "/execute/action");
            } else {
                url = new URL("http://" + IP + "/api/v1/servers/" + ServerId + "/execute/action");
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);
            conn.setRequestProperty("APIKey", ApiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            String json = "{\"actionId\":" + actionId + "}";
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes());
            os.flush();
            os.close();
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
