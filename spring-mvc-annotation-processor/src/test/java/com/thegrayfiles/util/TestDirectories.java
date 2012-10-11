package com.thegrayfiles.util;

public class TestDirectories {
    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor/target/generated-sources";
    private static final String TEST_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor/src/test/java/com/thegrayfiles/util";
    private static final String TEST_CLASSES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor/target/test-classes";

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
