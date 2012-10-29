package com.thegrayfiles.server;

import com.thegrayfiles.method.MethodParameter;
import com.thegrayfiles.method.MethodSignature;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

public class ServerEndpoint {

    private MethodSignature signature;
    private List<MethodParameter> requestParameters = new ArrayList<MethodParameter>();
    private List<MethodParameter> pathVariables = new ArrayList<MethodParameter>();
    private String requestMapping;
    private RequestMethod requestMethod;
    private MethodParameter requestBody;

    public ServerEndpoint(String requestMapping, RequestMethod requestMethod, MethodSignature signature) {
        this.signature = signature;
        this.requestMethod = requestMethod;
        this.requestMapping = requestMapping;
    }

    public String getRequestMapping() {
        return requestMapping;
    }

    public MethodSignature getMethodSignature() {
        return signature;
    }

    public void addRequestParameter(MethodParameter parameter) {
        requestParameters.add(parameter);
        signature.addParameter(parameter);
    }

    public List<MethodParameter> getRequestParameters() {
        return requestParameters;
    }

    public void addPathVariable(MethodParameter pathVariable) {
        pathVariables.add(pathVariable);
        signature.addParameter(pathVariable);
    }

    public List<MethodParameter> getPathVariables() {
        return pathVariables;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public MethodParameter getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(MethodParameter requestBody) {
        this.requestBody = requestBody;
        signature.addParameter(requestBody);
    }
}
