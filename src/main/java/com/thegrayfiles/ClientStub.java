package com.thegrayfiles;

import java.util.ArrayList;
import java.util.List;

public class ClientStub {

    private MethodSignature signature;
    private MethodRequestMapping requestMapping;
    private List<RequestParameter> requestParameters = new ArrayList<RequestParameter>();
    private List<PathVariable> pathVariables = new ArrayList<PathVariable>();

    public ClientStub(MethodSignature signature, MethodRequestMapping requestMapping) {
        this.signature = signature;
        this.requestMapping = requestMapping;
    }

    public MethodSignature getMethodSignature() {
        return signature;
    }

    public void addRequestParameter(RequestParameter parameter) {
        requestParameters.add(parameter);
    }

    public List<RequestParameter> getRequestParameters() {
        return requestParameters;
    }

    public void addPathVariable(PathVariable pathVariable) {
        pathVariables.add(pathVariable);
    }

    public List<PathVariable> getPathVariables() {
        return pathVariables;
    }
}
