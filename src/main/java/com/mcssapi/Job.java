package com.mcssapi;

import com.mcssapi.ServerAction;
import com.mcssapi.exceptions.APIInvalidTaskDetailsException;
import com.mcssapi.exceptions.APINotFoundException;
import com.mcssapi.exceptions.APIUnauthorizedException;

import java.io.IOException;
import java.util.ArrayList;

public class Job {

    public ServerAction getAction() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {
        return null;
    }

    public ArrayList<String> getCommands() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {
        return null;
    }

    public String getBackupGUID() throws APIUnauthorizedException, APINotFoundException, IOException {
        return null;
    }

    public void setAction(ServerAction action) throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {
        return;
    }

    public void setCommands(String... commands) throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {
        return;
    }

    public void setBackupGUID(String backupGUID) throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException {
        return;
    }


}
