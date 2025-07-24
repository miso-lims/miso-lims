package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import java.util.Arrays;

public class BarcodableSearchRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/barcodables";

  @Test
  public void testSearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/search", "11111", Arrays.asList(1));
  }
}