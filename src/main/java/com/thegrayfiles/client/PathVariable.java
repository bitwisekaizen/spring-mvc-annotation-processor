package com.thegrayfiles.client;

public class PathVariable {
    private String name;
    private Class<?> type;

    public PathVariable(Class<String> type, String name) {
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
