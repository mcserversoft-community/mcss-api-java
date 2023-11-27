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
        this.addHeader("Content-Type", "application/json; charset=utf-8;");
    }

    // Sets the base URL
    public void setBaseUrl(String url) {
        this.baseUrl = url;
    }

    // Adds a header to the request
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    // Replaces a header in the request
    public void setHeader(String key, String value) {
        this.headers.replace(key, value);
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
            if(response.toString().isEmpty()) return new JSONObject("{}").put("status", responseCode);
            if(!response.toString().startsWith("{")) return new JSONObject("{ \"data\":" + response.toString() + "}").put("status", responseCode);
            return new JSONObject().put("status", responseCode);
        } else {
            if(!response.toString().startsWith("{")) return new JSONObject("{ \"data\":" + response.toString() + "}").put("status", responseCode);
            return new JSONObject(response.toString()).put("status", responseCode);
        }
    }

    public JSONObject POST(String url, JSONObject body) throws IOException {

        URL postReqUrl = new URL(this.baseUrl + url);
        HttpURLConnection con = (HttpURLConnection) postReqUrl.openConnection();
        con.setRequestMethod("POST");

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
            if(response.toString().isEmpty()) return new JSONObject("{}").put("status", responseCode);
            if(!response.toString().startsWith("{")) return new JSONObject("{ \"data\":" + response.toString() + "}").put("status", responseCode);
            return new JSONObject().put("status", responseCode);
        } else {
            if(response.toString().isEmpty()) return new JSONObject("{}").put("status", responseCode);
            return new JSONObject(response.toString()).put("status", responseCode);
        }
    }

    public JSONObject PUT(String url, JSONObject body) throws IOException {
        URL putReqUrl = new URL(this.baseUrl + url);
        HttpURLConnection con = (HttpURLConnection) putReqUrl.openConnection();
        con.setRequestMethod("PUT");

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
            if(response.toString().isEmpty()) return new JSONObject("{}").put("status", responseCode);
            if(!response.toString().startsWith("{")) return new JSONObject("{ \"data\":" + response.toString() + "}").put("status", responseCode);
            return new JSONObject().put("status", responseCode);
        } else {
            if(response.toString().isEmpty()) return new JSONObject("{}").put("status", responseCode);
            return new JSONObject(response.toString()).put("status", responseCode);
        }
    }

    public JSONObject PATCH(String url, JSONObject body) throws IOException {
        URL patchReqUrl = new URL(this.baseUrl + url);
        HttpURLConnection con = (HttpURLConnection) patchReqUrl.openConnection();
        con.setRequestMethod("PATCH");

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
            if(response.toString().isEmpty()) return new JSONObject("{}").put("status", responseCode);
            if(!response.toString().startsWith("{")) return new JSONObject("{ \"data\":" + response.toString() + "}").put("status", responseCode);
            return new JSONObject().put("status", responseCode);
        } else {
            if(response.toString().isEmpty()) return new JSONObject("{}").put("status", responseCode);
            return new JSONObject(response.toString()).put("status", responseCode);
        }
    }

    public JSONObject DELETE(String url) throws IOException {
        URL deleteReqUrl = new URL(this.baseUrl + url);
        HttpURLConnection con = (HttpURLConnection) deleteReqUrl.openConnection();
        con.setRequestMethod("DELETE");

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
            if(response.toString().isEmpty()) return new JSONObject("{}").put("status", responseCode);
            if(!response.toString().startsWith("{")) return new JSONObject("{ \"data\":" + response.toString() + "}").put("status", responseCode);
            return new JSONObject().put("status", responseCode);
        } else {
            if(response.toString().isEmpty()) return new JSONObject("{}").put("status", responseCode);
            return new JSONObject(response.toString()).put("status", responseCode);
        }
    }

}