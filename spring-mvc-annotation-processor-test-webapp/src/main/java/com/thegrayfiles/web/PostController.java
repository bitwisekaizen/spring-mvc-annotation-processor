package com.thegrayfiles.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PostController {

    @RequestMapping(value="/canPostWithoutParametersOrReturnValue", method = RequestMethod.POST)
    public @ResponseBody void canPostWithoutParametersOrReturnValue() {

    }
}
