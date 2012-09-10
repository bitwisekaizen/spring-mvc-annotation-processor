package com.thegrayfiles;

import java.util.List;

public interface MethodImplementationSourceGenerator {
    List<String> generate(ClientMethod stub);
}
