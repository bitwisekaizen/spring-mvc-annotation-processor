package com.thegrayfiles.tests.acceptance;

import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class SimpleControllerTests {

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void canGetResourceFromController() {
        String response = restTemplate.getForEntity("http://localhost:8080/test", String.class).getBody();
        assertTrue(response.length() != 0, "Response string length is zero!");
    }
}
