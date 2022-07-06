package com.mcssapi;


import com.mcssapi.barebones.getApiVersion;
import com.mcssapi.barebones.scheduler.getSchedulerInfo;
import com.mcssapi.barebones.scheduler.getSchedulerTask;
import com.mcssapi.barebones.scheduler.getSchedulerTasks;
import com.mcssapi.barebones.servers.*;
import org.json.JSONObject;

import java.awt.image.BufferedImage;

public class Wrapper {

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: SSL (true/false)
     * @return the apiVersion
     */
    public static String getApiVersion(String IP, String ApiKey, Boolean SSL) {
        return getApiVersion.getApiVersion(IP, ApiKey, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId of the server to get the stats of
     * @param: SSL (true/false)
     * @return the server stats as a JSON object
     */
    public static JSONObject getServerStats(String IP, String ApiKey, String ServerId, Boolean SSL) {
        return getServerStats.getServerStats(IP, ApiKey, ServerId, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId of the server to get the icon of
     * @param: SSL (true/false)
     * @return the server information as a JSON object
     */
    public static JSONObject getServer(String IP, String ApiKey, String ServerId, Boolean SSL) {
        return getServer.getServer(IP, ApiKey, ServerId, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId of the server to get the info of
     * @param: SSL (true/false)
     * @param: Filter (none, minimal, status) see documentation for more information
     * @return the server information as a JSON object
     */
    public static JSONObject getServer(String IP, String ApiKey, String ServerId, Boolean SSL, String filter) {
        return getServer.getServer(IP, ApiKey, ServerId, SSL, filter);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId of the server to get the icon of
     * @param: SSL (true/false)
     * @return BufferedImage of the server icon. Null if error during request.
     */
    public static BufferedImage getServerIcon(String IP, String ApiKey, String ServerId, Boolean SSL) {
        return getServerIcon.getServerIcon(IP, ApiKey, ServerId, SSL);
    }

    /**
     * @param: IP address of the MCSS API server, including the port
     * @param: ApiKey of the MCSS API server
     * @param: ServerId of the server to execute the action
     * @param: Action ID of the action to run (1-stop,2-start,3-kill,4-restart)
     * @param: SSL (true/false)
     * @return: true if action executed correctly
     */
    public static boolean runServerAction(String IP, String ApiKey, String ServerId, int actionId, Boolean SSL) {
        return runServerAction.runServerAction(IP, ApiKey, ServerId, actionId, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId of the server to run the command on
     * @param: Command to run
     * @param: SSL (true/false)
     * @return the server information as a JSON object
     */
    public static boolean runServerCommand(String IP, String ApiKey, String ServerId, String command, Boolean SSL) {
        return runServerCommand.runServerCommand(IP, ApiKey, ServerId, command, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: ServerId of the server to get the icon of
     * @param: CommandS to run
     * @param: separator of each command
     * @param: SSL (true/false)
     * @return True if successful, false if not
     */
    public static boolean runServerCommands(String IP, String ApiKey, String ServerId, String commands, String separator, Boolean SSL) {
        return runServerCommands.runServerCommands(IP, ApiKey, ServerId, commands, separator, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: SSL (true/false)
     * @return Number of servers, -1 if error during request.
     */
    public static int getServerCount(String IP, String ApiKey, Boolean SSL) {
        return getServerCount.getServerCount(IP, ApiKey, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @param: SSL (true/false)
     * @return Number of servers, -1 if error during request.
     */
    public static int getOnlineServerCount(String IP, String ApiKey, Boolean SSL) {
        return getOnlineServerCount.getOnlineServerCount(IP, ApiKey, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @Param: ServerId of the server to get the info of
     * @param: SSL (true/false)
     * @return the server information as a JSON object. Null if error during request.
     */
    public static JSONObject getSchedulerInfo(String IP, String ApiKey, String ServerId, Boolean SSL) {
        return getSchedulerInfo.getSchedulerInfo(IP, ApiKey, ServerId, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @Param: ServerId of the server to get the info of
     * @param: SSL (true/false)
     * @return the server information as a JSON object. Null if error during request.
     */
    public static JSONObject getSchedulerTasks(String IP, String ApiKey, String ServerId, Boolean SSL) {
        return getSchedulerTasks.getSchedulerTasks(IP, ApiKey, ServerId, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @Param: ServerId of the server to get the info of
     * @param: Filter (None, FixedTime, Interval, Timeless)
     * @param: SSL (true/false)
     * @return the server information as a JSON object. Null if error during request.
     */
    public static JSONObject getSchedulerTasks(String IP, String ApiKey, String ServerId, String filter, Boolean SSL) {
        return getSchedulerTasks.getSchedulerTasks(IP, ApiKey, ServerId, filter, SSL);
    }

    /**
     * @Param: IP address of the MCSS API server, including the port
     * @Param: ApiKey of the MCSS API server
     * @Param: ServerId of the server to get the info of
     * @Param: taskId of the task to get the info of
     * @param: SSL (true/false)
     * @return the server information as a JSON object. Null if error during request.
     */
    public static JSONObject getSchedulerTask(String IP, String ApiKey, String ServerId, String taskId, Boolean SSL) {
        return getSchedulerTask.getSchedulerTask(IP, ApiKey, ServerId, taskId, SSL);
    }

}
