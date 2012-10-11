package com.thegrayfiles.compile;

import com.thegrayfiles.exception.CompilationFailedException;
import com.thegrayfiles.processor.SpringControllerAnnotationProcessor;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class SimpleCompiler {

    public void compile(File file) throws CompilationFailedException {
        compile(file, new SpringControllerAnnotationProcessor());
    }

    public void compile(File file, SpringControllerAnnotationProcessor processor) throws CompilationFailedException {
        compile(file, processor, new HashMap<String, String>());
    }

    /**
     * Compile a file and process it using the annotation processor specified.
     * @param file the file to compile
     * @param processor the annotation processor to use
     * @param options the options to pass to the annotation processor
     * @throws CompilationFailedException if compilation of the specified file fails
     */
    public void compile(File file, SpringControllerAnnotationProcessor processor, Map<String, String> options) throws CompilationFailedException {
        List<String> processorOptions = buildAnnotationProcessorOptionsList(options);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(asList(file));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, processorOptions, null, javaFileObjects);

        task.setProcessors(asList(processor));

        if (!task.call()) {
            throw new CompilationFailedException(file);
        }
    }

    private List<String> buildAnnotationProcessorOptionsList(Map<String, String> options) {
        List<String> optionsString = new ArrayList<String>();
        for (String key : options.keySet()) {
            optionsString.add("-A" + key + "=" + options.get(key));
        }
        return optionsString;
    }
}
