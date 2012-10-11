package com.thegrayfiles.tests.acceptance;

import com.thegrayfiles.marshallable.TestEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SimpleControllerTests {

    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void canGetResourceFromController() {
        TestEntity response = restTemplate.getForEntity("http://localhost:8080/test-webapp/ws/test", TestEntity.class).getBody();
        assertEquals(response.getName(), "test", "Response entity name is incorrect.");
    }
}
