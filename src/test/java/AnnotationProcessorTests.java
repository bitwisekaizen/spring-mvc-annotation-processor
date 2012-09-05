import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class AnnotationProcessorTests {

    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/target/generated-sources";
    private static final String TEST_CLASSES_DIR = "/code/github/spring-mvc-annotation-processor/target/test-classes";

    @Test
    public void canProcessRequestMappingWithBetterProcessor() throws IOException {
        File generatedSources = new File(GENERATED_SOURCES_DIR);
        generatedSources.mkdirs();

        File testClasses = new File(TEST_CLASSES_DIR);
        testClasses.mkdirs();

        File clientFile = File.createTempFile("ClientStub", ".java", generatedSources);
        clientFile.deleteOnExit();

        assertEquals(FileUtils.sizeOf(clientFile), 0, "Non-zero initial file size.");

        BetterProcessor processor = new BetterProcessor(clientFile);
        MethodSignature methodSignature = new MethodSignature(void.class, "methodname");
        MethodRequestMapping processorRequestMapping = new MethodRequestMapping("endpoint");
        processor.addStub(new ClientStub(methodSignature, processorRequestMapping));

        // this should produce the client stub...
        processor.process();

        // file size should have increased
        assertTrue(FileUtils.sizeOf(clientFile) != 0, "Client file size did not increase.");

        // inverse process the java source to extract the method signatures
        InverseProcessor inverseProcessor = new InverseProcessor(clientFile, testClasses);

        inverseProcessor.process();

        assertEquals(inverseProcessor.getMethodSignatures().size(), InverseProcessor.class.getSuperclass().getMethods().length + 1, "Expected a single method signature in inverse processor.");
    }



    private class MethodRequestMapping {

        public MethodRequestMapping(String endpoint) {

        }
    }

    private class ClientStub {

        private MethodSignature signature;
        private MethodRequestMapping requestMapping;

        public ClientStub(MethodSignature signature, MethodRequestMapping requestMapping) {
            this.signature = signature;
            this.requestMapping = requestMapping;
        }

        @Override
        public String toString() {
            return "public " + signature.getReturnType().toString() + " " + signature.getMethodName() + "() { }";
        }
    }

    private class BetterProcessor {

        private File file;
        private List<ClientStub> stubs = new ArrayList<ClientStub>();

        public BetterProcessor(File file) {
            this.file = file;
        }

        public void process() {
            try {
                StringBuilder fileContents = new StringBuilder();
                fileContents.append("public class " + file.getName().replaceFirst("(.$?)\\.java", "$1") + " {");

                for (ClientStub stub : stubs) {
                    fileContents.append(stub);
                }

                fileContents.append("}");
                FileUtils.writeStringToFile(file, fileContents.toString());
            } catch (IOException e) {
                // throw runtime exception for now, but have more explicit exception soon
                throw new RuntimeException("IOException occurred when writing to the file.", e);
            }
        }

        public void addStub(ClientStub stub) {
            stubs.add(stub);
        }
    }
}
