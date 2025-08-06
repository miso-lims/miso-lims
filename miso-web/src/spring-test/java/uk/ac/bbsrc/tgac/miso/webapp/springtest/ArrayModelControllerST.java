package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import java.util.Arrays;

public class ArrayModelControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/arraymodel";

  @Test
  public void testList() throws Exception {
    testModelList(CONTROLLER_BASE + "/list", "data", Arrays
        .asList(Arrays.asList("id", "1", "alias", "Test BeadChip"), Arrays.asList("id", "2", "alias", "Unused")));
  }

  @Test
  public void testBulkCreate() throws Exception {
    testModelBulkCreate(CONTROLLER_BASE + "/bulk/new", 3, "input");
  }

  @Test
  public void testBulkEdit() throws Exception {
    testModelBulkEdit(CONTROLLER_BASE + "/bulk/edit", "1,2", "input",
        Arrays.asList(Arrays.asList("id", "1", "alias", "Test BeadChip"), Arrays.asList("id", "2", "alias", "Unused")));
  }
}
