package com.mcserversoft.commons.responses.user;

import java.util.ArrayList;

import org.json.JSONObject;

import com.mcserversoft.commons.responses.Response;

public class UsersResponse extends Response {
    
    private JSONObject json;

    private ArrayList<UserResponse> users;

    public UsersResponse(JSONObject responses) {
        super(responses);
        this.json = responses;
        this.users = new ArrayList<UserResponse>();
        for (int i = 0; i < json.getJSONArray("data").length(); i++) {
            users.add(new UserResponse(json.getJSONArray("data").getJSONObject(i)));
        }
    }

    public ArrayList<UserResponse> getUsers() {
        return this.users;
    }

}
