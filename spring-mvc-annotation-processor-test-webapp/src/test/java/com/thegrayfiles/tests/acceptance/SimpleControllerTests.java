package com.thegrayfiles.tests.acceptance;

import com.thegrayfiles.compile.SimpleCompiler;
import com.thegrayfiles.generator.JavaClientHttpOperations;
import com.thegrayfiles.generator.RestTemplatePoweredHttpOperations;
import com.thegrayfiles.marshallable.TestEntity;
import com.thegrayfiles.processor.SpringControllerAnnotationProcessor;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class SimpleControllerTests {

    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/target/generated-sources";
    private static final String TEST_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/src/main/java/com/thegrayfiles/web";
    private static final String TEST_CLASSES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/target/test-classes";

    @Test
    public void canFetchResourceWithNoParameters() {
        TestEntity entity = canFetchResourceFromController();
        assertEquals(entity.getName(), "test", "Response entity name is incorrect.");
    }

    @Test
    public void canFetchResourceUsingParameter() {
        String entityName = "somecrazyname";
        TestEntity entity = canFetchResourceFromController(String.class, entityName);
        assertEquals(entity.getName(), entityName, "Response entity name is incorrect.");
    }

    @Test
    public void canFetchResourceUsingPathVariable() {
        String entityName = "pathvariablename";
        TestEntity entity = canFetchResourceFromController(String.class, entityName);
        assertEquals(entity.getName(), entityName, "Response entity name is incorrect.");
    }

    private TestEntity canFetchResourceFromController() {
        Class<?> noType = null;
        Object noValue = null;
        return canFetchResourceFromController(noType, noValue);
    }

    private TestEntity canFetchResourceFromController(Class<?> type, Object value) {
        try {
            // generate client by compiling test controller with annotation processor
            SimpleCompiler annotationProcessingCompiler = new SimpleCompiler();
            File generatedSourcesDirectory = new File(GENERATED_SOURCES_DIR);
            generatedSourcesDirectory.mkdirs();
            File outputClientFile = File.createTempFile("TestClient", ".java", generatedSourcesDirectory);
            outputClientFile.deleteOnExit();
            String inputControllerFilename = TEST_SOURCES_DIR + "/SimpleController.java";
            annotationProcessingCompiler.addAnnotationProcessor(new SpringControllerAnnotationProcessor());
            annotationProcessingCompiler.addAnnotationProcessorOption(SpringControllerAnnotationProcessor.OPTION_CLIENT_OUTPUT_FILE, outputClientFile.getAbsolutePath());
            annotationProcessingCompiler.compile(new File(inputControllerFilename));

            // client source should now be generated, compile the client source
            SimpleCompiler clientCompiler = new SimpleCompiler();
            File testClassesDirectory = new File(TEST_CLASSES_DIR);
            File classFile = clientCompiler.compile(outputClientFile, testClassesDirectory);
            classFile.deleteOnExit();
            assertEquals(classFile.getParentFile().getAbsolutePath(), testClassesDirectory.getAbsolutePath(), "Compiled class file does not reside in specified classes directory.");

            // load the client
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            Class<?> clazz = loader.loadClass(classFile.getName().replaceAll(".class", ""));
            JavaClientHttpOperations ops = new RestTemplatePoweredHttpOperations("http://localhost:8080/test-webapp/ws");
            Object client = clazz.getConstructor(JavaClientHttpOperations.class).newInstance(ops);
            String testMethodName = determineTestMethod();
            if (type == null) {
                return (TestEntity) clazz.getMethod(testMethodName).invoke(client);
            }
            return (TestEntity) clazz.getMethod(testMethodName, type).invoke(client, value);
        } catch (Exception e) {
            fail("Unexpected exception thrown while fetching resource from controller.", e);
        }

        return null;
    }

    private String determineTestMethod() throws ClassNotFoundException {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            // test methods will never have arguments in this context
            try {
                Test testAnnotation = Class.forName(className).getMethod(methodName).getAnnotation(Test.class);
                if (testAnnotation != null) {
                    return methodName;
                }
            } catch (NoSuchMethodException e) {
                // ignore
            }
        }

        return null;
    }
}
