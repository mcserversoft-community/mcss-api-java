package dev.le_app;

import dev.le_app.exceptions.APIInvalidTaskDetailsException;
import dev.le_app.exceptions.APINotFoundException;
import dev.le_app.exceptions.APIUnauthorizedException;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Job {

    public abstract ServerAction getAction() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException;

    public abstract ArrayList<String> getCommands() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException;

    public abstract String getBackupGUID() throws APIUnauthorizedException, APINotFoundException, IOException;

    public abstract Job setAction(ServerAction action) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException;

    public abstract Job setCommands(String... commands) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException;

    public abstract Job setBackupGUID(String backupGUID) throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException;


}
