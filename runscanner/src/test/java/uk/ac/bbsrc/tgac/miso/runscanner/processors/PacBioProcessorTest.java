package uk.ac.bbsrc.tgac.miso.runscanner.processors;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.NotificationDto;
import uk.ac.bbsrc.tgac.miso.dto.PacBioNotificationDto;
import uk.ac.bbsrc.tgac.miso.runscanner.processors.DefaultPacBio.StatusResponse;

public class PacBioProcessorTest extends AbstractProcessorTest {
  private static class TestPacBio extends DefaultPacBio {
    private final Map<String, StatusResponse> responses;

    private TestPacBio(Map<String, StatusResponse> responses) {
      super(new Builder(PlatformType.PACBIO, "unittest", null), URL_PREFIX);
      this.responses = responses;
    }

    @Override
    protected StatusResponse getStatus(String url) {
      return responses.get(url.substring(URL_PREFIX.length()));
    }
  }

  private static final String URL_PREFIX = "http://example.com";

  public PacBioProcessorTest() {
    super(PacBioNotificationDto.class);
  }

  @Override
  protected NotificationDto process(File directory) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, StatusResponse> responses = mapper.readValue(new File(directory, "webrequests.json"),
        mapper.getTypeFactory().constructMapLikeType(HashMap.class, String.class, StatusResponse.class));
    return new TestPacBio(responses).process(directory, TimeZone.getDefault());
  }

  @Test
  public void testGoldens() throws IOException {
    checkDirectory("/pacbio");
  }

}