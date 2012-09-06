public class TestSourceGenerator implements SourceGenerator {

    public String generate(ClientStub stub) {

        Class<?> returnType = stub.getMethodSignature().getReturnType();
        if (returnType.equals(void.class)) {
            return "";
        } else if (returnType.isPrimitive()) {
            return "return 0;";
        }
        return "return null;";
    }
}
