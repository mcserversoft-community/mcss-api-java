package com.mcserversoft.api.users;

public enum Permission {
    VIEW_STATS("viewStats"),
    VIEW_CONSOLE("viewConsole"),
    USE_CONSOLE("useConsole"),
    USE_SERVER_ACTIONS("useServerActions"),
    EDIT_SERVER("editServer"),
    VIEW_BACKUPS("viewBackups"),
    CREATE_BACKUP("createBackup"),
    EDIT_BACKUP("editBackup"),
    DELETE_BACKUPS("deleteBackups"),
    TRIGGER_BACKUP("triggerBackup");
    
    private final String permission;
    
    Permission(String permission) {
        this.permission = permission;
    }
    
    public String getPermission() {
        return this.permission;
    }
    
    public static Permission getPermission(String permission) {
        for (Permission p : Permission.values()) {
            if (p.getPermission().equals(permission)) {
                return p;
            }
        }
        return null;
    }

    public static Permission fromString(String permission) {
        for (Permission p : Permission.values()) {
            if (p.getPermission().equals(permission)) {
                return p;
            }
        }
        return null;
    }
    
    public String toString() {
        return this.permission;
    }
}
