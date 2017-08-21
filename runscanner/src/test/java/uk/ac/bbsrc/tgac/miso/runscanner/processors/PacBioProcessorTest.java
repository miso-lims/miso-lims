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
    private final Map<String, StatusResponse> statusResponses;
    private final Map<String, String> sampleSheetResponses;

    private TestPacBio(Map<String, StatusResponse> statusResponses, Map<String, String> sampleSheetResponses) {
      super(new Builder(PlatformType.PACBIO, "unittest", null), URL_PREFIX);
      this.statusResponses = statusResponses;
      this.sampleSheetResponses = sampleSheetResponses;
    }

    @Override
    protected StatusResponse getStatus(String url) {
      return statusResponses.get(url.substring(URL_PREFIX.length()));
    }

    @Override
    protected String getSampleSheet(String url) {
      return sampleSheetResponses.get(url.substring(URL_PREFIX.length()));
    }
  }

  private static final String URL_PREFIX = "http://example.com";

  public PacBioProcessorTest() {
    super(PacBioNotificationDto.class);
  }

  @Override
  protected NotificationDto process(File directory) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, StatusResponse> statusResponses = mapper.readValue(new File(directory, "webrequests-status.json"),
        mapper.getTypeFactory().constructMapLikeType(HashMap.class, String.class, StatusResponse.class));
    Map<String, String> sampleSheetResponses = mapper.readValue(new File(directory, "webrequests-samplesheet.json"),
        mapper.getTypeFactory().constructMapLikeType(HashMap.class, String.class, String.class));
    return new TestPacBio(statusResponses, sampleSheetResponses).process(directory, TimeZone.getDefault());
  }

  @Test
  public void testGoldens() throws IOException {
    checkDirectory("/pacbio");
  }

}