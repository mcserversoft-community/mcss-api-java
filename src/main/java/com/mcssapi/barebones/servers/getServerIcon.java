package com.mcssapi.barebones.servers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class getServerIcon {

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId of the server to get the icon for
     * @param: SSL (true/false)
     * @return BufferedImage of the server icon. Null if error during request.
     */
    public static BufferedImage getServerIcon(String IP, String ApiKey, String ServerId, Boolean SSL) {
        //Get the version using the ApiKey from /api
        BufferedImage serverIcon = null;
        StringBuilder sb = new StringBuilder();
        String line;
        URL url;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/servers/" + ServerId + "/icon");
            } else {
                url = new URL("http://" + IP + "/api/v1/servers/" + ServerId + "/icon");
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

            serverIcon = javax.imageio.ImageIO.read(conn.getInputStream());
            return serverIcon;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
