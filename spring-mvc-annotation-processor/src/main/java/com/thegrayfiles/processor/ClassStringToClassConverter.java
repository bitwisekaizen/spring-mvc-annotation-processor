package com.thegrayfiles.processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ClassStringToClassConverter {

    public static final Set<String> SUPPORTED_PRIMITIVE_STRINGS;
    public static final Collection<Class<?>> SUPPORTED_PRIMITIVE_CLASSES;

    private static final Map<String, Class<?>> primitiveToClassMap = new HashMap<String, Class<?>>();

    static {
        primitiveToClassMap.put("int", int.class);
        primitiveToClassMap.put("float", float.class);
        primitiveToClassMap.put("double", double.class);
        primitiveToClassMap.put("long", long.class);
        primitiveToClassMap.put("char", char.class);
        primitiveToClassMap.put("short", short.class);
        primitiveToClassMap.put("byte", byte.class);
        primitiveToClassMap.put("boolean", boolean.class);
        primitiveToClassMap.put("void", void.class);
        SUPPORTED_PRIMITIVE_STRINGS = primitiveToClassMap.keySet();
        SUPPORTED_PRIMITIVE_CLASSES = primitiveToClassMap.values();
    }

    public Class<?> convert(String primitiveClass) throws ClassNotFoundException {
        return primitiveToClassMap.containsKey(primitiveClass) ? primitiveToClassMap.get(primitiveClass) : Class.forName(primitiveClass);
    }
}
