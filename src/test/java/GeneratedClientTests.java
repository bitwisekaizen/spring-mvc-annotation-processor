import com.thegrayfiles.ClientStub;
import com.thegrayfiles.MethodSignature;
import com.thegrayfiles.RestTemplatePoweredClientSourceGenerator;
import com.thegrayfiles.SpringControllerAnnotationProcessor;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@Test
public class GeneratedClientTests {
    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/target/generated-sources";
    private static final String TEST_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/src/test/java";

    public void canGenerateRestTemplatePoweredClient() throws IOException {
        File generatedSourcesDirectory = new File(GENERATED_SOURCES_DIR);
        generatedSourcesDirectory.mkdirs();

        RestTemplatePoweredClientSourceGenerator generator = new RestTemplatePoweredClientSourceGenerator();

        File sourceFile = new ClassPathResource("TestController.java").getFile();

        File generatedSource = new File(GENERATED_SOURCES_DIR + "/" + generator.getClass().getSimpleName() + ".java");
        generatedSource.deleteOnExit();

        // read the source and generate the stubs
        SpringControllerAnnotationProcessor annotationProcessor = new SpringControllerAnnotationProcessor(generator, generatedSource);
        annotationProcessor.process();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);

        if (!task.call()) {
            throw new RuntimeException("Class failed to compile.");
        }


    }

    @Test
    public void canRunSimpleAnnotationProcessor() throws IOException {
        CompileTimeAnnotationProcessor processor = new CompileTimeAnnotationProcessor();
        File sourceFile = new File(TEST_SOURCES_DIR + "/TestController.java");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);
        task.setProcessors(Arrays.asList(processor));

        if (!task.call()) {
            throw new RuntimeException("Class failed to compile.");
        }

        assertEquals(processor.getTypeElements().size(), 1, "Expected exactly one request mapping.");
    }

    @Test
    public void canConvertTypeElementToClientStub() {
        TypeElementAdapter typeElementAdapter = new TypeElementAdapter();
        TypeElement typeElement = Mockito.mock(TypeElement.class);
        Name methodName = Mockito.mock(Name.class);

        // mock out the response to
        when(typeElement.getSimpleName()).thenReturn(methodName);
        when(methodName.toString()).thenReturn("name");

        ClientStub stub = typeElementAdapter.convert(typeElement);

        assertEquals(stub.getMethodSignature().getMethodName(), "name");
    }

    public class TypeElementAdapter {

        public ClientStub convert(TypeElement typeElement) {
            String methodName = typeElement.getSimpleName().toString();
            MethodSignature methodSignature = new MethodSignature(void.class, methodName);
            ClientStub stub = new ClientStub(methodSignature, null);
            return stub;
        }
    }

    @SupportedAnnotationTypes(value= "org.springframework.web.bind.annotation.RequestMapping")
    @SupportedSourceVersion(SourceVersion.RELEASE_6)
    public class CompileTimeAnnotationProcessor extends AbstractProcessor {

        private List<TypeElement> typeElements = new ArrayList<TypeElement>();

        @Override
        public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
            for (TypeElement typeElement : typeElements) {
                this.typeElements.add(typeElement);
            }
            return true;
        }

        public List<TypeElement> getTypeElements() {
            return typeElements;
        }
    }
}
