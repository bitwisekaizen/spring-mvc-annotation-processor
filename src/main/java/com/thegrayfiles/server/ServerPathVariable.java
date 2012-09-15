package com.thegrayfiles.server;

public class ServerPathVariable {
    private String name;
    private Class<?> type;

    public ServerPathVariable(Class<String> type, String name) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return this.type;
    }
}
