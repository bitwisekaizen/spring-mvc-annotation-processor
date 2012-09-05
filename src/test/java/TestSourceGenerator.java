public class TestSourceGenerator implements SourceGenerator {

    public String generate(ClientStub stub) {
        Class<?> returnType = stub.getMethodSignature().getReturnType();
        return returnType.equals(void.class) ? "" : "unsupported";
    }
}
