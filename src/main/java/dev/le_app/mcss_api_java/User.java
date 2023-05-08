package dev.le_app.mcss_api_java;

import dev.le_app.mcss_api_java.exceptions.APINotFoundException;
import dev.le_app.mcss_api_java.exceptions.APIServerSideException;
import dev.le_app.mcss_api_java.exceptions.APIUnauthorizedException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class User {

    private MCSSApi api;

    /** This flag tells the code if it should execute API requests.<br>
     *  If this is a new user to be added to the API, this flag will be false.<br>
     *  If this is an existing user, this flag will be true.<br>
     * */
    private boolean functional;
    private String userID;
    private String username;
    private boolean enabled;
    private boolean isAdmin;
    private boolean hasAccessToAllServers;
    /** Structure is as follows: <br>
     * HashMap (ServerID, UserPermissions) <br>
     *     the Server ID is the ID of the server that the user has permissions on <br>
     *     the ArrayList contains the permissions that the user has on that specific server <br>
     */
    private HashMap<String, ArrayList<UserPermissions>> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;


    /**
     * Internal constructor for the user <br>
     * All exceptions that may be thrown are thrown when the user gets all of his information
     * @param api the local MCSS API
     * @param userID The existing user ID
     * @throws APIServerSideException if the API returns error 500
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws IOException if there is any error during the API request
     * @throws APINotFoundException if the user is not found (should never be thrown)
     */
    private User (MCSSApi api, String userID) throws APIServerSideException, APIUnauthorizedException, IOException, APINotFoundException {
        this.api = api;
        this.userID = userID;
        this.functional = true;
        updateDetails();
    }

    public User (String username, boolean enabled, boolean admin, boolean hasAccessToAllServers) {


        this.functional = false;
        this.username = username;
        this.enabled = enabled;
        this.isAdmin = admin;
        this.hasAccessToAllServers = hasAccessToAllServers;
        if (!hasAccessToAllServers) permissions= null;
        

    }

    //GETTERS - It's 5AM and i'm tired :P

    /**
     * Returns the userID of the user
     * @return The userID of the user, alphanumeric string
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Returns the username of the user
     * @return The username of the user, string
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the enabled status of the user
     * @return The enabled status of the user, boolean
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns true if the user is an administrator
     * @return The admin status of the user, boolean
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     * Returns true if the user has access to all servers (if trye, the permissions HashMap will be null)
     * @return The hasAccessToAllServers status of the user, boolean
     */
    public boolean isHasAccessToAllServers() {
        return hasAccessToAllServers;
    }

    /**
     * Returns the permissions of the user <br>
     * @return The permissions of the user, HashMap (ServerID, UserPermissions) - MAY BE NULL IF hasAccessToAllServers IS TRUE
     */
    public HashMap<String, ArrayList<UserPermissions>> getPermissions() {
        return permissions;
    }

    /**
     * Returns the LocalDateTime of when the user was created
     * @return The creation LocalDateTime
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns the LocalDateTime of when the user was last edited
     * @return The last time the user was modified as a LocalDateTime
     */
    public LocalDateTime getLastModifiedAt() {
        return lastModifiedAt;
    }

    /**
     * Update the user information from the API
     * @throws IOException if there is any error during the connection
     * @throws APIServerSideException if the MCSS api returns an error 500
     * @throws APIUnauthorizedException if the APIKey is invalid
     * @throws APINotFoundException if the User is not found (should never be thrown)
     * @throws IllegalStateException if the user was not created by the wrapper (if it's a custom user)
     */
    public void updateDetails() throws IOException, APIServerSideException, APIUnauthorizedException, APINotFoundException {

        if (!functional) throw new IllegalStateException("This user is not functional. Please get an user from the API.");
        URL url = new URL(Endpoints.USER_INFO.getEndpoint().replace("{IP}", api.IP).replace("{USER_ID}", userID));

        HttpURLConnection conn = createGetConnection(url);

        conn.connect();

        int responseCode = conn.getResponseCode();

        switch (responseCode) {
            case 500 -> throw new APIServerSideException(Errors.API_ERROR.getMessage());
            case 401 -> throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
            case 403 -> throw new APIUnauthorizedException(Errors.FORBIDDEN.getMessage());
            case 404 -> throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
            case 200 -> {
                JSONObject response = new JSONObject(new JSONTokener(new InputStreamReader(conn.getInputStream())));

                conn.disconnect();

                this.username = response.getString("username");
                this.enabled = response.getBoolean("enabled");
                this.isAdmin = response.getBoolean("isAdmin");
                this.hasAccessToAllServers = response.getBoolean("hasAccessToAllServers");
                this.permissions = new HashMap<>();
                this.createdAt = LocalDateTime.parse(response.getString("createdAt"));
                this.lastModifiedAt = LocalDateTime.parse(response.getString("lastModifiedAt"));

                JSONObject permissions = response.getJSONObject("customServerPermissions");
                for (String serverID : permissions.keySet()) {
                    JSONObject permissionsObject = permissions.getJSONObject(serverID);
                    ArrayList<UserPermissions> permissionsList = new ArrayList<>();
                    if (permissionsObject.getBoolean("viewStats")) permissionsList.add(UserPermissions.VIEW_STATS);
                    if (permissionsObject.getBoolean("viewConsole")) permissionsList.add(UserPermissions.VIEW_CONSOLE);
                    if (permissionsObject.getBoolean("useConsole")) permissionsList.add(UserPermissions.USE_CONSOLE);
                    if (permissionsObject.getBoolean("useServerActions")) permissionsList.add(UserPermissions.USE_SERVER_ACTIONS);

                    this.permissions.put(serverID, permissionsList);
                }
            }
        }
    }



    private HttpURLConnection createGetConnection(URL url) throws IOException {

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("apiKey", api.token);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setDoInput(true);

        return conn;

    }

}
