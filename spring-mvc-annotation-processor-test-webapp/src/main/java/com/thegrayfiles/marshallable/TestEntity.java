package com.thegrayfiles.marshallable;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class TestEntity {

    private String name;
    private List<String> requestParameterValues = new ArrayList<String>();
    private List<String> pathVariableValues = new ArrayList<String>();

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

    public List<String> getRequestParameterValues() {
        return requestParameterValues;
    }

    public void setRequestParameterValues(List<String> requestParameterValues) {
        this.requestParameterValues = requestParameterValues;
    }

    public List<String> getPathVariableValues() {
        return pathVariableValues;
    }

    public void setPathVariableValues(List<String> pathVariableValues) {
        this.pathVariableValues = pathVariableValues;
    }

    public void addPathVariableValue(String value) {
        pathVariableValues.add(value);
    }

    public void addRequestParameterValue(String value) {
        requestParameterValues.add(value);
    }
}
