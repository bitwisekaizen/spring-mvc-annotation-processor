package com.thegrayfiles.method;

public class MethodParameter {
    private Class<?> type;

    public MethodParameter(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }
}
