package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import java.util.Arrays;

public class EditArrayRunControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/arrayrun";

  @Test
  public void testNew() throws Exception {
    baseTestNewModel(CONTROLLER_BASE + "/new", "New Array Run");
  }

  @Test
  public void testSetupForm() throws Exception {
    int id = 1;
    String objectAttribute = "arrayRunJson";

    testModelFormSetup(CONTROLLER_BASE + "/1", id, objectAttribute,
        Arrays.asList("id", "1", "alias", "ArrayRun_1", "health", "Running"));
  }

}
