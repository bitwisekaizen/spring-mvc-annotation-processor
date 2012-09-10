package com.thegrayfiles.generator;

import com.thegrayfiles.client.ClientMethod;

import java.util.List;

public interface MethodImplementationSourceGenerator {
    List<String> generate(ClientMethod stub);
}
