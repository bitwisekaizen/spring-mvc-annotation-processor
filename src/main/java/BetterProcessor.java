import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BetterProcessor {

    private File file;
    private List<ClientStub> stubs = new ArrayList<ClientStub>();

    public BetterProcessor(File file) {
        this.file = file;
    }

    public void process() {
        try {
            StringBuilder fileContents = new StringBuilder();
            fileContents.append("public class " + file.getName().replaceFirst("(.$?)\\.java", "$1") + " {");

            for (ClientStub stub : stubs) {
                fileContents.append(stub);
            }

            fileContents.append("}");
            FileUtils.writeStringToFile(file, fileContents.toString());
        } catch (IOException e) {
            // throw runtime exception for now, but have more explicit exception soon
            throw new RuntimeException("IOException occurred when writing to the file.", e);
        }
    }

    public void addStub(ClientStub stub) {
        stubs.add(stub);
    }
}
