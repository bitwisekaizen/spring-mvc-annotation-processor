package com.thegrayfiles.builders;

import com.thegrayfiles.compile.SimpleCompiler;
import com.thegrayfiles.exception.CompilationFailedException;
import com.thegrayfiles.generator.JavaClientHttpOperations;
import com.thegrayfiles.generator.RestTemplatePoweredHttpOperations;
import com.thegrayfiles.processor.SpringControllerAnnotationProcessor;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class DynamicallyGeneratedClientMethodInvoker {

    private static final String GENERATED_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/target/generated-sources";
    private static final String TEST_SOURCES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/src/main/java/com/thegrayfiles/web";
    private static final String TEST_CLASSES_DIR = "/code/github/spring-mvc-annotation-processor/spring-mvc-annotation-processor-test-webapp/target/test-classes";

    private String methodName;
    private List<Class<?>> argumentTypes = new ArrayList<Class<?>>();
    private List<Object> argumentValues = new ArrayList<Object>();

    public static DynamicallyGeneratedClientMethodInvoker aMethodNamed(String methodName) {
        return new DynamicallyGeneratedClientMethodInvoker(methodName);
    }

    private DynamicallyGeneratedClientMethodInvoker(String methodName) {
        this.methodName = methodName;
    }

    public DynamicallyGeneratedClientMethodInvoker withArgument(Class<?> type, Object value) {
        argumentTypes.add(type);
        argumentValues.add(value);
        return this;
    }

    public void invoke() {
        invoke(void.class);
    }

    public <T> T invoke(Class<T> returnType) {
        Class<?> clazz;
        try {
            clazz = generateClient();
            JavaClientHttpOperations ops = new RestTemplatePoweredHttpOperations("http://localhost:8080/test-webapp/ws");
            Object client = clazz.getConstructor(JavaClientHttpOperations.class).newInstance(ops);
            Object returnValue = clazz.getMethod(methodName, argumentTypes.toArray(new Class[argumentTypes.size()])).invoke(client, argumentValues.toArray(new Object[argumentValues.size()]));
            return returnType.cast(returnValue);
        } catch (CompilationFailedException e) {
            throw new RuntimeException("Problem invoking client method.", e);
        } catch (IOException e) {
            throw new RuntimeException("Problem invoking client method.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Problem invoking client method.", e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Problem invoking client method.", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Problem invoking client method.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Problem invoking client method.", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Problem invoking client method.", e);
        }
    }

    private Class<?> generateClient() throws CompilationFailedException, IOException, ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        // generate client by compiling test controller with annotation processor
        SimpleCompiler annotationProcessingCompiler = new SimpleCompiler();
        File generatedSourcesDirectory = new File(GENERATED_SOURCES_DIR);
        generatedSourcesDirectory.mkdirs();
        File outputClientFile = File.createTempFile("TestClient", ".java", generatedSourcesDirectory);
        outputClientFile.deleteOnExit();
        String inputControllerFilename = TEST_SOURCES_DIR;
        annotationProcessingCompiler.addAnnotationProcessor(new SpringControllerAnnotationProcessor());
        annotationProcessingCompiler.addAnnotationProcessorOption(SpringControllerAnnotationProcessor.OPTION_CLIENT_OUTPUT_FILE, outputClientFile.getAbsolutePath());
        annotationProcessingCompiler.compile(new File(inputControllerFilename));

        // client source should now be generated, compile the client source
        SimpleCompiler clientCompiler = new SimpleCompiler();
        File testClassesDirectory = new File(TEST_CLASSES_DIR);
        File classFile = clientCompiler.compile(outputClientFile, testClassesDirectory);
        classFile.deleteOnExit();

        ClassLoader loader = ClassLoader.getSystemClassLoader();
        return loader.loadClass(classFile.getName().replaceAll(".class", ""));
    }

    public DynamicallyGeneratedClientMethodInvoker withArguments(Class<?>[] types, Object[] values) {
        if (types.length != values.length) {
            throw new IllegalArgumentException("Array sizes of types and values must be equal.");
        }

        for (int i = 0; i < types.length; i++) {
            withArgument(types[i], values[i]);
        }
        return this;
    }
}
