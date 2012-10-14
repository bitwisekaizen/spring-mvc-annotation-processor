package com.thegrayfiles.web;

import com.thegrayfiles.marshallable.TestEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    public static final String TEST_REQUEST_MAPPING = "/test";

    @RequestMapping(value=TEST_REQUEST_MAPPING)
    public @ResponseBody TestEntity get() {
        return new TestEntity("test");
    }

    @RequestMapping(value=TEST_REQUEST_MAPPING)
    public @ResponseBody TestEntity canFetchResourceUsingParameter(@RequestParam String name) {
        return new TestEntity(name);
    }
}
