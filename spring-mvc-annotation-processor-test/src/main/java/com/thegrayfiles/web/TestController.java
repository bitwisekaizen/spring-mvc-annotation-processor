package com.thegrayfiles.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    public static final String TEST_REQUEST_MAPPING = "/test";

    @RequestMapping(value=TEST_REQUEST_MAPPING)
    public @ResponseBody String simple() {
        liquibase.integration.spring.SpringLiquibase li;
        org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean dooo;
        org.springframework.jdbc.datasource.DriverManagerDataSource moooo;
        org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter moo;
        return "test";

    }
}
