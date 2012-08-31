import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Test
public class AnnotationProcessorTests {

    @Test
    void canProcessRequestMappingWithBetterProcessor() throws IOException {
        File clientFile = File.createTempFile("ClientStub", "java");
        clientFile.deleteOnExit();
        Assert.assertEquals(FileUtils.sizeOf(clientFile), 0, "Non-zero initial file size.");

        BetterProcessor processor = new BetterProcessor(clientFile);
        ProcessorRequestMapping processorRequestMapping = new ProcessorRequestMapping();
        processor.addRequestMapping(processorRequestMapping);

        // this should produce the client stub...
        processor.process();

        // file size should have increased
        Assert.assertTrue(FileUtils.sizeOf(clientFile) != 0, "Client file size did not increase.");
    }

    private class ProcessorRequestMapping {

    }

    private class ProcessorAdapter {

    }

    private class BetterProcessor {

        public BetterProcessor(File file) {
            //To change body of created methods use File | Settings | File Templates.
        }

        public void addRequestMapping(ProcessorRequestMapping processorRequestMapping) {
            //To change body of created methods use File | Settings | File Templates.
        }

        public void process() {
            //To change body of created methods use File | Settings | File Templates.
        }
    }

    public interface MockElement extends Element {

    }


    //@Test
    void canProcessMethodLevelRequestMappingInSingleRound() {
        MyProcessor processor = new MyProcessor();
        RoundEnvironment round = mock(RoundEnvironment.class);
        Element element = mock(Element.class);

        // create single request mapping annotation to be passed
        Set elements = Collections.emptySet();
        elements.add(element);
        when(round.getElementsAnnotatedWith(RequestMapping.class)).thenReturn(elements);

        // do the processing
        Set<? extends TypeElement> processorWillIgnoreThisArgument = null;
        processor.process(processorWillIgnoreThisArgument, round);

        // assert that process request mapping is called in the underlying bean
    }



    private class MyProcessor extends AbstractProcessor {
        @Override
        public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
            return false;
        }
    }
}
