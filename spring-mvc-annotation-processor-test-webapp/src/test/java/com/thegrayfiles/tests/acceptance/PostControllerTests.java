package com.thegrayfiles.tests.acceptance;

import com.thegrayfiles.builders.DynamicallyGeneratedClientMethodInvoker;
import com.thegrayfiles.marshallable.TestEntity;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.thegrayfiles.builders.DynamicallyGeneratedClientMethodInvoker.aMethodNamed;
import static org.testng.Assert.assertEquals;

public class PostControllerTests {

    @Test
    public void canPostWithoutParametersOrReturnValue() {
        DynamicallyGeneratedClientMethodInvoker invoker = aMethodNamed("canPostWithoutParametersOrReturnValue");
        invoker.invoke();
        // nothing to assert at the moment besides no exception thrown
    }

    @Test
    public void canPostWithRequestBody() {
        String testEntityName = UUID.randomUUID().toString();
        TestEntity testEntityToPost = new TestEntity(testEntityName);
        DynamicallyGeneratedClientMethodInvoker postInvoker = aMethodNamed("canPostWithRequestBody").withArgument(TestEntity.class, testEntityToPost);
        TestEntity postedTestEntity = postInvoker.invoke(TestEntity.class);
        assertEquals(postedTestEntity.getName(), testEntityName);
    }

    @Test
    public void canPostWithRequestBodyAndRequestParam() {
        String testEntityName = UUID.randomUUID().toString();
        String requestParam = "something";
        TestEntity testEntityToPost = new TestEntity(testEntityName);
        DynamicallyGeneratedClientMethodInvoker postInvoker = aMethodNamed("canPostWithRequestBodyAndRequestParam").withArgument(TestEntity.class, testEntityToPost).withArgument(String.class, requestParam);
        TestEntity postedTestEntity = postInvoker.invoke(TestEntity.class);
        assertEquals(postedTestEntity.getName(), testEntityName);
        assertEquals(postedTestEntity.getRequestParameterValues().size(), 1);
        assertEquals(postedTestEntity.getRequestParameterValues().get(0), requestParam);
    }
}
