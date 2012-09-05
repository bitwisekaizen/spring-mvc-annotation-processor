public class MethodSignature {

    private Class<?> returnType;
    private String methodName;

    public MethodSignature(Class<?> returnType, String methodName) {
        this.returnType = returnType;
        this.methodName = methodName;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public String getMethodName() {
        return methodName;
    }
}