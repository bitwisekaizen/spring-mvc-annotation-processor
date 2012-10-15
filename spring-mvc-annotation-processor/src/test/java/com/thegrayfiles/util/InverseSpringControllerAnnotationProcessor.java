package com.thegrayfiles.util;

import com.thegrayfiles.method.MethodParameter;
import com.thegrayfiles.method.MethodSignature;
import org.apache.commons.io.FileUtils;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InverseSpringControllerAnnotationProcessor {

    private File sourceFile;
    private File outputDirectory;
    private List<MethodSignature> methodSignatures = new ArrayList<MethodSignature>();

    public InverseSpringControllerAnnotationProcessor(File sourceFile, File outputDirectory) {
        this.sourceFile = sourceFile;
        this.outputDirectory = outputDirectory;
    }

    public void process() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);
        if (!task.call()) {
            throw new RuntimeException("Class failed to compile.");
        }

        // move compiled file to somewhere in the classpath
        File compiledFile = new File(sourceFile.getAbsolutePath().replaceAll(".java", ".class"));
        File classFile = new File(outputDirectory.getAbsolutePath() + "/" + compiledFile.getName());
        FileUtils.moveFile(compiledFile, classFile);
        classFile.deleteOnExit();
        compiledFile.deleteOnExit();

        ClassLoader loader = ClassLoader.getSystemClassLoader();
        try {
            Class<?> clazz = loader.loadClass(classFile.getName().replaceAll(".class", ""));
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (!method.getDeclaringClass().equals(Object.class)) {
                    addMethodSignature(method);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void addMethodSignature(Method method) {
        MethodSignature methodSignature = new MethodSignature(method.getReturnType(), method.getName());
        for (Class<?> parameterType : method.getParameterTypes()) {
            methodSignature.addParameter(new MethodParameter(parameterType, "whatever"));
        }
        methodSignatures.add(methodSignature);
    }

    public List<MethodSignature> getMethodSignatures() {
        return methodSignatures;
    }
}
