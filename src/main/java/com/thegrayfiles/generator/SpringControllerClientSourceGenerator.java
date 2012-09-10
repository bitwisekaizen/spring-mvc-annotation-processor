package com.thegrayfiles.generator;

import com.thegrayfiles.client.ClientMethod;
import com.thegrayfiles.client.PathVariable;
import com.thegrayfiles.client.RequestParameter;
import com.thegrayfiles.method.MethodSignature;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SpringControllerClientSourceGenerator {

    private File file;
    private List<ClientMethod> stubs = new ArrayList<ClientMethod>();
    private MethodImplementationSourceGenerator sourceGenerator;

    public SpringControllerClientSourceGenerator(MethodImplementationSourceGenerator sourceGenerator, File file) {
        this.sourceGenerator = sourceGenerator;
        this.file = file;
    }

    public void process() {
        try {
            LinkedList<String> fileContents = new LinkedList<String>();
            fileContents.add("public class " + file.getName().replaceFirst(".java", "") + " {");

            for (ClientMethod stub : stubs) {
                // generate signature and method contents
                MethodSignature signature = stub.getMethodSignature();
                fileContents.add("public " + signature.getReturnType().getCanonicalName() + " " + signature.getMethodName());
                fileContents.add("(");

                // add request parameters
                List<RequestParameter> requestParameters = stub.getRequestParameters();
                for (RequestParameter requestParameter : requestParameters) {
                    fileContents.add(requestParameter.getType().getCanonicalName() + " " + requestParameter.getName());
                    fileContents.add(", ");
                }
                if (requestParameters.size() != 0) {
                    fileContents.removeLast();
                }

                // add path variables
                List<PathVariable> pathVariables = stub.getPathVariables();
                for (PathVariable pathVariable : pathVariables) {
                     fileContents.add(pathVariable.getType().getCanonicalName() + " " + pathVariable.getName());
                }
                fileContents.add(") {");
                fileContents.addAll(sourceGenerator.generate(stub));
                fileContents.add("}");
            }

            fileContents.add("}");
            FileUtils.writeLines(file, fileContents);
        } catch (IOException e) {
            // throw runtime exception for now, but have more explicit exception soon
            throw new RuntimeException("IOException occurred when writing to the file.", e);
        }
    }

    public void addStub(ClientMethod stub) {
        stubs.add(stub);
    }
}
