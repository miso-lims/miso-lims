package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.Array;
import java.util.Arrays;
import java.util.List;
import com.jayway.jsonpath.JsonPath;
import static org.junit.Assert.assertEquals;

public class EditArrayControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/array";
  private static final Class<Array> entityClass = Array.class;

  private void assertDbVsModelObjects(String resultJson, int id) {
    Array dbObject = currentSession().get(entityClass, id);
    assertEquals(dbObject.getId(), readLong(resultJson, "$.id"));
    assertEquals(dbObject.getAlias(), JsonPath.read(resultJson, "$.alias"));
  }

  @Test
  public void testNew() throws Exception {
    baseTestNewModel(CONTROLLER_BASE + "/new", "New Array");
  }

  @Test
  public void testSetupForm() throws Exception {
    int id = 1;
    String objectAttribute = "arrayJson";
    String resultJson = testModelFormSetup(CONTROLLER_BASE + "/1", objectAttribute);
    assertDbVsModelObjects(resultJson, id);
  }

}
