package com.thegrayfiles.processor;

import com.thegrayfiles.method.MethodParameter;
import com.thegrayfiles.method.MethodSignature;
import com.thegrayfiles.server.ServerEndpoint;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

public class AnnotationEnvironmentToServerEndpointConverter {

    private ClassStringToClassConverter classStringToClassConverter = new ClassStringToClassConverter();

    public List<ServerEndpoint> convert(ProcessingEnvironment processingEnv, RoundEnvironment roundEnvironment) {
        List<ServerEndpoint> endpoints = new ArrayList<ServerEndpoint>();
        Set<? extends Element> methods = roundEnvironment.getElementsAnnotatedWith(RequestMapping.class);
        for (Element method : methods) {
            try {
                // only support single request mapping if any values exist
                String requestMapping = "";
                RequestMapping requestMappingAnnotation = method.getAnnotation(RequestMapping.class);
                String[] mappings = requestMappingAnnotation.value();
                if (mappings != null) {
                    requestMapping = mappings[0];
                }

                // only support first request method if any values exist
                RequestMethod[] requestMethods = requestMappingAnnotation.method();
                RequestMethod requestMethod = requestMethods.length == 0 ? RequestMethod.GET : requestMethods[0];

                String methodName = method.getSimpleName().toString();
                ExecutableElement executableMethod = (ExecutableElement) method;
                Element elementReturnType = processingEnv.getTypeUtils().asElement(executableMethod.getReturnType());
                Class<?> returnType = elementReturnType == null ? void.class : classStringToClassConverter.convert(elementReturnType.toString());

                MethodSignature methodSignature = new MethodSignature(returnType, methodName);
                ServerEndpoint endpoint = new ServerEndpoint(requestMapping, requestMethod, methodSignature);
                for (VariableElement parameter : executableMethod.getParameters()) {
                    RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                    if (requestParam != null) {
                        endpoint.addRequestParameter(new MethodParameter(getParameterType(parameter, processingEnv),
                                parameter.getSimpleName().toString()));
                    }

                    PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
                    if (pathVariable != null) {
                        endpoint.addPathVariable(new MethodParameter(getParameterType(parameter, processingEnv),
                                parameter.getSimpleName().toString()));
                    }

                    RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
                    if (requestBody != null) {
                        endpoint.setRequestBody(new MethodParameter(getParameterType(parameter, processingEnv), parameter.getSimpleName().toString()));
                    }
                }
                endpoints.add(endpoint);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found.");
            }
        }

        return endpoints;
    }

    private Class<?> getParameterType(VariableElement parameter, ProcessingEnvironment processingEnvironment) throws ClassNotFoundException {
        TypeMirror mirror = parameter.asType();
        TypeElement element = (TypeElement) processingEnvironment.getTypeUtils().asElement(mirror);
        Name name = element.getQualifiedName();
        return classStringToClassConverter.convert(name.toString());
    }
}
