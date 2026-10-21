package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import uk.ac.bbsrc.tgac.miso.core.data.impl.samplesheet.SampleSheet;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleSheetControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/samplesheet";

  @Test
  public void testList() throws Exception {

    List<Long> ids = Arrays.asList(1L, 2L, 3L);
    String responseJson = testStaticListPage(CONTROLLER_BASE + "/list", "data");
    assertDbVsModelObjects(responseJson, ids);
  }

  private void assertDbVsModelObjects(String resultJson, List<Long> ids) {
    assertEquals(Integer.valueOf(ids.size()), JsonPath.read(resultJson, "$.length()"));

    for (int i = 0; i < ids.size(); i++) {
      SampleSheet dbObject = currentSession().find(SampleSheet.class, ids.get(i));
      assertEquals(dbObject.getId(), readLong(resultJson, "$[" + i + "].id"));
      assertEquals(dbObject.getName(), JsonPath.read(resultJson, "$[" + i + "].name"));
    }
  }

  @Test
  public void testNew() throws Exception {
    baseTestNewModel(CONTROLLER_BASE + "/new", "New Sample Sheet");
  }

  @Test
  public void testEdit() throws Exception {
    baseTestEditModel(CONTROLLER_BASE + "/3");
  }
}
