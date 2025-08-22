package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AttachmentCategory;

import java.util.Arrays;
import java.util.List;
import com.jayway.jsonpath.JsonPath;
import static org.junit.Assert.assertEquals;



public class AttachmentCategoryControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/attachmentcategories";
  private static final Class<AttachmentCategory> entityClass = AttachmentCategory.class;

  private void assertDbVsModelObjects(String resultJson, List<Long> ids) {
    assertEquals(Integer.valueOf(ids.size()), JsonPath.read(resultJson, "$.length()"));

    for (int i = 0; i < ids.size(); i++) {
      AttachmentCategory dbObject = currentSession().get(entityClass, ids.get(i));
      assertEquals(dbObject.getId(), readLong(resultJson, "$[" + i + "].id"));
      assertEquals(dbObject.getAlias(), JsonPath.read(resultJson, "$[" + i + "].alias"));
    }
  }

  @Test
  public void testList() throws Exception {
    // sorted alphabetically by alias
    List<Long> ids = Arrays.asList(3L, 5L, 1L, 2L, 4L);

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
