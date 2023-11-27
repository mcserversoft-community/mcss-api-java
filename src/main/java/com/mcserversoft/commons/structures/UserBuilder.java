package com.mcserversoft.commons.structures;

import java.util.HashMap;

import org.json.JSONObject;

import com.mcserversoft.api.users.Permission;
import com.mcserversoft.api.users.PermissionObject;

public class UserBuilder {

    private String username;
    private String password;
    private boolean enabled;
    private boolean isAdmin;
    private boolean hasAccessToAllServers;
    private PermissionObject[] permissions;

    public UserBuilder(JSONObject json) {
        this.username = json.getString("username");
        this.password = json.getString("password");
        this.enabled = json.getBoolean("enabled");
        this.isAdmin = json.getBoolean("isAdmin");
        this.hasAccessToAllServers = json.getBoolean("hasAccessToAllServers");
        this.permissions = null;

        JSONObject serverPermissions = json.getJSONObject("customServerPermissions");

        for (String serverId : serverPermissions.keySet()) {
            HashMap<String, Permission> perms = new HashMap<String, Permission>();
            JSONObject serverPerms = serverPermissions.getJSONObject(serverId);
            for (String perm : serverPerms.keySet()) {
                perms.put(perm, Permission.fromString(serverPerms.getString(perm)));
            }
            permissions[permissions.length] = new PermissionObject(serverId, perms);
        }
    }

    public UserBuilder() {
        this.username = null;
        this.password = null;
        this.enabled = false;
        this.isAdmin = false;
        this.hasAccessToAllServers = false;
        this.permissions = null;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isEnabled() { return enabled; }
    public boolean isAdmin() { return isAdmin; }
    public boolean hasAccessToAllServers() { return hasAccessToAllServers; }
    public PermissionObject[] getPermissions() { return permissions; }

    public UserBuilder setUsername(String username) { this.username = username; return this; }
    public UserBuilder setPassword(String password) { this.password = password; return this; }
    public UserBuilder setEnabled(boolean enabled) { this.enabled = enabled; return this; }
    public UserBuilder setAdmin(boolean isAdmin) { this.isAdmin = isAdmin; return this; }
    public UserBuilder setHasAccessToAllServers(boolean hasAccessToAllServers) { this.hasAccessToAllServers = hasAccessToAllServers; return this; }

    public UserBuilder setPermissions(PermissionObject[] permissions) { this.permissions = permissions; return this; }
    public UserBuilder setPermissions(JSONObject permissions) {
        for (String serverId : permissions.keySet()) {
            HashMap<String, Permission> perms = new HashMap<String, Permission>();
            JSONObject serverPerms = permissions.getJSONObject(serverId);
            for (String perm : serverPerms.keySet()) {
                perms.put(perm, Permission.fromString(serverPerms.getString(perm)));
            }
            this.permissions[this.permissions.length] = new PermissionObject(serverId, perms);
        }
        return this;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("username", this.username);
        json.put("password", this.password);
        json.put("enabled", this.enabled);
        json.put("isAdmin", this.isAdmin);
        json.put("hasAccessToAllServers", this.hasAccessToAllServers);
        json.put("customServerPermissions", this.permissions);
        return json;
    }


}
