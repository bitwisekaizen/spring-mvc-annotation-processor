package com.thegrayfiles.processor;

import com.thegrayfiles.method.MethodParameter;
import com.thegrayfiles.server.ServerEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class TypeElementToClientStubConverterTests {
    private RoundEnvironment roundEnvironment;
    private ProcessingEnvironment processingEnvironment;
    private AnnotationEnvironmentToServerEndpointConverter typeElementAdapter;
    private Types typeUtils;
    private Name methodName;
    private TypeMirror returnTypeMirror;
    private Element returnTypeElement;
    private ExecutableElement executableMethod;
    private Name requestParameterClassName;
    private Name requestParameterName;
    private TypeMirror requestParamTypeMirror;
    private TypeElement requestParamTypeElement;
    private VariableElement requestParameter;
    private RequestParam requestParamAnnotation;
    private List requestParameters;

    @BeforeMethod
    public void setup() {
        typeElementAdapter = new AnnotationEnvironmentToServerEndpointConverter();
        typeUtils = mock(Types.class);

        returnTypeMirror = mock(TypeMirror.class);
        returnTypeElement = mock(Element.class);
        executableMethod = mock(ExecutableElement.class);
        methodName = mock(Name.class);

        roundEnvironment = mock(RoundEnvironment.class);

        RequestMapping annotation = mock(RequestMapping.class);

        when(executableMethod.getReturnType()).thenReturn(returnTypeMirror);
        when(executableMethod.getSimpleName()).thenReturn(methodName);
        when(executableMethod.getAnnotation(RequestMapping.class)).thenReturn(annotation);
        when(annotation.value()).thenReturn(new String[] { "/something" });

        when(roundEnvironment.getElementsAnnotatedWith(RequestMapping.class)).thenReturn(new TreeSet(asList(executableMethod)));

        processingEnvironment = mock(ProcessingEnvironment.class);
        when(processingEnvironment.getTypeUtils()).thenReturn(typeUtils);
        when(typeUtils.asElement(returnTypeMirror)).thenReturn(returnTypeElement);

        requestParameter = mock(VariableElement.class);
        requestParamAnnotation = mock(RequestParam.class);
        requestParameters = new ArrayList();
        requestParamTypeMirror = mock(TypeMirror.class);
        requestParamTypeElement = mock(TypeElement.class);

        requestParameterName = mock(Name.class);
        requestParameterClassName = mock(Name.class);

        when(requestParameter.getSimpleName()).thenReturn(requestParameterName);

        when(requestParameter.getAnnotation(RequestParam.class)).thenReturn(requestParamAnnotation);
        when(requestParameter.asType()).thenReturn(requestParamTypeMirror);
        when(typeUtils.asElement(requestParamTypeMirror)).thenReturn(requestParamTypeElement);
        when(requestParamTypeElement.getQualifiedName()).thenReturn(requestParameterClassName);

        when(executableMethod.getParameters()).thenReturn(requestParameters);
    }

    @Test
    public void canConvertAnnotatedMethodWithNonPrimitiveReturnTypeToClientStub() throws ClassNotFoundException {
        canConvertAnnotatedMethodWithSpecifiedReturnTypeToClientStub(Integer.class);
    }

    @Test
    public void canConvertAnnotatedMethodWithVoidReturnTypeToClientStub() throws ClassNotFoundException {
        canConvertAnnotatedMethodWithSpecifiedReturnTypeToClientStub(void.class);
    }

    @Test
    public void canConvertAnnotatedMethodWithPrimitiveReturnTypeToClientStub() throws ClassNotFoundException {
        canConvertAnnotatedMethodWithSpecifiedReturnTypeToClientStub(int.class);
    }

    @Test
    public void canConvertAnnotatedMethodWithRequestParameterToClientStub() throws ClassNotFoundException {
        mockConverterDependencies("someName", Float.class);
        mockRequestParameter("requestParam", Object.class);

        List<ServerEndpoint> stubs = typeElementAdapter.convert(processingEnvironment, roundEnvironment);
        ServerEndpoint stub = stubs.get(0);

        MethodParameter requestParameter = stub.getRequestParameters().get(0);
        assertEquals(requestParameter.getName(), "requestParam");
        assertEquals(requestParameter.getType(), Object.class);
    }

    private void canConvertAnnotatedMethodWithSpecifiedReturnTypeToClientStub(Class<?> returnTypeClass) throws ClassNotFoundException {
        String stringMethodName = "someCrazyMethodName";
        mockConverterDependencies(stringMethodName, returnTypeClass);

        List<ServerEndpoint> stubs = typeElementAdapter.convert(processingEnvironment, roundEnvironment);
        ServerEndpoint stub = stubs.get(0);
        assertEquals(stub.getMethodSignature().getMethodName(), stringMethodName);
        assertEquals(stub.getMethodSignature().getReturnType(), returnTypeClass);
    }

    private void mockConverterDependencies(String stringMethodName, Class<?> returnTypeClass) {
        when(returnTypeElement.toString()).thenReturn(returnTypeClass.getCanonicalName());
        when(methodName.toString()).thenReturn(stringMethodName);
    }

    private void mockRequestParameter(String parameterName, Class<?> type) {
        when(requestParameterName.toString()).thenReturn(parameterName);
        when(requestParameterClassName.toString()).thenReturn(type.getCanonicalName());
        requestParameters.add(requestParameter);
    }
}
