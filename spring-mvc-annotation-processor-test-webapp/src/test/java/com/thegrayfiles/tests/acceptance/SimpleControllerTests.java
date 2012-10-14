package com.thegrayfiles.tests.acceptance;

import com.thegrayfiles.compile.SimpleCompiler;
import com.thegrayfiles.exception.CompilationFailedException;
import com.thegrayfiles.generator.JavaClientHttpOperations;
import com.thegrayfiles.generator.RestTemplatePoweredHttpOperations;
import com.thegrayfiles.marshallable.TestEntity;
import com.thegrayfiles.processor.SpringControllerAnnotationProcessor;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class SimpleControllerTests {

    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/target/generated-sources";
    private static final String TEST_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/src/main/java/com/thegrayfiles/web";
    private static final String TEST_CLASSES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/target/test-classes";

    @Test
    public void canFetchResourceFromController() throws CompilationFailedException, ClassNotFoundException,
            IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, IOException {
        // generate client by compiling test controller with annotation processor
        SimpleCompiler annotationProcessingCompiler = new SimpleCompiler();
        File outputClientFile = File.createTempFile("TestClient", ".java", new File(GENERATED_SOURCES_DIR));
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
        Object response = clazz.getMethod("get").invoke(client);
        TestEntity castedResponse = (TestEntity) response;

        // make the request
        assertNotNull(castedResponse, "Invocation of simple client method resulted in null response.");
        assertEquals(castedResponse.getName(), "test", "Response entity name is incorrect.");
    }

    @Test
    public void canFetchResourceUsingParameter() throws CompilationFailedException, IOException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException {
        // generate client by compiling test controller with annotation processor
        SimpleCompiler annotationProcessingCompiler = new SimpleCompiler();
        String outputClientFilename = GENERATED_SOURCES_DIR + "/TestClient.java";
        String inputControllerFilename = TEST_SOURCES_DIR + "/SimpleController.java";
        annotationProcessingCompiler.addAnnotationProcessor(new SpringControllerAnnotationProcessor());
        annotationProcessingCompiler.addAnnotationProcessorOption(SpringControllerAnnotationProcessor.OPTION_CLIENT_OUTPUT_FILE, outputClientFilename);
        annotationProcessingCompiler.compile(new File(inputControllerFilename));

        // client source should now be generated, compile the client source
        SimpleCompiler clientCompiler = new SimpleCompiler();
        File testClassesDirectory = new File(TEST_CLASSES_DIR);
        File classFile = clientCompiler.compile(new File(outputClientFilename), testClassesDirectory);
        classFile.deleteOnExit();
        assertEquals(classFile.getParentFile().getAbsolutePath(), testClassesDirectory.getAbsolutePath(), "Compiled class file does not reside in specified classes directory.");

        // load the client
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Class<?> clazz = loader.loadClass(classFile.getName().replaceAll(".class", ""));
        JavaClientHttpOperations ops = new RestTemplatePoweredHttpOperations("http://localhost:8080/test-webapp/ws");
        Object client = clazz.getConstructor(JavaClientHttpOperations.class).newInstance(ops);
        // this is the only thing that changed
        String entityName = "somecrazyname";
        Object response = clazz.getMethod("canFetchResourceUsingParameter", String.class).invoke(client, "somecrazyname");
        TestEntity castedResponse = (TestEntity) response;

        // make the request
        assertNotNull(castedResponse, "Invocation of simple client method resulted in null response.");
        assertEquals(castedResponse.getName(), entityName, "Response entity name is incorrect.");
    }
}
