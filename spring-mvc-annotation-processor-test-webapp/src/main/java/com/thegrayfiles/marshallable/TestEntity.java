package com.thegrayfiles.marshallable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestEntity {

    private String name;

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
}
