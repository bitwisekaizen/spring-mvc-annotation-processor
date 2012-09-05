public class ClientStub {

    private MethodSignature signature;
    private MethodRequestMapping requestMapping;

    public ClientStub(MethodSignature signature, MethodRequestMapping requestMapping) {
        this.signature = signature;
        this.requestMapping = requestMapping;
    }

    public String generate(ClientGenerator generator) {
        return  "public " + signature.getReturnType().toString() + " " + signature.getMethodName() + "() { " + generator.generate(this) + " }";
    }
}
