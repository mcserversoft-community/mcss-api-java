package com.mcssapi;

import com.mcssapi.exceptions.APIInvalidTaskDetailsException;
import com.mcssapi.exceptions.APINotFoundException;
import com.mcssapi.exceptions.APIUnauthorizedException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Scheduler {

    private final MCSSApi api;
    private final String GUID;

    protected Scheduler(MCSSApi api, String GUID) {
        this.api = api;
        this.GUID = GUID;


    }


    /**
     * Get total task number
     * @return INT of the total task amount
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an IO error (e.g. server is offline)
     */
    public int getTotalTaskAmount() throws APIUnauthorizedException, APINotFoundException, IOException {

        //GET /api/v1/servers/{GUID}/scheduler/
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/");

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401, throw an APIUnauthorizedException
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when getting info.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when getting info.");
        }

        //save the response in a JSONObject
        JSONObject json = new JSONObject(conn.getOutputStream());

        //close connection
        conn.disconnect();

        //return the total task amount
        return json.getInt("tasks");
    }

    /**
     * Get number of tasks matching filter
     * @param filter The type of task to filter by
     * @return INT of the number of tasks matching the filter
     * @throws APIUnauthorizedException if the API key is invalid
     * @throws APINotFoundException if the server is not found
     * @throws IOException if there is an IO error (e.g. server is offline)
     */
    public int getTotalTaskAmount(TaskType filter) throws APIUnauthorizedException, APINotFoundException, IOException {

        //GET /api/v1/servers/{GUID}/scheduler/
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/?filter=" + filter.getValue());

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401, throw an APIUnauthorizedException
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when getting info.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when getting info.");
        }

        //save the response in a JSONObject
        JSONObject json = new JSONObject(conn.getOutputStream());

        //close connection
        conn.disconnect();

        //return the total task amount
        return json.getInt("tasks");
    }

    public ArrayList<Task> getTasks() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {
        //GET /api/v1/servers/{GUID}/scheduler/
        URL url = new URL("https://" + api.IP + "/api/v1/servers/" + GUID + "/scheduler/");

        //create a connection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //set the request method and request properties
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);// 5000 milliseconds = 5 seconds
        conn.setReadTimeout(5000);
        conn.setRequestProperty("APIKey", api.token);

        //connect to the server
        conn.connect();

        //get the response code
        int responseCode = conn.getResponseCode();

        //if the response code is 401, throw an APIUnauthorizedException
        if (responseCode == 401) {
            throw new APIUnauthorizedException("Got 401 response code when getting info.");
        } else if (responseCode == 404) {
            throw new APINotFoundException("Got 404 response code when getting info.");
        }

        //save the response in a JSONObject
        JSONObject json = new JSONObject(conn.getOutputStream());
        JSONArray jsonArray = json.getJSONArray("tasks");
        ArrayList<Task> tasks = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject task = jsonArray.getJSONObject(i);
            tasks.add(new Task(api, GUID, task.getString("taskId"),
                    task.getString("name"), task.getBoolean("enabled")));
        }
        //close connection
        conn.disconnect();
        return tasks;
    }


}
