package com.thegrayfiles.marshallable;

import com.thegrayfiles.server.ServerPathVariable;
import com.thegrayfiles.server.ServerRequestParameter;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestEntity {

    private String name;
    private ServerPathVariable pathVariable;
    private ServerRequestParameter requestParameter;

    protected TestEntity() {}

    public TestEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServerPathVariable getPathVariable() {
        return pathVariable;
    }

    public void setPathVariable(ServerPathVariable pathVariable) {
        this.pathVariable = pathVariable;
    }


    public ServerRequestParameter getRequestParameter() {
        return requestParameter;
    }

    public void setRequestParameter(ServerRequestParameter requestParameter) {
        this.requestParameter = requestParameter;
    }
}
