package com.thegrayfiles.generator;

import java.util.Map;

public interface JavaClientHttpOperations {
    public <T> T get(String mapping, Class<T> responseType);

    public <T> T get(String mapping, Class<T> responseType, Map<String, Object> requestParameters);
}
