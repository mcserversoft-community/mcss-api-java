package dev.le_app.mcss_api_java;

import dev.le_app.mcss_api_java.exceptions.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The main class of the API.
 */
public class MCSSApi {

    protected String IP = null;
    protected String token = null;
    protected String version = null;
    protected String expectedVersion = "2.0.0";

    protected Boolean allowUnsafeSSL = false;

    /**
     * Create a new MCSSApi object
     * @param IP The IP of the MCSS server
     * @param token The token of the MCSS server
     * @throws APIUnauthorizedException If the token is invalid
     * @throws APIVersionMismatchException If the API version of the MCSS server is not the same as the expected version
     * @throws IOException If there is an error connecting to the MCSS server
     */
    public MCSSApi(String IP, String token) throws APIUnauthorizedException, APIVersionMismatchException, IOException {
        this.IP = IP;
        this.token = token;

        Info in = getInfo();
        this.version = in.getMCSSApiVersion();
        checkVersionMismatch();
    }

    /**
     * Get the API object
     * @param IP The IP of the MCSS server
     * @param token The token of the MCSS server
     * @param allowUnsafeSSL True if you want to avoid checking the SSL certificate
     * @throws APIUnauthorizedException If the token is invalid
     * @throws APIVersionMismatchException If the API version is not the same as the expected version
     * @throws IOException If there is an error while connecting to the API
     * @throws KeyManagementException If there is an error with the KeyManagment
     * @throws NoSuchAlgorithmException If there is an error with the SSLContext
     */
    public MCSSApi(String IP, String token, Boolean allowUnsafeSSL) throws APIUnauthorizedException, APIVersionMismatchException, IOException, NoSuchAlgorithmException, KeyManagementException {
        this.IP = IP;
        this.token = token;
        this.allowUnsafeSSL = allowUnsafeSSL;

        if (allowUnsafeSSL) {
            TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            //Warn the user of the unsafe SSL connection
            System.out.println("[MCSSAPI] WARNING: Unsafe SSL connection enabled! NO SSL CERTIFICATES WILL BE VERIFIED!");

        }

        Info in = getInfo();
        this.version = in.getMCSSApiVersion();
        checkVersionMismatch();
    }

    /**
     * Resets all web panel sessions, meaning that every web panel user will be logged out,
     * and will have to log in again.
     */
    public void wipeSessions() throws IOException, APIUnauthorizedException, APIServerSideException, APINotFoundException {

        URL url = new URL (Endpoints.WIPE_SESSIONS.getEndpoint().replace("{IP}", IP));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("apikey", token);

        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode == 401 ) {
            throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
        } else if (responseCode == 403) {
            throw new APIUnauthorizedException(Errors.NOT_ADMIN.getMessage());
        } else if (responseCode == 500 ) {
            throw new APIServerSideException(Errors.API_ERROR.getMessage());
        } else if (responseCode == 404) {
            throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
        }

