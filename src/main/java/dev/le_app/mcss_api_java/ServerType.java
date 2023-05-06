package dev.le_app.mcss_api_java;

public enum ServerType {

    VANILLA("Vanilla"),
    SPIGOT("Spigot"),
    PAPER("Paper"),
    PURPUR("Purpur"),
    FORGE("Forge"),
    FABRIC("Fabric"),
    BUNGEECORD("BungeeCord"),
    WATERFALL("Waterfall"),
    CRAFTBUKKIT("CraftBukkit"),
    BEDROCK("Bedrock");

    private final String type;

    ServerType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static ServerType findByVal(String type) {
        for (ServerType t : ServerType.values()) {
            if (t.getType().equals(type)) {
                return t;
            }
        }
        return null;
    }

}
