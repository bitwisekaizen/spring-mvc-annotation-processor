package com.thegrayfiles.method;

public class MethodParameter {
    private Class<?> type;
    private String name;

    public MethodParameter(Class<?> type, String name) {
        this.type = type;
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
