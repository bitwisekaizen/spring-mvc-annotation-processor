package com.thegrayfiles.generator;

import com.thegrayfiles.server.ServerEndpoint;

import java.util.Arrays;
import java.util.List;

public class MethodImplementationSourceGenerator {
    public List<String> generate(ServerEndpoint endpoint) {
        String returnType = endpoint.getMethodSignature().getReturnType().getSimpleName();
        return Arrays.asList((returnType.equals("void") ? "" : "return ") + "ops.get(\"" + endpoint.getRequestMapping() + "\", " + returnType + ".class);");
    }
}
