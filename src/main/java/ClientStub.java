public class ClientStub {

    private MethodSignature signature;
    private MethodRequestMapping requestMapping;

    public ClientStub(MethodSignature signature, MethodRequestMapping requestMapping) {
        this.signature = signature;
        this.requestMapping = requestMapping;
    }

    public MethodSignature getMethodSignature() {
        return signature;
    }
}
