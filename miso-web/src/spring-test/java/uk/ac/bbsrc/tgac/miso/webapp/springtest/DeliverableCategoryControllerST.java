package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DeliverableCategory;

import java.util.Arrays;
import java.util.List;
import com.jayway.jsonpath.JsonPath;
import static org.junit.Assert.assertEquals;

public class DeliverableCategoryControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/deliverablecategory";
   private static final Class<DeliverableCategory> entityClass = DeliverableCategory.class;
   
  private void assertDbVsModelObjects(String resultJson, List<Long> ids) {
    assertEquals(Integer.valueOf(ids.size()), JsonPath.read(resultJson, "$.length()"));

    for (int i = 0; i < ids.size(); i++) {
      DeliverableCategory dbObject = currentSession().get(entityClass, ids.get(i));
      assertEquals(dbObject.getId(), readLong(resultJson, "$[" + i + "].id"));
      assertEquals(dbObject.getName(), JsonPath.read(resultJson, "$[" + i + "].name"));
    }
  }

  @Test
  public void testList() throws Exception {
    List<Long> ids = Arrays.asList(2L, 1L, 3L);

    String resultJson = testStaticListPage(CONTROLLER_BASE + "/list", "data");
    assertDbVsModelObjects(resultJson, ids);
  }

  @Test
  public void testBulkCreate() throws Exception {
    testBulkCreatePage(CONTROLLER_BASE + "/bulk/new", 3, "input");
  }

  @Test
  public void testBulkEdit() throws Exception {
    List<Long> ids = Arrays.asList(1L, 2L);
    String resultJson = testBulkEditPage(CONTROLLER_BASE + "/bulk/edit", ids, "input");
    assertDbVsModelObjects(resultJson, ids);
  }
  
}