package com.mcssapi;

import com.mcssapi.barebones.getApiVersion;
import com.mcssapi.barebones.servers.getServerCount;
import com.mcssapi.barebones.servers.getServers;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

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

    /**
     * Get the number of servers. Parameters can be NULL
     * @param filter not required, 0 for all, 1 for online, 2 for offline, 3 if using servertype filter
     * @param serverTypeID only required if using filter 3, servertypeID is the GUID of a servertype
     * @return number of servers, -1 if error during request. (INT)
     */
    public int getServerCount(@Nullable String filter, @Nullable String serverTypeID) {
        try {
            return getServerCount.get(IP, token, filter, serverTypeID);
        } catch (Exception e) {
            System.out.println("Error while getting server count! Error: " + e.getMessage());
            return -1;
        }
    }

    class getServerData extends mcssapi {

        public getServerData(String IP, String token) {
            super(IP, token);
        }

        /**
         * Get the servers. Parameters can be NULL
         * @param filter can be 0,1,2. 0 = no filter, 1 = minimal info, 2 = status info
         * @return JSON object containing the servers, or null if error during request.
         */
        public JSONObject getServersAsJSON(@Nullable String filter) {
            try {
                return com.mcssapi.barebones.servers.getServers.get(IP, token, filter);
            } catch (Exception e) {
                System.out.println("Error while getting servers! Error: " + e.getMessage());
                return null;
            }
        }

        public String[] getGUID(@Nullable String filter) {
            try {
                JSONObject json = com.mcssapi.barebones.servers.getServers.get(IP, token, filter);
                assert json != null;
                String[] GUIDs = new String[json.length()];
                for (int i = 0; i < json.length(); i++) {
                    GUIDs[i] = json.getJSONObject(String.valueOf(i)).getString("GUID");
                }
                return GUIDs;
            } catch (Exception e) {
                System.out.println("Error while getting servers! Error: " + e.getMessage());
                return null;
            }
        }
    }


}
