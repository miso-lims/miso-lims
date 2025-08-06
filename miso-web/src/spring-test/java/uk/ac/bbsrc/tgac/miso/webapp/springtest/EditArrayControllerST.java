package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import java.util.Arrays;

public class EditArrayControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/array";

  @Test
  public void testNew() throws Exception {
    baseTestNewModel(CONTROLLER_BASE + "/new", "New Array");
  }

  @Test
  public void testSetupForm() throws Exception {
    int id = 1;
    String objectAttribute = "arrayJson";

    testModelFormSetup(CONTROLLER_BASE + "/1", id, objectAttribute,
        Arrays.asList("id", "1", "alias", "Array_1", "description", "test array"));
  }

}
