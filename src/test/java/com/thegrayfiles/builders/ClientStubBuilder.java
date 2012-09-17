package com.thegrayfiles.builders;

import com.thegrayfiles.server.ServerEndpoint;
import com.thegrayfiles.server.ServerPathVariable;
import com.thegrayfiles.server.ServerRequestParameter;
import com.thegrayfiles.method.MethodSignature;

import java.util.ArrayList;
import java.util.List;

public class ClientStubBuilder implements Builder<ServerEndpoint> {

    private MethodSignature methodSignature;
    private List<ServerRequestParameter> requestParameters = new ArrayList<ServerRequestParameter>();
    private List<ServerPathVariable> pathVariables = new ArrayList<ServerPathVariable>();

    private ClientStubBuilder() {
        methodSignature = new MethodSignature(void.class, "methodname");
    }

    public static ClientStubBuilder aStub() {
        return new ClientStubBuilder();
    }

    public ClientStubBuilder thatReturns(Class<?> type) {
        methodSignature = new MethodSignature(type, methodSignature.getMethodName());
        return this;
    }

    public ClientStubBuilder withRequestParam(ServerRequestParameter requestParameter) {
        this.requestParameters.add(requestParameter);
        return this;
    }

    public ClientStubBuilder withPathVariable(ServerPathVariable pathVariable) {
        this.pathVariables.add(pathVariable);
        return this;
    }

    public ServerEndpoint build() {
        ServerEndpoint stub = new ServerEndpoint(methodSignature);
        for (ServerPathVariable pathVariable : pathVariables) {
            stub.addPathVariable(pathVariable);
        }
        for (ServerRequestParameter requestParameter : requestParameters) {
            stub.addRequestParameter(requestParameter);
        }
        return stub;
    }
}
