package com.thegrayfiles.tests;

import com.thegrayfiles.client.ClientMethod;
import com.thegrayfiles.generator.RestTemplatePoweredClientSourceGenerator;
import com.thegrayfiles.generator.SpringControllerClientSourceGenerator;
import com.thegrayfiles.processor.SpringControllerAnnotationProcessor;
import com.thegrayfiles.processor.TypeElementToClientStubConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.testng.annotations.Test;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class GeneratedClientTests {
    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/target/generated-sources";
    private static final String TEST_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/src/test/java/com/thegrayfiles/util";

    public void canGenerateRestTemplatePoweredClient() throws IOException {
        File generatedSourcesDirectory = new File(GENERATED_SOURCES_DIR);
        generatedSourcesDirectory.mkdirs();

        RestTemplatePoweredClientSourceGenerator generator = new RestTemplatePoweredClientSourceGenerator();

        File sourceFile = new ClassPathResource("TestController.java").getFile();

        File generatedSource = new File(GENERATED_SOURCES_DIR + "/" + generator.getClass().getSimpleName() + ".java");
        generatedSource.deleteOnExit();

        // read the source and generate the stubs
        SpringControllerClientSourceGenerator annotationProcessor = new SpringControllerClientSourceGenerator(generator, generatedSource);
        annotationProcessor.process();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);

        if (!task.call()) {
            throw new RuntimeException("Class failed to compile.");
        }

    }

    @Test
    public void canRunSimpleAnnotationProcessor() throws IOException {
        SpringControllerAnnotationProcessor processor = new SpringControllerAnnotationProcessor();
        File sourceFile = new File(TEST_SOURCES_DIR + "/TestController.java");

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> javaFileObjects = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile));
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, javaFileObjects);
        task.setProcessors(Arrays.asList(processor));

        if (!task.call()) {
            throw new RuntimeException("Class failed to compile.");
        }

        assertEquals(processor.getStubs().size(), 1, "Expected exactly one request mapping.");
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

    private void canConvertAnnotatedMethodWithSpecifiedReturnTypeToClientStub(Class<?> returnTypeClass) throws ClassNotFoundException {
        TypeElementToClientStubConverter typeElementAdapter = new TypeElementToClientStubConverter();

        String stringMethodName = "someCrazyMethodName";
        TypeMirror typeMirror = mock(TypeMirror.class);
        Element returnType = mock(Element.class);
        RoundEnvironment roundEnvironment = mockRoundEnvironment(stringMethodName, returnTypeClass, typeMirror, returnType);
        ProcessingEnvironment processingEnvironment = mockProcessingEnvironment(typeMirror, returnType);

        List<ClientMethod> stubs = typeElementAdapter.convert(processingEnvironment, roundEnvironment);
        assertEquals(stubs.get(0).getMethodSignature().getMethodName(), stringMethodName);
        assertEquals(stubs.get(0).getMethodSignature().getReturnType(), returnTypeClass);
    }

    private RoundEnvironment mockRoundEnvironment(String stringMethodName, Class<?> returnTypeClass, TypeMirror typeMirror, Element returnTypeElement) {

        ExecutableElement executableMethod = mock(ExecutableElement.class);
        RoundEnvironment roundEnvironment = mock(RoundEnvironment.class);

        Name methodName = mock(Name.class);

        when(returnTypeElement.toString()).thenReturn(returnTypeClass.getCanonicalName());
        when(methodName.toString()).thenReturn(stringMethodName);
        when(executableMethod.getReturnType()).thenReturn(typeMirror);
        when(executableMethod.getSimpleName()).thenReturn(methodName);

        when(roundEnvironment.getElementsAnnotatedWith(RequestMapping.class)).thenReturn(new TreeSet(Arrays.asList(executableMethod)));
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
