package com.thegrayfiles.tests;

import com.thegrayfiles.generator.RestTemplatePoweredClientSourceGenerator;
import com.thegrayfiles.generator.SpringControllerClientSourceGenerator;
import com.thegrayfiles.processor.SpringControllerAnnotationProcessor;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;

public class GeneratedClientTests {
    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/target/generated-sources";
    private static final String TEST_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/src/test/java/com/thegrayfiles/util";

    public void canGenerateRestTemplatePoweredClient() throws IOException {
        File generatedSourcesDirectory = new File(GENERATED_SOURCES_DIR);
        generatedSourcesDirectory.mkdirs();

        RestTemplatePoweredClientSourceGenerator generator = new RestTemplatePoweredClientSourceGenerator();

        File sourceFile = new ClassPathResource("TestController.java").getFile();

        File generatedSource = new File(GENERATED_SOURCES_DIR + "/" + generator.getClass().getSimpleName() + ".java");
        generatedSource.deleteOnExit();

        // read the source and generate the stubs
        SpringControllerClientSourceGenerator annotationProcessor = new SpringControllerClientSourceGenerator(generator, generatedSource);
        annotationProcessor.process();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(asList(sourceFile));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);

        if (!task.call()) {
            throw new RuntimeException("Class failed to compile.");
        }

    }

    @Test
    public void canRunSimpleAnnotationProcessor() throws IOException {
        SpringControllerAnnotationProcessor processor = new SpringControllerAnnotationProcessor();
        File sourceFile = new File(TEST_SOURCES_DIR + "/TestController.java");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(asList(sourceFile));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);
        task.setProcessors(asList(processor));

        if (!task.call()) {
            throw new RuntimeException("Class failed to compile.");
        }

        assertEquals(processor.getStubs().size(), 1, "Expected exactly one request mapping.");
    }

}
