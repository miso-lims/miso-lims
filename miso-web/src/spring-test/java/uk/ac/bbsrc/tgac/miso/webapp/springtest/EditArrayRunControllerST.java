package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import java.util.Arrays;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import java.util.List;
import java.util.Map;
import com.jayway.jsonpath.JsonPath;
import static org.junit.Assert.assertEquals;

public class EditArrayRunControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/arrayrun";
  private static final Class<ArrayRun> entityClass = ArrayRun.class;

  @Test
  public void testNew() throws Exception {
    baseTestNewModel(CONTROLLER_BASE + "/new", "New Array Run");
  }

  @Test
  public void testEditPage() throws Exception {
    int id = 1;
    String objectAttribute = "arrayRunJson";
    String resultJson = baseTestEditModel(CONTROLLER_BASE + "/" + id).get(objectAttribute).toString();
    assertDbVsModelObjects(resultJson, id);
  }

  private void assertDbVsModelObjects(String resultJson, int id) {
    ArrayRun dbObject = currentSession().get(entityClass, id);
    assertEquals(dbObject.getId(), readLong(resultJson, "$.id"));
    assertEquals(dbObject.getAlias(), JsonPath.read(resultJson, "$.alias"));
  }

}
