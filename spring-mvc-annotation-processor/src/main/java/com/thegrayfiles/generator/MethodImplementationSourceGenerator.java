package com.thegrayfiles.generator;

import com.thegrayfiles.server.ServerEndpoint;

import java.util.List;

public interface MethodImplementationSourceGenerator {
    List<String> generate(ServerEndpoint endpoint);
}
