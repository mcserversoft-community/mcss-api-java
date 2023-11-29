package com.mcserversoft.api.users;

import java.util.ArrayList;

import org.json.JSONObject;

import com.mcserversoft.api.MCSS;
import com.mcserversoft.api.utilities.Request;
import com.mcserversoft.commons.responses.Response;
import com.mcserversoft.commons.responses.user.UserResponse;
import com.mcserversoft.commons.responses.user.UsersResponse;
import com.mcserversoft.commons.structures.UserBuilder;

public class Users {
    
    private Request request;

    public Users() {
        this.request = MCSS.getRequest();
    }

    public ArrayList<UserResponse> get() throws Exception {
        UsersResponse users = new UsersResponse(this.request.GET("/users"));
        return users.getUsers();
    }

    public UserResponse get(String userId) throws Exception {
        return new UserResponse(this.request.GET("/users/" + userId));
    }

    public Response create(UserBuilder user) throws Exception {
        return new Response(this.request.POST("/users", user.toJSON().put("passwordRepeat", user.getPassword())));
    }

    public Response update(String userId, UserBuilder user) throws Exception {
        return new Response(this.request.PUT("/users/" + userId, user.toJSON()));
    }

    public Response delete(String userId) throws Exception {
        return new Response(this.request.DELETE("/users/" + userId));
    }

    public Response wipeSessions() throws Exception {
        return new Response(this.request.POST("/users/wipe/sessions", new JSONObject()));
    }

}
