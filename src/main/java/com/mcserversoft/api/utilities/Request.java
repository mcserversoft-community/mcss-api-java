package com.mcserversoft.api.utilities;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;

// Class to handle HTTP requests

public class Request {

    // Base URL for the API
    private String baseUrl;

    // Headers for the request
    private HashMap<String, String> headers;

    // Constructor
    public Request(String url) {
        this.baseUrl = url;
        this.headers = new HashMap<String, String>();
        this.addHeader("accept", "*/*");
        this.addHeader("User-Agent", "Mozilla/5.0");
        this.addHeader("Content-Type", "application/json; charset=utf-8;");
    }

    // Adds a header to the request
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    // Removes a header from the request
    public void removeHeader(String key) {
        this.headers.remove(key);
    }

    // Returns the headers
    public HashMap<String, String> getHeaders() {
        return this.headers;
    }

    /* HTTP REQUEST FUNCTIONS */

    public JSONObject GET(String url, Object... args) throws IOException {   
        URL getReqUrl = new URL(this.baseUrl + url);
        HttpURLConnection con = (HttpURLConnection) getReqUrl.openConnection();
        con.setRequestMethod("GET");

        for (String key : this.headers.keySet()) {
            con.setRequestProperty(key, this.headers.get(key));
        }
        
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int responseCode = con.getResponseCode();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        if(responseCode != 200) {
            return new JSONObject().put("status", responseCode);
        } else {
            return new JSONObject(response.toString()).put("status", responseCode);
        }
    }

    public JSONObject POST(String url, JSONObject body) throws IOException {

        URL postReqUrl = new URL(this.baseUrl + url);
        HttpURLConnection con = (HttpURLConnection) postReqUrl.openConnection();
        con.setRequestMethod("POST");
        con.addRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json");

        for (String key : this.headers.keySet()) {
            con.setRequestProperty(key, this.headers.get(key));
        }

        con.setDoOutput(true);

        try (DataOutputStream outputStream = new DataOutputStream(con.getOutputStream())) {
            outputStream.writeBytes(body.toString());
            outputStream.flush();
        }

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        if(responseCode != 200) {
            return new JSONObject().put("status", responseCode);
        } else {
            return new JSONObject(response.toString()).put("status", responseCode);
        }
    }

    public JSONObject PUT(String url, JSONObject body) throws IOException {
        URL putReqUrl = new URL(this.baseUrl + url);
        HttpURLConnection con = (HttpURLConnection) putReqUrl.openConnection();
        con.setRequestMethod("PUT");
        con.addRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json");

        for (String key : this.headers.keySet()) {
            con.setRequestProperty(key, this.headers.get(key));
        }

        con.setDoOutput(true);

        try (DataOutputStream outputStream = new DataOutputStream(con.getOutputStream())) {
            outputStream.writeBytes(body.toString());
            outputStream.flush();
        }

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        if(responseCode != 200) {
            return new JSONObject().put("status", responseCode);
        } else {
            return new JSONObject(response.toString()).put("status", responseCode);
        }
    }

    public JSONObject DELETE(String url) throws IOException {
        URL deleteReqUrl = new URL(this.baseUrl + url);
        HttpURLConnection con = (HttpURLConnection) deleteReqUrl.openConnection();
        con.setRequestMethod("DELETE");
        con.addRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Content-Type", "application/json");

        for (String key : this.headers.keySet()) {
            con.setRequestProperty(key, this.headers.get(key));
        }

        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        if(responseCode != 200) {
            return new JSONObject().put("status", responseCode);
        } else {
            return new JSONObject(response.toString()).put("status", responseCode);
        }
    }

}