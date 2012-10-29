package com.thegrayfiles.generator;

public interface JavaClientHttpOperations {
    public <T> T get(String mapping, Class<T> responseType);
    public <T> T post(String mapping, Class<T> responseType, Object requestBody);
}
