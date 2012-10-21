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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        String value = "pathvariablename";
        TestEntity entity = canFetchResourceFromController(String.class, value);
        assertEquals(entity.getName(), value, "Response entity name is incorrect.");
    }

    @Test
    public void canFetchResourceWithPathVariableAndRequestParameter() {
        TestEntity testEntity = canFetchResourceFromController(new Class<?>[]{String.class, String.class}, new Object[]{"pathVariableValue", "requestParameterValue"});
        assertEquals(testEntity.getRequestParameterValues().size(), 1);
        assertEquals(testEntity.getPathVariableValues().size(), 1);
        assertEquals(testEntity.getRequestParameterValues().get(0), "requestParameterValue");
        assertEquals(testEntity.getPathVariableValues().get(0), "pathVariableValue");
    }

    @Test
    public void clientGenerationPreservesControllerParameterOrdering() {
        TestEntity testEntity = canFetchResourceFromController(new Class<?>[]{String.class, String.class}, new Object[]{"requestParameterValue", "pathVariableValue"});
        assertEquals(testEntity.getRequestParameterValues().size(), 1);
        assertEquals(testEntity.getPathVariableValues().size(), 1);
        assertEquals(testEntity.getRequestParameterValues().get(0), "requestParameterValue");
        assertEquals(testEntity.getPathVariableValues().get(0), "pathVariableValue");
    }

    @Test
    public void getRequestMappingMethodShouldNotAffectAbilityToFetchResource() {
        canFetchResourceWithNoParameters();
    }

    @Test
    public void canPerformGetWithoutAnyResponse() {
        String randomString = UUID.randomUUID().toString();
        DynamicallyGeneratedClientMethodBuilder fetchRequestBuilder = new DynamicallyGeneratedClientMethodBuilder("canPerformGetWithoutAnyResponseFetchEntity");
        DynamicallyGeneratedClientMethodBuilder updateRequestBuilder = new DynamicallyGeneratedClientMethodBuilder("canPerformGetWithoutAnyResponseUpdateEntity");

        //
        updateRequestBuilder.withArgument(String.class, randomString).invoke();

        //
        TestEntity updatedTestEntity = fetchRequestBuilder.invoke(TestEntity.class);
        assertEquals(updatedTestEntity.getName(), randomString);
    }

    private class ClientArgument<T> {

        private final Class<T> type;
        private final T value;

        public ClientArgument(Class<T> type, T value) {
            this.type = type;
            this.value = value;
        }
    }

    private class DynamicallyGeneratedClientMethodBuilder {

        private String methodName;
        private List<Class<?>> argumentTypes = new ArrayList<Class<?>>();
        private List<Object> argumentValues = new ArrayList<Object>();

        public DynamicallyGeneratedClientMethodBuilder(String methodName) {
            this.methodName = methodName;
        }

        public <T> DynamicallyGeneratedClientMethodBuilder withArgument(Class<T> type, T value) {
            argumentTypes.add(type);
            argumentValues.add(value);
            return this;
        }


        public void invoke() {
            Class<?> clazz;
            try {
                clazz = generateClient();
                JavaClientHttpOperations ops = new RestTemplatePoweredHttpOperations("http://localhost:8080/test-webapp/ws");
                Object client = clazz.getConstructor(JavaClientHttpOperations.class).newInstance(ops);
                clazz.getMethod(methodName, argumentTypes.toArray(new Class[argumentTypes.size()]))
                        .invoke(client, argumentValues.toArray(new Object[argumentValues.size()]));
            } catch (CompilationFailedException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (IOException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            }
        }

        public <T> T invoke(Class<T> returnType) {
            Class<?> clazz;
            try {
                clazz = generateClient();
                JavaClientHttpOperations ops = new RestTemplatePoweredHttpOperations("http://localhost:8080/test-webapp/ws");
                Object client = clazz.getConstructor(JavaClientHttpOperations.class).newInstance(ops);
                return (T) clazz.getMethod(methodName, argumentTypes.toArray(new Class[argumentTypes.size()]))
                        .invoke(client, argumentValues.toArray(new Object[argumentValues.size()]));
            } catch (CompilationFailedException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (IOException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            } catch (InstantiationException e) {
                throw new RuntimeException("Problem invoking client method.", e);
            }
        }
    }

    private TestEntity canFetchResourceFromController(Class<?> type, Object value) {
        return canFetchResourceFromController(new Class<?>[]{type}, new Object[]{value});
    }

    private TestEntity canFetchResourceFromController() {
        Class<?> noType = null;
        Object noValue = null;
        return canFetchResourceFromController(noType, noValue);
    }

    private TestEntity canFetchResourceFromController(Class<?>[] types, Object[] values) {
        try {
            Class<?> clazz = generateClient();
            JavaClientHttpOperations ops = new RestTemplatePoweredHttpOperations("http://localhost:8080/test-webapp/ws");
            Object client = clazz.getConstructor(JavaClientHttpOperations.class).newInstance(ops);

            // load the client
            String testMethodName = determineTestMethod();
            if (types == null || types[0] == null) {
                return (TestEntity) clazz.getMethod(testMethodName).invoke(client);
            }
            return (TestEntity) clazz.getMethod(testMethodName, types).invoke(client, values);
        } catch (Exception e) {
            fail("Unexpected exception thrown while fetching resource from controller.", e);
        }

        return null;
    }

    private Class<?> generateClient() throws CompilationFailedException, IOException, ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
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

        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Class<?> clazz = loader.loadClass(classFile.getName().replaceAll(".class", ""));
        return clazz;
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
