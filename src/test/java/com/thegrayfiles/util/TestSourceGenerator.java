package com.thegrayfiles.util;

import com.thegrayfiles.server.ServerEndpoint;
import com.thegrayfiles.server.ServerPathVariable;
import com.thegrayfiles.server.ServerRequestParameter;
import com.thegrayfiles.generator.MethodImplementationSourceGenerator;

import java.util.ArrayList;
import java.util.List;

public class TestSourceGenerator implements MethodImplementationSourceGenerator {

    public List<String> generate(ServerEndpoint stub) {
        List<String> source = new ArrayList<String>();
        Class<?> returnType = stub.getMethodSignature().getReturnType();
        for (ServerRequestParameter requestParameter : stub.getRequestParameters()) {
            source.add(requestParameter.getName() + ".toString();");
        }
        for (ServerPathVariable pathVariable : stub.getPathVariables()) {
            source.add(pathVariable.getName() + ".toString();");
        }
        if (returnType.equals(void.class)) {
            return source;
        } else if (returnType.isPrimitive()) {
            source.add("return 0;");
            return source;
        }

        source.add("return null;");
        return source;
    }
}
