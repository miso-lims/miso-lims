package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

public class BarcodableSearchRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/barcodables";

  @Test
  public void testSearch() throws Exception {
    baseSearchByTermWithExpectedNumResults(CONTROLLER_BASE + "/search", "BOX", 6);
  }
}
