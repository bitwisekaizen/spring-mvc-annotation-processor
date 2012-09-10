package com.thegrayfiles.util;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TestController {

    public static final String TEST_REQUEST_MAPPING = "/test";

    @RequestMapping(value=TEST_REQUEST_MAPPING)
    public Integer simple() {
        return 1;
    }
}
