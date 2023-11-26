package com.mcserversoft.api.users;

import java.util.ArrayList;

import com.mcserversoft.api.utilities.Request;
import com.mcserversoft.commons.responses.user.UserResponse;
import com.mcserversoft.commons.responses.user.UsersResponse;

public class Users {
    
    private Request request;

    public Users(Request request) {
        this.request = request;
    }

    public ArrayList<UserResponse> get() throws Exception {
        UsersResponse users = new UsersResponse(request.GET("/users"));
        return users.getUsers();
    }

    public UserResponse get(String userId) throws Exception {
        return new UserResponse(request.GET("/users/" + userId));
    }

}
