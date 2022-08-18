package com.mcssapi;

import com.mcssapi.barebones.getApiVersion;
import com.mcssapi.barebones.servers.getServerCount;
import com.mcssapi.exceptions.APIUnauthorizedException;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class mcssapi {

    public boolean online = false;
    protected String IP = null;
    protected String token = null;
    protected String version = null;

    public mcssapi(String IP, String token) {
        this.IP = IP;
        this.token = token;
    }

    public Info getInfo() throws IOException, APIUnauthorizedException {
            URL url;

            url = new URL("https://" + IP + "/api/v1/info");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
            conn.setReadTimeout(5000);
            conn.setRequestProperty("APIKey", token);

            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 401) {
                throw new APIUnauthorizedException("Got 401 response code when getting info.");
            }

            //save the response in a JSONObject
            JSONObject json = new JSONObject(conn.getOutputStream());

            return new Info(json.getBoolean("isDev"), json.getString("MCSSVersion"), json.getString("MCSSApiVersion"), json.getString("UniqueID"), json.getBoolean("youAreAwesome"));
    }


    /**
     * Get the version of the API
     * @return version as a string, or ERR_ and the error code. As an
     * example, ERR_401 means that the API returned an error code 401
     */
    /*
    public String getApiVersion() {
        try {
            this.version = getApiVersion.get(IP, token);
        } catch (Exception e) {
            this.version = "ERR_" + e.getMessage();
            System.out.println("Error while getting API version! Error: " + e.getMessage());
        }
        return this.version;
    }
    */


    /**
     * Get the number of servers. Parameters can be NULL
     * @param filter not required, 0 for all, 1 for online, 2 for offline, 3 if using servertype filter
     * @param serverTypeID only required if using filter 3, servertypeID is the GUID of a servertype
     * @return number of servers, -1 if error during request. (INT)
     */
    /*
    public int getServerCount(@Nullable String filter, @Nullable String serverTypeID) {
        try {
            return getServerCount.get(IP, token, filter, serverTypeID);
        } catch (Exception e) {
            System.out.println("Error while getting server count! Error: " + e.getMessage());
            return -1;
        }
    }
    */




}
