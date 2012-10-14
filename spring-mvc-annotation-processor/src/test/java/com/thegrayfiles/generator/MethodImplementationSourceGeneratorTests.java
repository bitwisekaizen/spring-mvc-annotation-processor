package com.thegrayfiles.generator;

import com.thegrayfiles.method.MethodSignature;
import com.thegrayfiles.server.ServerEndpoint;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

public class MethodImplementationSourceGeneratorTests {

    @Test
    public void canGenerateSimpleGetRequest() {
        MethodSignature signature = new MethodSignature(void.class, "simple");
        ServerEndpoint endpoint = new ServerEndpoint("/test", signature);

        MethodImplementationSourceGenerator sourceGenerator = new MethodImplementationSourceGenerator();
        List<String> source = sourceGenerator.generate(endpoint);

        assertEquals(source.size(), 1, "Generated source should be no more than a single string.");
        assertEquals(source.get(0), "ops.get(\"/test\", void.class);", "Unexpected generated source.");
    }
}
