package com.thegrayfiles.tests.acceptance;

import com.thegrayfiles.builders.DynamicallyGeneratedClientMethodInvoker;
import org.testng.annotations.Test;

import static com.thegrayfiles.builders.DynamicallyGeneratedClientMethodInvoker.aMethodNamed;

public class PostControllerTests {

    @Test
    public void canPostWithoutParametersOrReturnValue() {
        DynamicallyGeneratedClientMethodInvoker invoker = aMethodNamed("canPostWithoutParametersOrReturnValue");
        invoker.invoke();
    }
}
