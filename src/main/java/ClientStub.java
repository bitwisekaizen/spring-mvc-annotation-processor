import java.util.ArrayList;
import java.util.List;

public class ClientStub {

    private MethodSignature signature;
    private MethodRequestMapping requestMapping;
    private List<RequestParameter> requestParameters = new ArrayList<RequestParameter>();

    public ClientStub(MethodSignature signature, MethodRequestMapping requestMapping) {
        this.signature = signature;
        this.requestMapping = requestMapping;
    }

    public MethodSignature getMethodSignature() {
        return signature;
    }

    public void addRequestParameter(RequestParameter parameter) {
        requestParameters.add(parameter);
    }

    public List<RequestParameter> getRequestParameters() {
        return requestParameters;
    }
}
