package com.thegrayfiles.method;

import java.util.ArrayList;
import java.util.List;

public class MethodSignature {

    private Class<?> returnType;
    private String methodName;
    private List<MethodParameter> parameters = new ArrayList<MethodParameter>();

    public MethodSignature(Class<?> returnType, String methodName) {
        this.returnType = returnType;
        this.methodName = methodName;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<MethodParameter> getParameters() {
        return parameters;
    }

    public void addParameter(MethodParameter parameter) {
        parameters.add(parameter);
    }
}