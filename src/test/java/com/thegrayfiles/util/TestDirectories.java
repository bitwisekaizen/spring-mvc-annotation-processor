package com.thegrayfiles.util;

/**
 * Created by IntelliJ IDEA. User: gavin Date: 10/11/12 Time: 10:54 AM To change this template use File | Settings |
 * File Templates.
 */
public class TestDirectories {
    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/target/generated-sources";
    private static final String TEST_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/src/test/java/com/thegrayfiles/util";
    private static final String TEST_CLASSES_DIR = "/code/github/spring-mvc-annotation-processor/target/test-classes";

    public String getTestClasses() {
        return TEST_CLASSES_DIR;
    }

    public String getTestSources() {
        return TEST_SOURCES_DIR;
    }

    public String getGeneratedSources() {
        return GENERATED_SOURCES_DIR;
    }
}
