package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;

import java.util.Arrays;
import java.util.List;
import com.jayway.jsonpath.JsonPath;
import static org.junit.Assert.assertEquals;



public class ArrayModelControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/arraymodel";
  private static final Class<ArrayModel> entityClass = ArrayModel.class;

  private void assertDbVsModelObjects(String resultJson, List<Long> ids) {
    assertEquals(Integer.valueOf(ids.size()), JsonPath.read(resultJson, "$.length()"));

    for (int i = 0; i < ids.size(); i++) {
      ArrayModel dbObject = currentSession().get(entityClass, ids.get(i));
      assertEquals(dbObject.getId(), ((Integer) JsonPath.read(resultJson, "$[" + i + "].id")).longValue());
      assertEquals(dbObject.getAlias(), JsonPath.read(resultJson, "$[" + i + "].alias"));
    }
  }

  @Test
  public void testList() throws Exception {
    List<Long> ids = Arrays.asList(1L, 2L);

    assertDbVsModelObjects(testStaticListPage(CONTROLLER_BASE + "/list", "data"), ids);
  }

  @Test
  public void testBulkCreate() throws Exception {
    testBulkCreatePage(CONTROLLER_BASE + "/bulk/new", 3, "input");
  }

  @Test
  public void testBulkEdit() throws Exception {
    List<Long> ids = Arrays.asList(1L, 2L);

    assertDbVsModelObjects(testBulkEditPage(CONTROLLER_BASE + "/bulk/edit", ids, "input"), ids);
  }
}
