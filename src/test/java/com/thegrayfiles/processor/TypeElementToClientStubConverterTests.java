package com.thegrayfiles.processor;

import com.thegrayfiles.client.ClientMethod;
import com.thegrayfiles.client.RequestParameter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
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
    private TypeElementToClientStubConverter typeElementAdapter;

    @BeforeMethod
    public void setup() {
        typeElementAdapter = new TypeElementToClientStubConverter();
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
        List requestParameters = mockRequestParameter("requestParam");
        expectAnnotatedMethod(requestParameters);

        List<ClientMethod> stubs = typeElementAdapter.convert(processingEnvironment, roundEnvironment);
        ClientMethod stub = stubs.get(0);

        RequestParameter requestParameter = stub.getRequestParameters().get(0);
        assertEquals(requestParameter.getName(), "requestParam");
        assertEquals(requestParameter.getType(), Integer.class);
    }

    private void canConvertAnnotatedMethodWithSpecifiedReturnTypeToClientStub(Class<?> returnTypeClass) throws ClassNotFoundException {
        String stringMethodName = "someCrazyMethodName";
        expectAnnotatedMethod(returnTypeClass);

        List<ClientMethod> stubs = typeElementAdapter.convert(processingEnvironment, roundEnvironment);
        ClientMethod stub = stubs.get(0);
        assertEquals(stub.getMethodSignature().getMethodName(), stringMethodName);
        assertEquals(stub.getMethodSignature().getReturnType(), returnTypeClass);
    }

    private void expectAnnotatedMethod(List requestParameters) throws ClassNotFoundException {
        expectAnnotatedMethod(void.class, requestParameters);
    }

    private void expectAnnotatedMethod(Class<?> returnType, List requestParameters) {
        String stringMethodName = "someCrazyMethodName";
        TypeMirror typeMirror = mock(TypeMirror.class);
        Element returnTypeElement = mock(Element.class);
        roundEnvironment = mockRoundEnvironment(stringMethodName, returnType, requestParameters, typeMirror, returnTypeElement);
        processingEnvironment = mockProcessingEnvironment(typeMirror, returnTypeElement);
    }

    private void expectAnnotatedMethod(Class<?> returnType) {
        expectAnnotatedMethod(returnType, new ArrayList());
    }

    private List mockRequestParameter(String parameterName) {
        VariableElement requestParameter = mock(VariableElement.class);
        List requestParameters = new ArrayList();
        requestParameters.add(requestParameter);

        Name requestParameterName = mock(Name.class);
        when(requestParameter.getSimpleName()).thenReturn(requestParameterName);
        when(requestParameterName.toString()).thenReturn(parameterName);
        return requestParameters;
    }

    private RoundEnvironment mockRoundEnvironment(String stringMethodName, Class<?> returnTypeClass, List requestParameters, TypeMirror typeMirror, Element returnTypeElement) {

        ExecutableElement executableMethod = mock(ExecutableElement.class);
        RoundEnvironment roundEnvironment = mock(RoundEnvironment.class);


        Name methodName = mock(Name.class);


        when(returnTypeElement.toString()).thenReturn(returnTypeClass.getCanonicalName());
        when(methodName.toString()).thenReturn(stringMethodName);
        when(executableMethod.getReturnType()).thenReturn(typeMirror);
        when(executableMethod.getSimpleName()).thenReturn(methodName);
        when(executableMethod.getParameters()).thenReturn(requestParameters);
        when(roundEnvironment.getElementsAnnotatedWith(RequestMapping.class)).thenReturn(new TreeSet(asList(executableMethod)));
        return roundEnvironment;
    }

    private ProcessingEnvironment mockProcessingEnvironment(TypeMirror typeMirror, Element returnType) {
        ProcessingEnvironment processingEnvironment = mock(ProcessingEnvironment.class);
        Types typeUtils = mock(Types.class);
        when(typeUtils.asElement(typeMirror)).thenReturn(returnType);
        when(processingEnvironment.getTypeUtils()).thenReturn(typeUtils);
        return processingEnvironment;
    }
}
