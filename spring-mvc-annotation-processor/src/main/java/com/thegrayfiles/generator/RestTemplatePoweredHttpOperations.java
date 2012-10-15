package com.thegrayfiles.generator;

import org.springframework.web.client.RestTemplate;

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
}
