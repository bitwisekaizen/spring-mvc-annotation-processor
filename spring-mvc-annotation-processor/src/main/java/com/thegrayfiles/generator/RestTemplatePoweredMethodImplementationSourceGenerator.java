package com.thegrayfiles.generator;

import com.thegrayfiles.server.ServerEndpoint;

import java.util.Arrays;
import java.util.List;

public class RestTemplatePoweredMethodImplementationSourceGenerator implements MethodImplementationSourceGenerator {
    public List<String> generate(ServerEndpoint endpoint) {
        return Arrays.asList("restTemplate.getForEntity(wsRoot+\"/test\",null);");
    }
}
