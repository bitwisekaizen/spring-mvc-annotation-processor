package com.thegrayfiles.tests.acceptance;

import com.thegrayfiles.builders.DynamicallyGeneratedClientMethodInvoker;
import com.thegrayfiles.marshallable.TestEntity;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.thegrayfiles.builders.DynamicallyGeneratedClientMethodInvoker.aMethodNamed;
import static org.testng.Assert.assertEquals;

public class SimpleControllerTests {

    @Test
    public void canFetchResourceWithNoParameters() {
        TestEntity entity = canFetchResourceFromController();
        assertEquals(entity.getName(), "test", "Response entity name is incorrect.");
    }

    @Test
    public void canFetchResourceUsingParameter() {
        String entityName = "somecrazyname";
        TestEntity entity = canFetchResourceFromController(String.class, entityName);
        assertEquals(entity.getName(), entityName, "Response entity name is incorrect.");
    }

    @Test
    public void canFetchResourceUsingPathVariable() {
        String value = "pathvariablename";
        TestEntity entity = canFetchResourceFromController(String.class, value);
        assertEquals(entity.getName(), value, "Response entity name is incorrect.");
    }

    @Test
    public void canFetchResourceWithPathVariableAndRequestParameter() {
        TestEntity testEntity = canFetchResourceFromController(new Class<?>[]{String.class, String.class}, new Object[]{"pathVariableValue", "requestParameterValue"});
        assertEquals(testEntity.getRequestParameterValues().size(), 1);
        assertEquals(testEntity.getPathVariableValues().size(), 1);
        assertEquals(testEntity.getRequestParameterValues().get(0), "requestParameterValue");
        assertEquals(testEntity.getPathVariableValues().get(0), "pathVariableValue");
    }

    @Test
    public void clientGenerationPreservesControllerParameterOrdering() {
        TestEntity testEntity = canFetchResourceFromController(new Class<?>[]{String.class, String.class}, new Object[]{"requestParameterValue", "pathVariableValue"});
        assertEquals(testEntity.getRequestParameterValues().size(), 1);
        assertEquals(testEntity.getPathVariableValues().size(), 1);
        assertEquals(testEntity.getRequestParameterValues().get(0), "requestParameterValue");
        assertEquals(testEntity.getPathVariableValues().get(0), "pathVariableValue");
    }

    @Test
    public void getRequestMappingMethodShouldNotAffectAbilityToFetchResource() {
        canFetchResourceWithNoParameters();
    }

    @Test
    public void canPerformGetWithoutAnyResponse() {
        String randomString = UUID.randomUUID().toString();
        DynamicallyGeneratedClientMethodInvoker fetchRequestInvoker = aMethodNamed("canPerformGetWithoutAnyResponseFetchEntity");
        DynamicallyGeneratedClientMethodInvoker updateRequestInvoker = aMethodNamed("canPerformGetWithoutAnyResponseUpdateEntity");

        // perform a get that basically updates an entity
        updateRequestInvoker.withArgument(String.class, randomString).invoke();

        // perform a get that fetches the entity
        TestEntity updatedTestEntity = fetchRequestInvoker.invoke(TestEntity.class);
        assertEquals(updatedTestEntity.getName(), randomString);
    }


    private TestEntity canFetchResourceFromController(Class<?> type, Object value) {
        return canFetchResourceFromController(new Class<?>[]{type}, new Object[]{value});
    }

    private TestEntity canFetchResourceFromController() {
        return aMethodNamed(determineTestMethod()).invoke(TestEntity.class);
    }

    private TestEntity canFetchResourceFromController(Class<?>[] types, Object[] values) {
        return aMethodNamed(determineTestMethod()).withArguments(types, values).invoke(TestEntity.class);
    }

    private String determineTestMethod() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            // test methods will never have arguments in this context
            try {
                Test testAnnotation = Class.forName(className).getMethod(methodName).getAnnotation(Test.class);
                if (testAnnotation != null) {
                    return methodName;
                }
            } catch (NoSuchMethodException e) {
                // deliberatly ignore this exception
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Exception occurred attempting to determine test method name.", e);
            }
        }

        return null;
    }
}
