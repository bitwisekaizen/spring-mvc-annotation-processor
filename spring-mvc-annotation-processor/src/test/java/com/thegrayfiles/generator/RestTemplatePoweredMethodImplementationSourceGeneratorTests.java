package com.thegrayfiles.generator;

import com.thegrayfiles.method.MethodSignature;
import com.thegrayfiles.server.ServerEndpoint;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class RestTemplatePoweredMethodImplementationSourceGeneratorTests {

    @Test
    public void canGenerateSimpleGetRequest() {
        RestTemplate template = new RestTemplate();
        MethodSignature signature = new MethodSignature(void.class, "simple");
        ServerEndpoint endpoint = new ServerEndpoint("/test", signature);

        RestTemplatePoweredMethodImplementationSourceGenerator sourceGenerator = new RestTemplatePoweredMethodImplementationSourceGenerator();
        List<String> source = sourceGenerator.generate(endpoint);

        assertEquals(source.size(), 1, "Generated source should be no more than a single string.");
        assertEquals(source.get(0), "restTemplate.getForEntity(wsRoot+\"/test\",null);", "Unexpected generated source.");
    }
}
