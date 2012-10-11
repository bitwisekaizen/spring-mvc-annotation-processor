package com.thegrayfiles.processor;

import com.thegrayfiles.server.ServerEndpoint;
import org.apache.commons.io.FileUtils;

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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes(value= "org.springframework.web.bind.annotation.RequestMapping")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedOptions(SpringControllerAnnotationProcessor.OPTION_CLIENT_OUTPUT_FILE)
public class SpringControllerAnnotationProcessor extends AbstractProcessor {

    private List<ServerEndpoint> stubs = new ArrayList<ServerEndpoint>();

    public static final String OPTION_CLIENT_OUTPUT_FILE = "clientOutputFile";

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        String clientOutputFilename = this.processingEnv.getOptions().get(OPTION_CLIENT_OUTPUT_FILE);
        if (clientOutputFilename != null) {
            File file = new File(clientOutputFilename);
            try {
                file.createNewFile();
                FileUtils.writeLines(file, Arrays.asList("public class " + file.getName().replaceAll("(.*?)\\.java",
                        "$1") + "{}"));
            } catch (IOException e) {
                // blegh
            }
        }

        AnnotationEnvironmentToServerEndpointConverter converter = new AnnotationEnvironmentToServerEndpointConverter();
        stubs.addAll(converter.convert(this.processingEnv, roundEnvironment));
        return true;
    }

    public List<ServerEndpoint> getServerEndpoints() {
        return stubs;
    }
}
