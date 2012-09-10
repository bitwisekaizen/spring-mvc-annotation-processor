package com.thegrayfiles.processor;

import com.thegrayfiles.client.ClientMethod;
import com.thegrayfiles.client.RequestParameter;
import org.springframework.web.bind.annotation.RequestMapping;
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

    private void canConvertAnnotatedMethodWithSpecifiedReturnTypeToClientStub(Class<?> returnTypeClass) throws ClassNotFoundException {
        TypeElementToClientStubConverter typeElementAdapter = new TypeElementToClientStubConverter();

        String stringMethodName = "someCrazyMethodName";
        TypeMirror typeMirror = mock(TypeMirror.class);
        Element returnType = mock(Element.class);
        RoundEnvironment roundEnvironment = mockRoundEnvironment(stringMethodName, returnTypeClass, typeMirror, returnType);
        ProcessingEnvironment processingEnvironment = mockProcessingEnvironment(typeMirror, returnType);

        List<ClientMethod> stubs = typeElementAdapter.convert(processingEnvironment, roundEnvironment);
        ClientMethod stub = stubs.get(0);
        assertEquals(stub.getMethodSignature().getMethodName(), stringMethodName);
        assertEquals(stub.getMethodSignature().getReturnType(), returnTypeClass);
        assertEquals(stub.getMethodSignature().getParameters().size(), 1, "Expected exactly one parameter.");

        RequestParameter requestParameter = stub.getRequestParameters().get(0);
        assertEquals(requestParameter.getName(), "requestParam");
        assertEquals(requestParameter.getType(), Integer.class);
    }

    private RoundEnvironment mockRoundEnvironment(String stringMethodName, Class<?> returnTypeClass, TypeMirror typeMirror, Element returnTypeElement) {

        ExecutableElement executableMethod = mock(ExecutableElement.class);
        RoundEnvironment roundEnvironment = mock(RoundEnvironment.class);
        VariableElement requestParameter = mock(VariableElement.class);
        List requestParameters = new ArrayList();
        requestParameters.add(requestParameter);

        Name methodName = mock(Name.class);
        Name requestParameterName = mock(Name.class);

        when(returnTypeElement.toString()).thenReturn(returnTypeClass.getCanonicalName());
        when(methodName.toString()).thenReturn(stringMethodName);
        when(executableMethod.getReturnType()).thenReturn(typeMirror);
        when(executableMethod.getSimpleName()).thenReturn(methodName);
        when(executableMethod.getParameters()).thenReturn(requestParameters);
        when(roundEnvironment.getElementsAnnotatedWith(RequestMapping.class)).thenReturn(new TreeSet(asList(executableMethod)));
        when(requestParameter.getSimpleName()).thenReturn(requestParameterName);
        when(requestParameterName.toString()).thenReturn("requestParam");
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
