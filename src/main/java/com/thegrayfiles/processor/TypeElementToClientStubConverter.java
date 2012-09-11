package com.thegrayfiles.processor;

import com.thegrayfiles.client.ClientMethod;
import com.thegrayfiles.client.RequestParameter;
import com.thegrayfiles.method.MethodParameter;
import com.thegrayfiles.method.MethodSignature;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TypeElementToClientStubConverter {

    private ClassStringToClassConverter classStringToClassConverter = new ClassStringToClassConverter();

    public List<ClientMethod> convert(ProcessingEnvironment processingEnv, RoundEnvironment roundEnvironment) {
        List<ClientMethod> stubs = new ArrayList<ClientMethod>();
        Set<? extends Element> methods = roundEnvironment.getElementsAnnotatedWith(RequestMapping.class);
        for (Element method : methods) {
            try {
                String methodName = method.getSimpleName().toString();
                ExecutableElement executableMethod = (ExecutableElement) method;
                Element elementReturnType = processingEnv.getTypeUtils().asElement(executableMethod.getReturnType());
                Class<?> returnType = classStringToClassConverter.convert(elementReturnType.toString());

                MethodSignature methodSignature = new MethodSignature(returnType, methodName);
                ClientMethod clientMethod = new ClientMethod(methodSignature, null);
                for (VariableElement parameter : executableMethod.getParameters()) {
                    methodSignature.addParameter(new MethodParameter(parameter.getClass()));
                    clientMethod.addRequestParameter(new RequestParameter(getParameterType(parameter, processingEnv), parameter.getSimpleName().toString()));
                }
                stubs.add(clientMethod);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found.");
            }
        }

        return stubs;
    }

    private Class<?> getParameterType(VariableElement parameter, ProcessingEnvironment processingEnvironment) throws ClassNotFoundException {
        TypeMirror mirror = parameter.asType();
        TypeElement element = (TypeElement) processingEnvironment.getTypeUtils().asElement(mirror);
        Name name = element.getQualifiedName();
        return classStringToClassConverter.convert(name.toString());
    }
}
