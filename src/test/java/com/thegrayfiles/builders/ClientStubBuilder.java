package com.thegrayfiles.builders;

import com.thegrayfiles.ClientStub;
import com.thegrayfiles.MethodRequestMapping;
import com.thegrayfiles.MethodSignature;
import com.thegrayfiles.PathVariable;
import com.thegrayfiles.RequestParameter;

import java.util.ArrayList;
import java.util.List;

public class ClientStubBuilder implements Builder<ClientStub> {

    private MethodSignature methodSignature;
    private MethodRequestMapping methodRequestMapping;
    private List<RequestParameter> requestParameters = new ArrayList<RequestParameter>();
    private List<PathVariable> pathVariables = new ArrayList<PathVariable>();

    private ClientStubBuilder() {
        methodSignature = new MethodSignature(void.class, "methodname");
        methodRequestMapping = new MethodRequestMapping("endpoint");
    }

    public static ClientStubBuilder aStub() {
        return new ClientStubBuilder();
    }

    public ClientStubBuilder thatReturns(Class<?> type) {
        methodSignature = new MethodSignature(type, methodSignature.getMethodName());
        return this;
    }

    public ClientStubBuilder withRequestParam(RequestParameter requestParameter) {
        this.requestParameters.add(requestParameter);
        return this;
    }

    public ClientStubBuilder withPathVariable(PathVariable pathVariable) {
        this.pathVariables.add(pathVariable);
        return this;
    }

    public ClientStub build() {
        ClientStub stub = new ClientStub(methodSignature, methodRequestMapping);
        for (PathVariable pathVariable : pathVariables) {
            stub.addPathVariable(pathVariable);
        }
        for (RequestParameter requestParameter : requestParameters) {
            stub.addRequestParameter(requestParameter);
        }
        return stub;
    }
}
