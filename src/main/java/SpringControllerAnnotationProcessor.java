import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpringControllerAnnotationProcessor {

    private File file;
    private List<ClientStub> stubs = new ArrayList<ClientStub>();
    private SourceGenerator sourceGenerator;

    public SpringControllerAnnotationProcessor(SourceGenerator sourceGenerator, File file) {
        this.sourceGenerator = sourceGenerator;
        this.file = file;
    }

    public void process() {
        try {
            List<String> fileContents = new ArrayList<String>();
            fileContents.add("public class " + file.getName().replaceFirst("(.$?)\\.java", "$1") + " {");

            for (ClientStub stub : stubs) {
                // generate signature and method contents
                MethodSignature signature = stub.getMethodSignature();
                fileContents.add("public " + signature.getReturnType().toString() + " " + signature.getMethodName() + "() { ");
                fileContents.add(sourceGenerator.generate(stub));
                fileContents.add("}");
            }

            fileContents.add("}");
            FileUtils.writeLines(file, fileContents);
        } catch (IOException e) {
            // throw runtime exception for now, but have more explicit exception soon
            throw new RuntimeException("IOException occurred when writing to the file.", e);
        }
    }

    public void addStub(ClientStub stub) {
        stubs.add(stub);
    }
}