        conn.disconnect();
    }


    /**
     * Get general information about the MCSS install
     * @return Info object containing the information
     * @throws IOException General IO error
     * @throws APIUnauthorizedException API token is invalid/expired
     */
    public Info getInfo() throws IOException, APIUnauthorizedException {
        URL url;

        url = new URL(Endpoints.ROOT.getEndpoint().replace("{IP}", IP));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", token);
        conn.setDoInput(true);


        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
        }

        //Read the response
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

        //close connection
        conn.disconnect();

        return new Info(json.getBoolean("isDevBuild"), json.getString("mcssVersion"),
                json.getString("mcssApiVersion"), json.getString("uniqueIdentifier"),
                json.getBoolean("youAreAwesome"));
    }

    /**
     * Get the list of servers
     * @return ArrayList of servers
     * @throws APIUnauthorizedException API token is invalid/expired
     * @throws APINotFoundException API not found
     * @throws IOException General IO error
     * @throws APINoServerAccessException API does not have access to any server
     */
    public ArrayList<Server> getServers() throws APIUnauthorizedException, APINotFoundException, IOException, APINoServerAccessException {

        //create the ArrayList
        ArrayList<Server> servers = new ArrayList<>();

        //create the URL
        URL url = new URL(Endpoints.SERVERS.getEndpoint().replace("{IP}", IP));
        //Create and open the connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the connection variables, request proprieties and request method
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", token);
        conn.setDoInput(true);

        //Connect to the API
        conn.connect();
        //Get the response code of the connection
        int responseCode = conn.getResponseCode();
        //if the responsecode is an error, throw an exception
        if (responseCode == 401) {
            throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
        } else if (responseCode == 404) {
            //Might never fire, better safe than sorry
            throw new APINotFoundException(Errors.NOT_FOUND.getMessage());
        } else if (responseCode == 403) {
            throw new APINoServerAccessException(Errors.NO_SERVER_ACCESS.getMessage());
        }

        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONArray json = new JSONArray(new JSONTokener(reader));

        //close connection
        conn.disconnect();
        //Create the JsonArray from the JSONObject
        JSONArray serversArray = new JSONArray(json);
        //Create a DateTimeFormatter to parse the creationDate
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        //loop through the JsonArray and create a Server object for each server
        for (int i = 0; i < serversArray.length(); i++) {
            JSONObject server = serversArray.getJSONObject(i);
            //Create the Server object with parsed values from JSON, and add it to the ArrayList
            servers.add(new Server(server.getString("guid"), this));
        }

        //return the ArrayList
        return servers;

    }

    private void checkVersionMismatch() throws APIVersionMismatchException {
        if (!Objects.equals(version, expectedVersion)) {
            throw new APIVersionMismatchException(Errors.VERSION_MISMATCH.getMessage().replace("{GOT}", version)
                    .replace("{EXPECTED_VERSION}", expectedVersion));
        }
    }


    /**
     * Get the number of servers.
     * @throws APIUnauthorizedException if the APIKey is invalid
     * @throws IOException if there is an error with the connection
     * @return number of servers
     */
    public int getServerCount() throws APIUnauthorizedException, IOException {
        URL url = new URL(Endpoints.SERVER_COUNT.getEndpoint().replace("{IP}", IP));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", token);
        conn.setDoInput(true);

        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
        }

        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

        //close connection
        conn.disconnect();
        return json.getInt("count");
    }

    /**
     * Get the number of servers.
     * @param filter the filter to use
     * @throws APIUnauthorizedException if the APIKey is invalid
     * @throws IOException if there is an error with the connection
     * @return number of servers
     */
    public int getServerCount(ServerFilter filter) throws APIUnauthorizedException, IOException {
        URL url = new URL( Endpoints.SERVER_COUNT_FILTER.getEndpoint().replace("{IP}", IP)
                .replace("{FILTER}", filter.getValueStr()));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", token);
        conn.setDoInput(true);

        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
        }

        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

        //close connection
        conn.disconnect();
        return json.getInt("count");
    }

    /**
     * Get the number of servers. Only used for the servertype filter.
     * @param filter the ServerFilter to use
     * @param serverTypeID Only required if the filter is FILTER
     * @throws APIUnauthorizedException if the APIKey is invalid
     * @throws IOException if there is an error with the connection
     * @return number of servers
     */
    public int getServerCount(ServerFilter filter, String serverTypeID) throws APIUnauthorizedException, IOException {

        if (filter != ServerFilter.FILTER) {
            throw new IllegalArgumentException(Errors.ID_FILTER_ERROR.getMessage());
        }

        URL url = new URL(Endpoints.SERVER_COUNT_FILTER_SRVTYPE.getEndpoint().replace("{IP}", IP)
                .replace("{FILTER}", filter.getValueStr())
                .replace("{SRVTYPE}", serverTypeID));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", token);
        conn.setDoInput(true);

        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode == 401) {
            throw new APIUnauthorizedException(Errors.UNAUTHORIZED.getMessage());
        }

        //save the response in a JSONObject
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        JSONObject json = new JSONObject(new JSONTokener(reader));

        //close connection
        conn.disconnect();
        return json.getInt("count");
    }

}
