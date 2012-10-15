package com.thegrayfiles.generator;

import com.thegrayfiles.method.MethodSignature;
import com.thegrayfiles.server.ServerEndpoint;
import com.thegrayfiles.server.ServerPathVariable;
import com.thegrayfiles.server.ServerRequestParameter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class MethodImplementationSourceGeneratorTests {

    private MethodImplementationSourceGenerator sourceGenerator;

    @BeforeMethod
    public void setup() {
        sourceGenerator = new MethodImplementationSourceGenerator();
    }

    @Test
    public void canGenerateSimpleGetRequest() {
        MethodSignature signature = new MethodSignature(void.class, "simple");
        ServerEndpoint endpoint = new ServerEndpoint("/test", signature);

        List<String> source = sourceGenerator.generate(endpoint);

        assertEquals(source.size(), 1, "Generated source should be no more than a single string.");
        assertEquals(source.get(0), "ops.get(\"/test\",void.class);", "Unexpected generated source.");
    }

    @Test
    public void canGenerateGetRequestWithRequestParameters() {
        MethodSignature signature = new MethodSignature(void.class, "simple");
        ServerEndpoint endpoint = new ServerEndpoint("/test", signature);
        endpoint.addRequestParameter(new ServerRequestParameter(String.class, "param"));
        endpoint.addRequestParameter(new ServerRequestParameter(String.class, "param2"));

        LinkedList<String> source = new LinkedList<String>(sourceGenerator.generate(endpoint));

        assertEquals(source.getLast(), "ops.get(\"/test?param=\"+param+\"&param2=\"+param2+\"\",void.class,requestParameters);", "Unexpected generated source.");
    }

    @Test
    public void canGenerateGetRequestWithPathVariable() {
        MethodSignature signature = new MethodSignature(void.class, "simple");
        ServerEndpoint endpoint = new ServerEndpoint("/test/{pathVar}", signature);
        endpoint.addPathVariable(new ServerPathVariable(String.class, "pathVar"));

        LinkedList<String> source = new LinkedList<String>(sourceGenerator.generate(endpoint));

        assertEquals(source.getLast(), "ops.get(\"/test/\"+pathVar+\"\",void.class);", "Unexpected generated source.");
    }
}
