package com.mcssapi;

import com.mcssapi.barebones.getApiVersion;
import com.mcssapi.barebones.servers.getServerCount;
import org.jetbrains.annotations.Nullable;

public class mcssapi {

    public boolean online = false;
    protected String IP = null;
    protected String token = null;
    protected String version = null;

    public mcssapi(String IP, String token) {
        this.IP = IP;
        this.token = token;
    }

    /**
     * Get the version of the API
     * @return version as a string, or ERR_ and the error code. As an
     * example, ERR_401 means that the API returned an error code 401
     */
    public String getApiVersion() {
        try {
            this.version = getApiVersion.get(IP, token);
        } catch (Exception e) {
            this.version = "ERR_" + e.getMessage();
            System.out.println("Error while getting API version! Error: " + e.getMessage());
        }
        return this.version;
    }


    public int getServerCount(@Nullable String filter, @Nullable String serverTypeID) {
        try {
            return getServerCount.get(IP, token, filter, serverTypeID);
        } catch (Exception e) {
            System.out.println("Error while getting server count! Error: " + e.getMessage());
            return -1;
        }
    }

}
