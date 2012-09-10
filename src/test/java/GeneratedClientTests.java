import com.thegrayfiles.ClientStub;
import com.thegrayfiles.MethodSignature;
import com.thegrayfiles.RestTemplatePoweredClientSourceGenerator;
import com.thegrayfiles.SpringControllerAnnotationProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.testng.annotations.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
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
import java.util.TreeSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

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

        assertEquals(processor.getStubs().size(), 1, "Expected exactly one request mapping.");
    }

    @Test
    public void canConvertTypeElementToClientStub() throws ClassNotFoundException {
        TypeElementToClientStubConverter typeElementAdapter = new TypeElementToClientStubConverter();

        ProcessingEnvironment processingEnvironment = mock(ProcessingEnvironment.class);
        Types typeUtils = mock(Types.class);
        ExecutableElement executableMethod = mock(ExecutableElement.class);
        TypeMirror typeMirror = mock(TypeMirror.class);
        Name methodName = mock(Name.class);
        Element returnType = mock(Element.class);
        RoundEnvironment roundEnvironment = mock(RoundEnvironment.class);

        String stringMethodName = "someCrazyMethodName";

        when(processingEnvironment.getTypeUtils()).thenReturn(typeUtils);
        when(typeUtils.asElement(typeMirror)).thenReturn(returnType);
        when(returnType.toString()).thenReturn("java.lang.Integer");
        when(executableMethod.getSimpleName()).thenReturn(methodName);
        when(roundEnvironment.getElementsAnnotatedWith(RequestMapping.class)).thenReturn(new TreeSet(Arrays.asList(executableMethod)));

        when(methodName.toString()).thenReturn(stringMethodName);
        when(executableMethod.getReturnType()).thenReturn(typeMirror);
        when(typeMirror.toString()).thenReturn("void");

        List<ClientStub> stubs = typeElementAdapter.convert(processingEnvironment, roundEnvironment);
        assertEquals(stubs.get(0).getMethodSignature().getMethodName(), stringMethodName);
        assertEquals(stubs.get(0).getMethodSignature().getReturnType(), Integer.class);
    }

    public class TypeElementToClientStubConverter {

        public List<ClientStub> convert(ProcessingEnvironment processingEnv, RoundEnvironment roundEnvironment) {
            List<ClientStub> stubs = new ArrayList<ClientStub>();
            Set<? extends Element> methods = roundEnvironment.getElementsAnnotatedWith(RequestMapping.class);
            for (Element method : methods) {
                try {
                    String methodName = method.getSimpleName().toString();
                    ExecutableElement executableMethod = (ExecutableElement) method;
                    Element elementReturnType = processingEnv.getTypeUtils().asElement(executableMethod.getReturnType());
                    Class<?> returnType = Class.forName(elementReturnType.toString());

                    MethodSignature methodSignature = new MethodSignature(returnType, methodName);
                    stubs.add(new ClientStub(methodSignature, null));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class not found.");
                }
            }

            return stubs;
        }
    }

    @SupportedAnnotationTypes(value= "org.springframework.web.bind.annotation.RequestMapping")
    @SupportedSourceVersion(SourceVersion.RELEASE_6)
    public class CompileTimeAnnotationProcessor extends AbstractProcessor {

        private List<ClientStub> stubs = new ArrayList<ClientStub>();

        @Override
        public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
            TypeElementToClientStubConverter converter = new TypeElementToClientStubConverter();
            stubs.addAll(converter.convert(this.processingEnv, roundEnvironment));
            return true;
        }

        public List<ClientStub> getStubs() {
            return stubs;
        }
    }
}
