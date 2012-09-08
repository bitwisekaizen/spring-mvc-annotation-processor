import java.util.ArrayList;
import java.util.List;

public class TestSourceGenerator implements SourceGenerator {

    public List<String> generate(ClientStub stub) {
        List<String> source = new ArrayList<String>();
        Class<?> returnType = stub.getMethodSignature().getReturnType();
        for (RequestParameter requestParameter : stub.getRequestParameters()) {
            source.add(requestParameter.getName() + ".toString();");
        }
        for (PathVariable pathVariable : stub.getPathVariables()) {
            source.add(pathVariable.getName() + ".toString();");
        }
        if (returnType.equals(void.class)) {
            return source;
        } else if (returnType.isPrimitive()) {
            source.add("return 0;");
            return source;
        }

        source.add("return null;");
        return source;
    }
}
