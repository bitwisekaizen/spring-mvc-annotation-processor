package com.thegrayfiles;

public class RequestParameter {
    private Class<?> type;
    private String name;

    public RequestParameter(Class<?> type, String name) {
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
