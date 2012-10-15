package com.thegrayfiles.generator;

import com.thegrayfiles.server.ServerEndpoint;
import com.thegrayfiles.server.ServerPathVariable;
import com.thegrayfiles.server.ServerRequestParameter;

import java.util.ArrayList;
import java.util.List;

public class MethodImplementationSourceGenerator {
    public List<String> generate(ServerEndpoint endpoint) {
        List<String> source = new ArrayList<String>();
        String opsInvocation;
        String requestParameterString = "";
        String requestMapping = endpoint.getRequestMapping();
        String returnType = endpoint.getMethodSignature().getReturnType().getSimpleName();

        // substitute path variables
        List<ServerPathVariable> pathVariables = endpoint.getPathVariables();
        if (pathVariables.size() > 0) {
            for (ServerPathVariable pathVariable : pathVariables) {
                String pathVariableName = pathVariable.getName();
                requestMapping = requestMapping.replaceAll("(.*?)[{]" + pathVariableName + "[}](.*)", "$1\"+" + pathVariableName + "+\"");
            }
        }

        // add request parameters
        List<ServerRequestParameter> requestParameters = endpoint.getRequestParameters();
        if (requestParameters.size() > 0) {
            requestParameterString = "?";
            source.add("Map<String, Object> requestParameters = new HashMap<String, Object>();");
            for (ServerRequestParameter requestParameter : requestParameters) {
                String requestParameterName = requestParameter.getName();
                source.add("requestParameters.put(\"" + requestParameterName + "\"," + requestParameterName + ");");
                requestParameterString += requestParameterName + "=\"+" + requestParameterName + "+\"&";
            }
            requestParameterString = requestParameterString.replaceAll("(.*)&$", "$1");
        }

        // generate the string
        opsInvocation = (returnType.equals("void") ? "" : "return ");
        opsInvocation += "ops.get(\"" + requestMapping;
        opsInvocation += requestParameterString;
        opsInvocation += "\"";
        opsInvocation += "," + returnType + ".class";
        opsInvocation += requestParameters.size() > 0 ? ",requestParameters" : "";
        opsInvocation += ");";
        source.add(opsInvocation);
        return source;
    }
}
