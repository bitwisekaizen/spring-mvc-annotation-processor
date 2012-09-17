package com.thegrayfiles.server;

import com.thegrayfiles.method.MethodSignature;

import java.util.ArrayList;
import java.util.List;

public class ServerEndpoint {

    private MethodSignature signature;
    private List<ServerRequestParameter> requestParameters = new ArrayList<ServerRequestParameter>();
    private List<ServerPathVariable> pathVariables = new ArrayList<ServerPathVariable>();

    public ServerEndpoint(MethodSignature signature) {
        this.signature = signature;
    }

    public MethodSignature getMethodSignature() {
        return signature;
    }

    public void addRequestParameter(ServerRequestParameter parameter) {
        requestParameters.add(parameter);
    }

    public List<ServerRequestParameter> getRequestParameters() {
        return requestParameters;
    }

    public void addPathVariable(ServerPathVariable pathVariable) {
        pathVariables.add(pathVariable);
    }

    public List<ServerPathVariable> getPathVariables() {
        return pathVariables;
    }
}
