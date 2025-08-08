package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import java.util.Arrays;

public class EditBoxControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/box";

  @Test
  public void testBulkCreate() throws Exception {
    testModelBulkCreate(CONTROLLER_BASE + "/bulk/new", 3, "input");
  }

  @Test
  public void testBulkEdit() throws Exception {
    testModelBulkEdit(CONTROLLER_BASE + "/bulk/edit", "1,2", "input",
        Arrays.asList(Arrays.asList("id", "1", "name", "BOX1"), Arrays.asList("id", "2", "name", "BOX2")));
  }
}
