package com.mcserversoft.api.users;

import java.util.HashMap;
import java.util.Map;

public class PermissionObject {
    
    private String serverId;
    private HashMap<String, Permission> permissions;

    public PermissionObject(String serverId, HashMap<String, Permission> permissions) {
        this.serverId = serverId;
        this.permissions = permissions;
    }

    public String getServerId() { return serverId; }
    public Map<String, Permission> getPermissions() { return permissions; }

    public void setPermission(String permission, Permission value) {
        permissions.put(permission, value);
    }
}
