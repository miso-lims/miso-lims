package uk.ac.bbsrc.tgac.miso.runscanner.processors;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.runscanner.RunProcessor;

public abstract class AbstractProcessorTest {
  private final Class<? extends NotificationDto> clazz;

  public AbstractProcessorTest(Class<? extends NotificationDto> clazz) {
    super();
    this.clazz = clazz;
  }

  final protected void checkDirectory(String root) throws JsonParseException, JsonMappingException, IOException {
    ObjectMapper mapper = RunProcessor.createObjectMapper();
    for (File directory : new File(this.getClass().getResource(root).getPath()).listFiles()) {
      NotificationDto result = process(directory);
      NotificationDto reference = mapper.readValue(new File(directory, "reference.json"), clazz);
      result.setSequencerFolderPath(null); // We delete this because it is going to be different in each environment.
      result.setMetrics(null); // We delete these because changes in metrics are non-critical.
      assertEquals(result, reference);
    }
  }

  protected abstract NotificationDto process(File directory) throws IOException;
}
