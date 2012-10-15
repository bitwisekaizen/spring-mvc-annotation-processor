package com.thegrayfiles.generator;

import com.thegrayfiles.method.MethodParameter;
import com.thegrayfiles.server.ServerEndpoint;

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
        List<MethodParameter> pathVariables = endpoint.getPathVariables();
        if (pathVariables.size() > 0) {
            for (MethodParameter pathVariable : pathVariables) {
                String pathVariableName = pathVariable.getName();
                requestMapping = requestMapping.replaceAll("(.*?)[{]" + pathVariableName + "[}](.*)", "$1\"+" + pathVariableName + "+\"");
            }
        }

        // add request parameters
        List<MethodParameter> requestParameters = endpoint.getRequestParameters();
        if (requestParameters.size() > 0) {
            requestParameterString = "?";
            for (MethodParameter requestParameter : requestParameters) {
                String requestParameterName = requestParameter.getName();
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
        opsInvocation += ");";
        source.add(opsInvocation);
        return source;
    }
}
