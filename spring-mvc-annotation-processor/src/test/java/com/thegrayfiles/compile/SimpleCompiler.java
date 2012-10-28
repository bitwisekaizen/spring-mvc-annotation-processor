package com.thegrayfiles.compile;

import com.thegrayfiles.exception.CompilationFailedException;
import com.thegrayfiles.processor.SpringControllerAnnotationProcessor;
import org.apache.commons.io.FileUtils;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class SimpleCompiler {

    private Map<String, String> annotationProcessorOptions = new HashMap<String, String>();
    private List<SpringControllerAnnotationProcessor> annotationProcessors = new ArrayList<SpringControllerAnnotationProcessor>();

    /**
     * Compile a file and process it using the annotation processor specified.
     * @param file the file to compile
     * @throws CompilationFailedException if compilation of the specified file fails
     */
    public File compile(File file) throws CompilationFailedException {
        List<String> processorOptions = buildAnnotationProcessorOptionsList();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Collection<File> files;
        if (file.isDirectory()) {
            files = FileUtils.listFiles(file, new String[]{"java"}, true);
        } else {
            files = asList(file);
        }
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(files);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, processorOptions, null, javaFileObjects);

        task.setProcessors(annotationProcessors);

        if (!task.call()) {
            throw new CompilationFailedException(file);
        }

        return new File(file.getAbsolutePath().replaceAll(".java", ".class"));
    }

    private List<String> buildAnnotationProcessorOptionsList() {
        List<String> optionsString = new ArrayList<String>();
        for (String key : annotationProcessorOptions.keySet()) {
            optionsString.add("-A" + key + "=" + annotationProcessorOptions.get(key));
        }
        return optionsString;
    }

    public void addAnnotationProcessor(SpringControllerAnnotationProcessor annotationProcessor) {
        annotationProcessors.add(annotationProcessor);
    }

    public void addAnnotationProcessorOption(String key, String value) {
        annotationProcessorOptions.put(key, value);
    }

    public File compile(File inputFile, File outputDirectory) throws CompilationFailedException, IOException {
        boolean createDestinationDirectory = true;
        File compiledFile = compile(inputFile);
        FileUtils.moveFileToDirectory(compiledFile, outputDirectory, createDestinationDirectory);
        return new File(outputDirectory + "/" + compiledFile.getName());
    }
}
