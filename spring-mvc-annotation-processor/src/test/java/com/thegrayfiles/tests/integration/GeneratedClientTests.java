package com.thegrayfiles.tests.integration;

import com.thegrayfiles.compile.SimpleCompiler;
import com.thegrayfiles.exception.CompilationFailedException;
import com.thegrayfiles.processor.SpringControllerAnnotationProcessor;
import com.thegrayfiles.util.TestDirectories;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertTrue;

public class GeneratedClientTests {

    private TestDirectories testDirectories = new TestDirectories();

    @Test
    public void processorProducesClientSourceFile() throws CompilationFailedException, IOException {
        File clientSourceFile = processTestController();
        assertTrue(clientSourceFile.exists(), "Client source file does not exist.");
    }

    @Test
    public void processorProducesCompilableClientSourceFile() throws CompilationFailedException, IOException {
        File clientSourceFile = processTestController();
        SimpleCompiler compiler = new SimpleCompiler();
        compiler.compile(clientSourceFile);
    }

    private File processTestController() throws CompilationFailedException, IOException {
        SpringControllerAnnotationProcessor processor = new SpringControllerAnnotationProcessor();
        File annotatedSourceFile = new File(testDirectories.getTestSources() + "/TestController.java");

        // create file to get the appropriate temp file name and then delete it so that the processor can recreate it
        File generatedSourcesDirectory = new File(testDirectories.getGeneratedSources());
        generatedSourcesDirectory.mkdirs();
        File clientSourceFile = File.createTempFile("TestClient", ".java", generatedSourcesDirectory);
        clientSourceFile.delete();

        SimpleCompiler annotationProcessingCompiler = new SimpleCompiler();

        annotationProcessingCompiler.addAnnotationProcessorOption(SpringControllerAnnotationProcessor.OPTION_CLIENT_OUTPUT_FILE, clientSourceFile.getAbsolutePath());
        annotationProcessingCompiler.addAnnotationProcessor(processor);
        annotationProcessingCompiler.compile(annotatedSourceFile);
        clientSourceFile.deleteOnExit();

        return clientSourceFile;
    }
}
