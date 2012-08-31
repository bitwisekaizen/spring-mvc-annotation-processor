import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.testng.annotations.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class AnnotationProcessorTests {

    @Test
    void canProcessRequestMappingWithBetterProcessor() throws IOException {
        File clientFile = File.createTempFile("ClientStub", ".java");
        clientFile.deleteOnExit();
        assertEquals(FileUtils.sizeOf(clientFile), 0, "Non-zero initial file size.");

        BetterProcessor processor = new BetterProcessor(clientFile);
        MethodSignature methodSignature = new MethodSignature(void.class, "methodname");
        MethodRequestMapping processorRequestMapping = new MethodRequestMapping("endpoint");
        processor.addStub(methodSignature, processorRequestMapping);

        // this should produce the client stub...
        processor.process();

        // file size should have increased
        assertTrue(FileUtils.sizeOf(clientFile) != 0, "Client file size did not increase.");

        // inverse process the java source to extract the method signatures
        InverseProcessor inverseProcessor = new InverseProcessor(clientFile);

        inverseProcessor.process();

        assertEquals(inverseProcessor.getMethodSignatures().size(), 1, "Expected a single method signature in inverse processor.");
    }

    private class InverseProcessor {

        private File file;
        private List<MethodSignature> methodSignatures = new ArrayList<MethodSignature>();

        public InverseProcessor(File file) {
            this.file = file;
        }
/*


        //Get a new instance of the standard file manager implementation

        // Get the list of java file objects, in this case we have only
// one file, TestClass.java
        Iterable<? extends JavaFileObject> compilationUnits1 =
                fileManager.getJavaFileObjectsFromFiles("TestClass.java");
    */
        public void process() {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
            JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);
            if (!task.call()) {
                throw new RuntimeException("Class failed to compile.");
            }

            ClassLoader loader = ClassLoader.getSystemClassLoader();
            try {
                Class<?> clazz = loader.loadClass(file.getName().replaceAll(".java", ""));
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    addMethodSignature(method);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        private void addMethodSignature(Method method) {
            methodSignatures.add(new MethodSignature(method.getReturnType(), method.getName()));
        }

        public List<MethodSignature> getMethodSignatures() {
            return methodSignatures;
        }
    }

    private class MethodSignature {

        public MethodSignature(Class<?> returnType, String methodName) {

        }
    }

    private class MethodRequestMapping {

        public MethodRequestMapping(String endpoint) {

        }
    }

    private class BetterProcessor {

        private File file;

        public BetterProcessor(File file) {
            this.file = file;
        }

        public void process() {
            try {
                FileUtils.writeStringToFile(file, "something interesting to make the test pass");
            } catch (IOException e) {
                // throw runtime exception for now, but have more explicit exception soon
                throw new RuntimeException("IOException occurred when writing to the file.", e);
            }
        }

        public void addStub(MethodSignature methodSignature, MethodRequestMapping requestMapping) {
            //To change body of created methods use File | Settings | File Templates.
        }
    }

    //@Test
    void canProcessMethodLevelRequestMappingInSingleRound() {
        MyProcessor processor = new MyProcessor();
        RoundEnvironment round = mock(RoundEnvironment.class);
        Element element = mock(Element.class);

        // create single request mapping annotation to be passed
        Set elements = Collections.emptySet();
        elements.add(element);
        when(round.getElementsAnnotatedWith(RequestMapping.class)).thenReturn(elements);

        // do the processing
        Set<? extends TypeElement> processorWillIgnoreThisArgument = null;
        processor.process(processorWillIgnoreThisArgument, round);

        // assert that process request mapping is called in the underlying bean
    }



    private class MyProcessor extends AbstractProcessor {
        @Override
        public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
            return false;
        }
    }
}
