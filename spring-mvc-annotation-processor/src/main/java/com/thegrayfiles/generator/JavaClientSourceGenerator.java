package com.thegrayfiles.generator;

import com.thegrayfiles.method.MethodParameter;
import com.thegrayfiles.method.MethodSignature;
import com.thegrayfiles.processor.ClassStringToClassConverter;
import com.thegrayfiles.server.ServerEndpoint;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class JavaClientSourceGenerator {

    private File file;
    private List<ServerEndpoint> endpoints = new ArrayList<ServerEndpoint>();
    private MethodImplementationSourceGenerator methodGenerator;

    public JavaClientSourceGenerator(MethodImplementationSourceGenerator methodGenerator, File file) {
        this.methodGenerator = methodGenerator;
        this.file = file;
    }

    public void process() {

        try {
            String className = file.getName().replaceFirst(".java", "");
            LinkedList<String> fileContents = new LinkedList<String>();

            addImportsToFileContents(fileContents);
            fileContents.add("public class " + className + " {");

            // generate data members
            fileContents.add("private JavaClientHttpOperations ops;");

            // generate client constructor
            fileContents.add("public " + className + "(JavaClientHttpOperations ops) { this.ops = ops; }");

            for (ServerEndpoint endpoint: endpoints) {
                // generate signature and method contents
                MethodSignature signature = endpoint.getMethodSignature();
                fileContents.add("public " + signature.getReturnType().getCanonicalName() + " " + signature.getMethodName());
                fileContents.add("(");

                List<MethodParameter> parameters = endpoint.getMethodSignature().getParameters();
                for (MethodParameter parameter : parameters) {
                    fileContents.add(parameter.getType().getCanonicalName() + " " + parameter.getName());
                    fileContents.add(", ");
                }

                if (parameters.size() != 0) {
                    fileContents.removeLast();
                }

                fileContents.add(") {");
                fileContents.addAll(methodGenerator.generate(endpoint));
                fileContents.add("}");
            }

            fileContents.add("}");
            FileUtils.writeLines(file, fileContents);
        } catch (IOException e) {
            // throw runtime exception for now, but have more explicit exception soon
            throw new RuntimeException("IOException occurred when writing to the file.", e);
        }
    }

    private void addImportsToFileContents(LinkedList<String> fileContents) {
        Set<Class> classesToImport = aggregateClassesToImport();
        fileContents.add("import java.util.Map;");
        fileContents.add("import java.util.HashMap;");
        fileContents.add("import com.thegrayfiles.generator.JavaClientHttpOperations;");

        for (Class clazz : classesToImport) {
            if (!ClassStringToClassConverter.SUPPORTED_PRIMITIVE_CLASSES.contains(clazz)) {
                fileContents.add("import " + clazz.getCanonicalName() + ";");
            }
        }
    }

    private Set<Class> aggregateClassesToImport() {
        Set<Class> classesToImport = new HashSet<Class>();
        for (ServerEndpoint endpoint : endpoints) {
            Class returnType = endpoint.getMethodSignature().getReturnType();
            classesToImport.add(returnType);
        }
        return classesToImport;
    }

    public void addEndpoint(ServerEndpoint endpoint) {
        endpoints.add(endpoint);
    }
}
