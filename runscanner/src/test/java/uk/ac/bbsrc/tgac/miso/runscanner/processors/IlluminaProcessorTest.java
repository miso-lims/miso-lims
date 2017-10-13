package uk.ac.bbsrc.tgac.miso.runscanner.processors;

import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.IlluminaNotificationDto;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.runscanner.RunProcessor.Builder;

public class IlluminaProcessorTest extends AbstractProcessorTest {
  private final DefaultIllumina instance = new DefaultIllumina(new Builder(PlatformType.ILLUMINA, "unittest", null), true);

  public IlluminaProcessorTest() {
    super(IlluminaNotificationDto.class);
  }

  @Override
  protected NotificationDto process(File directory) throws IOException {
    return instance.process(directory, TimeZone.getTimeZone("America/Toronto"));
  }

  @Test
  public void testGoldens() throws IOException {
    if (!System.getProperty("skipIllumina", "true").equals("true")) {
      checkDirectory("/illumina");
    }
  }

}
