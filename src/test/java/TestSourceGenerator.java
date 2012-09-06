public class TestSourceGenerator implements SourceGenerator {

    public String generate(ClientStub stub) {

        Class<?> returnType = stub.getMethodSignature().getReturnType();
        if (returnType.isPrimitive()) {
            return "return 0;";
        } else if (returnType.equals(void.class)) {
            return "";
        }
        return "return null;";
    }
}
