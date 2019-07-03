package uk.ac.bbsrc.tgac.miso.migration.destination;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.core.service.SampleService;

public class MisoServiceManagerTest {

  @Test
  public void testBuildWithDefaults() throws IOException {
    MisoServiceManager sut = MisoServiceManager.buildWithDefaults(null, null, new UserImpl());
    assertNotNull(sut);
    SampleService sampleService = sut.getSampleService();
    assertNotNull(sampleService);
    assertNotNull(sampleService.getAuthorizationManager());
    assertNotNull(sampleService.getDeletionStore());
  }

}
