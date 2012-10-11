package com.thegrayfiles.tests.acceptance;

import com.thegrayfiles.exception.CompilationFailedException;
import com.thegrayfiles.processor.SpringControllerAnnotationProcessor;
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

    @Test
    public void canRunSimpleAnnotationProcessor() throws IOException, CompilationFailedException {
        SpringControllerAnnotationProcessor processor = new SpringControllerAnnotationProcessor();
        File sourceFile = new File(TEST_SOURCES_DIR + "/TestController.java");

        compile(sourceFile, processor);

        assertEquals(processor.getStubs().size(), 1, "Expected exactly one request mapping.");
    }

    /**
     * Compile a file and process it using the annotation processor specified.
     * @param file the file to compile
     * @param processor the annotation processor to use
     * @throws CompilationFailedException if compilation of the specified file fails
     */
    private void compile(File file, SpringControllerAnnotationProcessor processor) throws CompilationFailedException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(asList(file));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);
        task.setProcessors(asList(processor));

        if (!task.call()) {
            throw new CompilationFailedException(file);
        }
    }
}
