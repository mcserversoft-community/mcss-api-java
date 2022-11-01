package dev.le_app.mcss_api_java;

import dev.le_app.mcss_api_java.exceptions.APIInvalidTaskDetailsException;
import dev.le_app.mcss_api_java.exceptions.APINotFoundException;
import dev.le_app.mcss_api_java.exceptions.APIUnauthorizedException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a job.
 */
public abstract class Job {

    /**
     * Get the action of the task.
     * @return The action of the task.
     * @throws APIUnauthorizedException If the API key is not valid.
     * @throws APINotFoundException If the server is not found.
     * @throws IOException If there is an IO error (e.g. server is offline).
     * @throws APIInvalidTaskDetailsException If the task details are invalid.
     */
    public abstract ServerAction getAction() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException;

    /**
     * Get the commands of the task.
     * @return The commands of the task.
     * @throws APIUnauthorizedException If the API key is not valid.
     * @throws APINotFoundException If the server is not found.
     * @throws IOException If there is an IO error (e.g. server is offline).
     * @throws APIInvalidTaskDetailsException If the task details are invalid.
     */
    public abstract ArrayList<String> getCommands() throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException;

    /**
     * Get the Backup ID of the backup executed by the task
     * @return The Backup ID of the backup executed by the task
     * @throws APIUnauthorizedException If the API key is not valid.
     * @throws APINotFoundException If the server is not found.
     * @throws IOException If there is an IO error (e.g. server is offline).
     */
    public abstract String getBackupGUID() throws APIUnauthorizedException, APINotFoundException, IOException;

    /**
     * Set the new action to execute
     * @param action The new action to execute
     * @return this.Job (used for concatenating)
     * @throws APIUnauthorizedException If the API key is not valid.
     * @throws APINotFoundException If the server is not found.
     * @throws APIInvalidTaskDetailsException If the task details are invalid.
     * @throws IOException If there is an IO error (e.g. server is offline).
     */
    public abstract Job setAction(ServerAction action) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException;

    /**
     * Set the new commands to execute
     * @param commands The new commands to execute
     * @return this.Job (used for concatenating)
     * @throws APIUnauthorizedException If the API key is not valid.
     * @throws APINotFoundException If the server is not found.
     * @throws APIInvalidTaskDetailsException If the task details are invalid.
     * @throws IOException If there is an IO error (e.g. server is offline).
     */
    public abstract Job setCommands(String... commands) throws APIUnauthorizedException, APINotFoundException, APIInvalidTaskDetailsException, IOException;

    /**
     * Set the new backup to execute
     * @param backupGUID The new backup to execute
     * @return this.Job (used for concatenating)
     * @throws APIUnauthorizedException If the API key is not valid.
     * @throws APINotFoundException If the server is not found.
     * @throws APIInvalidTaskDetailsException If the task details are invalid.
     * @throws IOException If there is an IO error (e.g. server is offline).
     */
    public abstract Job setBackupGUID(String backupGUID) throws APIUnauthorizedException, APINotFoundException, IOException, APIInvalidTaskDetailsException;


}
