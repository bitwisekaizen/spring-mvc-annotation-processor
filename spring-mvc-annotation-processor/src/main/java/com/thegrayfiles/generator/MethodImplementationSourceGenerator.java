package com.thegrayfiles.generator;

import com.thegrayfiles.server.ServerEndpoint;
import com.thegrayfiles.server.ServerRequestParameter;

import java.util.ArrayList;
import java.util.List;

public class MethodImplementationSourceGenerator {
    public List<String> generate(ServerEndpoint endpoint) {
        List<String> source = new ArrayList<String>();
        String opsInvocation;
        String returnType = endpoint.getMethodSignature().getReturnType().getSimpleName();
        List<ServerRequestParameter> requestParameters = endpoint.getRequestParameters();
        if (requestParameters.size() > 0) {
            source.add("Map<String, Object> requestParameters = new HashMap<String, Object>();");
            for (ServerRequestParameter requestParameter : requestParameters) {
                String requestParameterName = requestParameter.getName();
                source.add("requestParameters.put(\"" + requestParameterName + "\"," + requestParameterName + ");");
            }
        }

        // generate the string
        opsInvocation = (returnType.equals("void") ? "" : "return ");
        opsInvocation += "ops.get(\"" + endpoint.getRequestMapping() + "\"";
        opsInvocation += "," + returnType + ".class";
        opsInvocation += requestParameters.size() > 0 ? ",requestParameters" : "";
        opsInvocation += ");";
        source.add(opsInvocation);
        return source;
    }
}
