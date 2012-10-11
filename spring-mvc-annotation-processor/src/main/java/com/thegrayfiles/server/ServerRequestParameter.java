package com.thegrayfiles.server;

public class ServerRequestParameter {
    private Class<?> type;
    private String name;

    public ServerRequestParameter(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }
}
