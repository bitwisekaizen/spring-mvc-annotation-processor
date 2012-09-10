package com.thegrayfiles.processor;

import com.thegrayfiles.client.ClientMethod;

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

    private List<ClientMethod> stubs = new ArrayList<ClientMethod>();

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        TypeElementToClientStubConverter converter = new TypeElementToClientStubConverter();
        stubs.addAll(converter.convert(this.processingEnv, roundEnvironment));
        return true;
    }

    public List<ClientMethod> getStubs() {
        return stubs;
    }
}
