package com.thegrayfiles.processor;

import com.thegrayfiles.server.ServerEndpoint;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes(value= "org.springframework.web.bind.annotation.RequestMapping")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SpringControllerAnnotationProcessor extends AbstractProcessor {

    private List<ServerEndpoint> stubs = new ArrayList<ServerEndpoint>();

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        AnnotationEnvironmentToServerEndpointConverter converter = new AnnotationEnvironmentToServerEndpointConverter();
        stubs.addAll(converter.convert(this.processingEnv, roundEnvironment));
        return true;
    }

    public List<ServerEndpoint> getServerEndpoints() {
        return stubs;
    }
}
