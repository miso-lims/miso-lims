package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import java.util.Arrays;
import java.util.List;
import com.jayway.jsonpath.JsonPath;
import static org.junit.Assert.assertEquals;

public class DetailedQcStatusControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/detailedqcstatus";
  private static final Class<DetailedQcStatusImpl> entityClass = DetailedQcStatusImpl.class;

  private void assertDbVsModelObjects(String resultJson, List<Long> ids) {
    assertEquals(Integer.valueOf(ids.size()), JsonPath.read(resultJson, "$.length()"));

    for (int i = 0; i < ids.size(); i++) {
      DetailedQcStatusImpl dbObject = currentSession().get(entityClass, ids.get(i));
      assertEquals(dbObject.getId(), readLong(resultJson, "$[" + i + "].id"));
      assertEquals(dbObject.getDescription(), JsonPath.read(resultJson, "$[" + i + "].description"));
    }
  }

  @Test
  public void testList() throws Exception {
    List<Long> ids = Arrays.asList(1L, 2L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

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
