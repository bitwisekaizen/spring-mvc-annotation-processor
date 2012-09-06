import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
    public void canProcessSimpleRequestWithVoidReturnType() throws IOException {
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

    private void canProcessRequestMapping(Class<?> returnType) throws IOException {
        SourceGenerator clientGenerator = new TestSourceGenerator();

        SpringControllerAnnotationProcessor processor = new SpringControllerAnnotationProcessor(clientGenerator, generatedSource);
        MethodSignature methodSignature = new MethodSignature(returnType, "methodname");
        MethodRequestMapping processorRequestMapping = new MethodRequestMapping("endpoint");
        processor.addStub(new ClientStub(methodSignature, processorRequestMapping));

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
    }

    private void assertMethodSignaturesAreSame(MethodSignature actual, MethodSignature expected) {
        assertEquals(actual.getReturnType(), expected.getReturnType(), "Method signature return types differ.");
        assertEquals(actual.getMethodName(), expected.getMethodName(), "Method names differ.");
    }
}
