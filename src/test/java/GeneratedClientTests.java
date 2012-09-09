import com.thegrayfiles.RestTemplatePoweredClientSourceGenerator;
import com.thegrayfiles.SpringControllerAnnotationProcessor;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static org.testng.Assert.assertTrue;

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

        assertTrue(processor.isRun(), "Processor hasn't run.");
    }

    @SupportedAnnotationTypes(value= "*")
    @SupportedSourceVersion(SourceVersion.RELEASE_6)
    public class CompileTimeAnnotationProcessor extends AbstractProcessor {

        private boolean hasRun = false;

        @Override
        public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
            hasRun = true;
            return true;
        }

        public boolean isRun() {
            return hasRun;
        }
    }
}
