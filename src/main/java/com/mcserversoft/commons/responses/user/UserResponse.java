package com.mcserversoft.commons.responses.user;

import java.util.HashMap;

import org.json.JSONObject;

import com.mcserversoft.api.users.Permission;
import com.mcserversoft.api.users.PermissionObject;
import com.mcserversoft.commons.responses.Response;

public class UserResponse extends Response {

    private String userId;
    private String username;
    private boolean enabled;
    private boolean isAdmin;
    private boolean hasAccessToAllServers;
    private PermissionObject[] permissions;
    private String createdAt;
    private String lastModifiedAt;

    public UserResponse(JSONObject response) {
        super(response);
        this.userId = response.getString("userId");
        this.username = response.getString("username");
        this.enabled = response.getBoolean("enabled");
        this.isAdmin = response.getBoolean("isAdmin");
        this.hasAccessToAllServers = response.getBoolean("hasAccessToAllServers");
        this.createdAt = response.getString("createdAt");
        this.lastModifiedAt = response.getString("lastModifiedAt");

        JSONObject serverPermissions = response.getJSONObject("customServerPermissions");

        for (String serverId : serverPermissions.keySet()) {
            HashMap<String, Permission> perms = new HashMap<String, Permission>();
            JSONObject serverPerms = serverPermissions.getJSONObject(serverId);
            for (String perm : serverPerms.keySet()) {
                perms.put(perm, Permission.fromString(serverPerms.getString(perm)));
            }
            permissions[permissions.length] = new PermissionObject(serverId, perms);
        }

    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public boolean isEnabled() { return enabled; }
    public boolean isAdmin() { return isAdmin; }
    public boolean hasAccessToAllServers() { return hasAccessToAllServers; }
    public PermissionObject[] getPermissions() { return permissions; }
    public String getCreatedAt() { return createdAt; }
    public String getLastModifiedAt() { return lastModifiedAt; }

    public PermissionObject getPermissionObject(String serverId) {
        for (PermissionObject perm : permissions) {
            if (perm.getServerId().equals(serverId)) {
                return perm;
            }
        }
        return null;
    }

    public Permission getPermission(String serverId, String permission) {
        PermissionObject perm = getPermissionObject(serverId);
        if (perm != null) {
            return perm.getPermissions().get(permission);
        }
        return null;
    }

    public boolean hasPermission(String serverId, String permission) {
        Permission perm = getPermission(serverId, permission);
        if (perm != null) return true;
        return false;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("userId", userId);
        json.put("username", username);
        json.put("enabled", enabled);
        json.put("isAdmin", isAdmin);
        json.put("hasAccessToAllServers", hasAccessToAllServers);
        json.put("createdAt", createdAt);
        json.put("lastModifiedAt", lastModifiedAt);

        JSONObject serverPermissions = new JSONObject();

        for (PermissionObject perm : permissions) {
            JSONObject serverPerms = new JSONObject();
            for (String p : perm.getPermissions().keySet()) {
                serverPerms.put(p, perm.getPermissions().get(p).toString());
            }
            serverPermissions.put(perm.getServerId(), serverPerms);
        }
        return json;
    }
    

}
