public class ClientStub {

    private MethodSignature signature;
    private MethodRequestMapping requestMapping;

    public ClientStub(MethodSignature signature, MethodRequestMapping requestMapping) {
        this.signature = signature;
        this.requestMapping = requestMapping;
    }

    @Override
    public String toString() {
        return "public " + signature.getReturnType().toString() + " " + signature.getMethodName() + "() { }";
    }
}
