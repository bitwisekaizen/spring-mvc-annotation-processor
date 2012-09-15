package com.thegrayfiles.generator;

import com.thegrayfiles.server.ServerEndpoint;
import com.thegrayfiles.server.ServerPathVariable;
import com.thegrayfiles.server.ServerRequestParameter;
import com.thegrayfiles.method.MethodSignature;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class JavaClientSourceGenerator {

    private File file;
    private List<ServerEndpoint> stubs = new ArrayList<ServerEndpoint>();
    private MethodImplementationSourceGenerator methodGenerator;

    public JavaClientSourceGenerator(MethodImplementationSourceGenerator methodGenerator, File file) {
        this.methodGenerator = methodGenerator;
        this.file = file;
    }

    public void process() {
        try {
            LinkedList<String> fileContents = new LinkedList<String>();
            fileContents.add("public class " + file.getName().replaceFirst(".java", "") + " {");

            for (ServerEndpoint stub : stubs) {
                // generate signature and method contents
                MethodSignature signature = stub.getMethodSignature();
                fileContents.add("public " + signature.getReturnType().getCanonicalName() + " " + signature.getMethodName());
                fileContents.add("(");

                // add request parameters
                List<ServerRequestParameter> requestParameters = stub.getRequestParameters();
                for (ServerRequestParameter requestParameter : requestParameters) {
                    fileContents.add(requestParameter.getType().getCanonicalName() + " " + requestParameter.getName());
                    fileContents.add(", ");
                }
                if (requestParameters.size() != 0) {
                    fileContents.removeLast();
                }

                // add path variables
                List<ServerPathVariable> pathVariables = stub.getPathVariables();
                for (ServerPathVariable pathVariable : pathVariables) {
                     fileContents.add(pathVariable.getType().getCanonicalName() + " " + pathVariable.getName());
                }
                fileContents.add(") {");
                fileContents.addAll(methodGenerator.generate(stub));
                fileContents.add("}");
            }

            fileContents.add("}");
            FileUtils.writeLines(file, fileContents);
        } catch (IOException e) {
            // throw runtime exception for now, but have more explicit exception soon
            throw new RuntimeException("IOException occurred when writing to the file.", e);
        }
    }

    public void addStub(ServerEndpoint stub) {
        stubs.add(stub);
    }
}
