package com.thegrayfiles.builders;

import com.thegrayfiles.method.MethodParameter;
import com.thegrayfiles.method.MethodSignature;
import com.thegrayfiles.server.ServerEndpoint;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

public class ClientStubBuilder implements Builder<ServerEndpoint> {

    private MethodSignature methodSignature;
    private List<MethodParameter> requestParameters = new ArrayList<MethodParameter>();
    private List<MethodParameter> pathVariables = new ArrayList<MethodParameter>();

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

    public ClientStubBuilder withRequestParam(MethodParameter requestParameter) {
        this.requestParameters.add(requestParameter);
        return this;
    }

    public ClientStubBuilder withPathVariable(MethodParameter pathVariable) {
        this.pathVariables.add(pathVariable);
        return this;
    }

    public ServerEndpoint build() {
        ServerEndpoint stub = new ServerEndpoint("/mapping", RequestMethod.GET, methodSignature);
        for (MethodParameter pathVariable : pathVariables) {
            stub.addPathVariable(pathVariable);
        }
        for (MethodParameter requestParameter : requestParameters) {
            stub.addRequestParameter(requestParameter);
        }
        return stub;
    }
}
