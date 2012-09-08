import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class AnnotationProcessorTests {

    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/target/generated-sources";
    private static final String TEST_CLASSES_DIR = "/code/github/spring-mvc-annotation-processor/target/test-classes";
    private File testClassesDirectory;
    private File generatedSource;

    @BeforeMethod
    public void setup() throws IOException {
        File generatedSourcesDirectory = new File(GENERATED_SOURCES_DIR);
        generatedSourcesDirectory.mkdirs();

        testClassesDirectory = new File(TEST_CLASSES_DIR);
        testClassesDirectory.mkdirs();

        generatedSource = File.createTempFile("ClientStub", ".java", generatedSourcesDirectory);
        generatedSource.deleteOnExit();
    }

    @Test
    public void canProcessRequestMappingWithVoidReturnType() throws IOException {
        canProcessRequestMapping(void.class);
    }

    @Test
    public void canProcessRequestMappingWithNonPrimitiveReturnType() throws IOException {
        canProcessRequestMapping(Integer.class);
    }

    @Test
    public void canProcessRequestMappingWithPrimitiveReturnType() throws IOException {
        canProcessRequestMapping(int.class);
    }

    @Test
    public void canProcessRequestMappingWithMultipleRequestParameters() throws IOException {
        canProcessRequestMapping(void.class, asList(new RequestParameter(String.class, "param"), new RequestParameter(Integer.class, "anotherparam"), new RequestParameter(Integer.class, "onemoreofthesametype")));
    }

    @Test
    public void canProcessRequestMappingWithSinglePathVariable() throws IOException {
        canProcessRequestMapping(void.class, null, asList(new PathVariable(String.class, "somekindofpathvariable")));
    }

    private void canProcessRequestMapping(Class<?> returnType, List<RequestParameter> requestParameters) throws IOException {
        canProcessRequestMapping(returnType, requestParameters, null);
    }

    private void canProcessRequestMapping(Class<?> returnType) throws IOException {
        canProcessRequestMapping(returnType, null, null);
    }

    private void canProcessRequestMapping(Class<?> returnType, List<RequestParameter> requestParameters, List<PathVariable> pathVariables) throws IOException {
        SourceGenerator clientGenerator = new TestSourceGenerator();

        SpringControllerAnnotationProcessor processor = new SpringControllerAnnotationProcessor(clientGenerator, generatedSource);
        MethodSignature methodSignature = new MethodSignature(returnType, "methodname");
        MethodRequestMapping processorRequestMapping = new MethodRequestMapping("endpoint");
        ClientStub stub = new ClientStub(methodSignature, processorRequestMapping);

        // add request parameters
        if (requestParameters != null) {
            for (RequestParameter requestParameter : requestParameters) {
                stub.addRequestParameter(requestParameter);
            }
        }

        // add path variables
        if (pathVariables != null) {
            for (PathVariable pathVariable : pathVariables) {
                stub.addPathVariable(pathVariable);
            }
        }

        processor.addStub(stub);

        // this should produce the client stub...
        processor.process();

        // file size should have increased
        assertTrue(FileUtils.sizeOf(generatedSource) != 0, "Client file size did not increase.");

        // inverse process the java source to extract the method signatures
        InverseSpringControllerAnnotationProcessor inverseProcessor = new InverseSpringControllerAnnotationProcessor(generatedSource, testClassesDirectory);

        inverseProcessor.process();

        List<MethodSignature> inverseProcessorMethodSignatures = inverseProcessor.getMethodSignatures();
        assertEquals(inverseProcessorMethodSignatures.size(), 1, "Expected a single method signature in inverse processor.");
        MethodSignature inverseProcessorMethodSignature = inverseProcessorMethodSignatures.get(0);
        assertMethodSignaturesAreSame(inverseProcessorMethodSignature, methodSignature);
        assertEquals(inverseProcessorMethodSignature.getParameters().size(), stub.getRequestParameters().size() + stub.getPathVariables().size(), "Number of method parameters differ from number of request parameters.");
        if (requestParameters != null) {
            Map<Class<?>, Integer> stubTypeCount = getStubTypeCount(stub);
            Map<Class<?>, Integer> methodParamTypeCount = getMethodParameterTypeCount(inverseProcessorMethodSignature);
            Set<Class<?>> stubParamTypes = stubTypeCount.keySet();
            assertEquals(stubParamTypes, methodParamTypeCount.keySet());
            for (Class<?> requestParamType : stubParamTypes) {
                assertTrue(methodParamTypeCount.containsKey(requestParamType));
                assertEquals(stubTypeCount.get(requestParamType), methodParamTypeCount.get(requestParamType));
            }
        }

        if (pathVariables != null) {

        }
    }

    private Map<Class<?>, Integer> getMethodParameterTypeCount(MethodSignature methodSignature) {
        Map<Class<?>, Integer> typeCount = new HashMap<Class<?>, Integer>();
        for (MethodParameter parameter : methodSignature.getParameters()) {
            Class<?> type = parameter.getType();
            incrementTypeCount(typeCount, type);
        }
        return typeCount;
    }

    private void incrementTypeCount(Map<Class<?>, Integer> typeCount, Class<?> type) {
        if (typeCount.containsKey(type)) {
            typeCount.put(type, typeCount.get(type) + 1);
        } else {
            typeCount.put(type, 1);
        }
    }

    private Map<Class<?>, Integer> getStubTypeCount(ClientStub stub) {
        Map<Class<?>, Integer> typeCount = new HashMap<Class<?>, Integer>();
        for (RequestParameter parameter : stub.getRequestParameters()) {
            incrementTypeCount(typeCount, parameter.getType());
        }
        for (PathVariable pathVariable : stub.getPathVariables()) {
            incrementTypeCount(typeCount, pathVariable.getType());
        }
        return typeCount;
    }

    private void assertMethodSignaturesAreSame(MethodSignature actual, MethodSignature expected) {
        assertEquals(actual.getReturnType(), expected.getReturnType(), "Method signature return types differ.");
        assertEquals(actual.getMethodName(), expected.getMethodName(), "Method names differ.");
    }
}
