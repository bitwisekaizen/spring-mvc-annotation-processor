package com.thegrayfiles.processor;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ClassStringToClassConverterTests {

    private ClassStringToClassConverter converter;

    @BeforeMethod
    public void setup() {
        converter = new ClassStringToClassConverter();
    }

    @Test
    public void canConvertPrimitiveStrings() throws ClassNotFoundException {
        for (String clazz : ClassStringToClassConverter.SUPPORTED_PRIMITIVE_STRINGS) {
            // as long as this doesn't throw, then the test passes
            converter.convert(clazz);
        }
    }

    @Test
    public void canConvertNonPrimitiveStrings() throws ClassNotFoundException {
        converter.convert("java.lang.Object");
        converter.convert("java.lang.Number");
        converter.convert("java.lang.String");
    }

    @Test(expectedExceptions = ClassNotFoundException.class)
    public void conversionFailsIfClassNotFound() throws ClassNotFoundException {
        converter.convert("moo");
    }
}
