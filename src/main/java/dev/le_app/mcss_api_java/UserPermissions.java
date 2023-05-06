package dev.le_app.mcss_api_java;

public enum UserPermissions {

    VIEW_STATS("VIEW_STATS"),
    VIEW_CONSOLE("VIEW_CONSOLE"),
    USE_CONSOLE("USE_CONSOLE"),
    USE_SERVER_ACTIONS("USE_SERVER_ACTIONS");

    private final String permission;

    UserPermissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public static UserPermissions findByVal(String permission) {
        for (UserPermissions p : UserPermissions.values()) {
            if (p.getPermission().equals(permission)) {
                return p;
            }
        }
        return null;
    }


}
