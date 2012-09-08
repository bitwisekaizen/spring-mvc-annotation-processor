package com.thegrayfiles;

import java.util.List;

public interface SourceGenerator {
    List<String> generate(ClientStub stub);
}
