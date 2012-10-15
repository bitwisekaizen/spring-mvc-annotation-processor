package com.thegrayfiles.generator;

import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RestTemplatePoweredHttpOperations implements JavaClientHttpOperations {

    private String wsRoot;
    private RestTemplate restTemplate;

    public RestTemplatePoweredHttpOperations(String wsRoot) {
        this.wsRoot = wsRoot;
        restTemplate = new RestTemplate();
    }

    public <T> T get(String mapping, Class<T> responseType) {
        return restTemplate.getForEntity(wsRoot + mapping, responseType).getBody();
    }

    public <T> T get(String mapping, Class<T> responseType, Map<String, Object> requestParameters) {
        return restTemplate.getForEntity(wsRoot + mapping, responseType, requestParameters).getBody();
    }
}
