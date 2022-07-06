package com.mcssapi.barebones;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class getApiVersion {

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: SSL (true/false)
     * @return the apiVersion
     */
    public static String getApiVersion(String IP, String ApiKey, Boolean SSL) {
        //Get the version using the ApiKey from /api/v1
        String apiVersion = "";
        String line;
        URL url;
        try {
            if (SSL) {
                url = new URL("https://" + IP + "/api/v1/");
            } else {
                url = new URL("http://" + IP + "/api/v1/");
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);
            conn.setRequestProperty("APIKey", ApiKey);

            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 401) {
                return "ERR_KEY401";
            } else if (responseCode == 403) {
                return "ERR_KEY403";
            } else if (responseCode != 200) {
                return "ERR_"+responseCode;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                if (line.contains("apiVersion")) {
                    apiVersion = line.substring(line.indexOf(":") + 2, line.length() - 1);
                    break;
                }
            }
            reader.close();
            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "URL_ERR";
        } catch (IOException e) {
            e.printStackTrace();
            return "IO_ERR";
        }

        return apiVersion;
    }
}
