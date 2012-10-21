package com.thegrayfiles.web;

import com.thegrayfiles.marshallable.TestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SimpleController {

    @RequestMapping(value="/canFetchResourceWithNoParameters")
    public @ResponseBody TestEntity canFetchResourceWithNoParameters() {
        return new TestEntity("test");
    }

    @RequestMapping(value="/canFetchResourceUsingParameter")
    public @ResponseBody TestEntity canFetchResourceUsingParameter(@RequestParam String name) {
        return new TestEntity(name);
    }

    @RequestMapping(value="/canFetchResourceUsingPathVariable/{name}")
    public @ResponseBody TestEntity canFetchResourceUsingPathVariable(@PathVariable String name) {
        return new TestEntity(name);
    }

    @RequestMapping(value="/getRequestMappingMethodShouldNotAffectAbilityToFetchResource", method = RequestMethod.GET)
    public TestEntity getRequestMappingMethodShouldNotAffectAbilityToFetchResource() {
        return canFetchResourceWithNoParameters();
    }

    @RequestMapping(value="/canFetchResourceWithPathVariableAndRequestParameter/{pathVariable}")
    public TestEntity canFetchResourceWithPathVariableAndRequestParameter(@PathVariable String pathVariable, @RequestParam String requestParam) {
        TestEntity testEntity = new TestEntity("name");
        testEntity.addPathVariableValue(pathVariable);
        testEntity.addRequestParameterValue(requestParam);
        return testEntity;
    }

    @RequestMapping(value="/clientGenerationPreservesControllerParameterOrdering/{pathVariable}")
    public TestEntity clientGenerationPreservesControllerParameterOrdering(@RequestParam String requestParam, @PathVariable String pathVariable) {
        TestEntity testEntity = new TestEntity("name");
        testEntity.addPathVariableValue(pathVariable);
        testEntity.addRequestParameterValue(requestParam);
        return testEntity;
    }

    private TestEntity sharedEntity = new TestEntity("someName");;

    @RequestMapping(value="/canPerformGetWithoutAnyResponseFetchEntity")
    public TestEntity canPerformGetWithoutAnyResponseFetchEntity() {
        return sharedEntity;
    }

    @RequestMapping(value="/canPerformGetWithoutAnyResponseUpdateEntity")
    public @ResponseBody void canPerformGetWithoutAnyResponseUpdateEntity(@RequestParam String name) {
        sharedEntity.setName(name);
    }
}
