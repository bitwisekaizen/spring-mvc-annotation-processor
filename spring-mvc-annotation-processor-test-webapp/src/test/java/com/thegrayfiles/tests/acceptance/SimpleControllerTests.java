package com.thegrayfiles.tests.acceptance;

import com.thegrayfiles.compile.SimpleCompiler;
import com.thegrayfiles.exception.CompilationFailedException;
import com.thegrayfiles.marshallable.TestEntity;
import com.thegrayfiles.processor.SpringControllerAnnotationProcessor;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import static org.testng.Assert.assertEquals;

public class SimpleControllerTests {

    private RestTemplate restTemplate = new RestTemplate();
    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/target/generated-sources";
    private static final String TEST_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/src/main/java/com/thegrayfiles/web";
    private static final String TEST_CLASSES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/target/test-classes";


    @Test
    public void canFetchResourceFromController() throws CompilationFailedException, ClassNotFoundException,
            IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // generate client by compiling test controller with annotation processor
        SimpleCompiler annotationProcessingCompiler = new SimpleCompiler();
        String outputClientFilename = GENERATED_SOURCES_DIR + "/TestClient.java";
        String inputControllerFilename = TEST_SOURCES_DIR + "/TestController.java";
        annotationProcessingCompiler.addAnnotationProcessor(new SpringControllerAnnotationProcessor());
        annotationProcessingCompiler.addAnnotationProcessorOption(SpringControllerAnnotationProcessor.OPTION_CLIENT_OUTPUT_FILE, outputClientFilename);
        annotationProcessingCompiler.compile(new File(inputControllerFilename));

        // client source should now be generated, compile the client source
        SimpleCompiler clientCompiler = new SimpleCompiler();
        File testClassesDirectory = new File(TEST_CLASSES_DIR);
        File classFile = clientCompiler.compile(new File(outputClientFilename), testClassesDirectory);
        assertEquals(classFile.getParentFile().getAbsolutePath(), testClassesDirectory.getAbsolutePath(), "Compiled class file does not reside in specified classes directory.");

        // load the client
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Class<?> clazz = loader.loadClass(classFile.getName().replaceAll(".class", ""));
        Object client = clazz.getConstructor(String.class).newInstance("http://localhost:8080/test-webapp/ws");
        Object response = clazz.getMethod("simple").invoke(client);
        TestEntity castedResponse = (TestEntity) response;

        // make the request
        assertEquals(castedResponse.getName(), "test", "Response entity name is incorrect.");
    }
}
