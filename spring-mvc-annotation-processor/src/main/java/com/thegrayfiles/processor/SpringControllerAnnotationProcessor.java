package com.thegrayfiles.processor;

import com.thegrayfiles.generator.JavaClientSourceGenerator;
import com.thegrayfiles.generator.MethodImplementationSourceGenerator;
import com.thegrayfiles.server.ServerEndpoint;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes(value= "org.springframework.web.bind.annotation.RequestMapping")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions(SpringControllerAnnotationProcessor.OPTION_CLIENT_OUTPUT_FILE)
public class SpringControllerAnnotationProcessor extends AbstractProcessor {

    private List<ServerEndpoint> serverEndpoints = new ArrayList<ServerEndpoint>();
    private File outputFile;

    public static final String OPTION_CLIENT_OUTPUT_FILE = "clientOutputFile";

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        String clientOutputFilename = this.processingEnv.getOptions().get(OPTION_CLIENT_OUTPUT_FILE);
        if (clientOutputFilename != null) {
            if (outputFile == null) {
                outputFile = new File(clientOutputFilename);
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    // can't create...do something here
                }
            }
        } else {
            return false;
        }

        if (roundEnvironment.processingOver()) {
            MethodImplementationSourceGenerator methodImplementationSourceGenerator = new MethodImplementationSourceGenerator();
            JavaClientSourceGenerator javaClientSourceGenerator = new JavaClientSourceGenerator(methodImplementationSourceGenerator, outputFile);
            for (ServerEndpoint endpoint : serverEndpoints) {
                javaClientSourceGenerator.addEndpoint(endpoint);
            }
            javaClientSourceGenerator.process();
        }

        AnnotationEnvironmentToServerEndpointConverter converter = new AnnotationEnvironmentToServerEndpointConverter();
        serverEndpoints.addAll(converter.convert(this.processingEnv, roundEnvironment));
        return true;
    }
}
