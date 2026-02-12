package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProbeSet;

import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.List;

public class ProbeSetControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/probeset";

  private void assertDbVsModelObjects(String resultJson, List<Long> ids) {
    assertEquals(Integer.valueOf(ids.size()), JsonPath.read(resultJson, "$.length()"));

    for (int i = 0; i < ids.size(); i++) {
      ProbeSet dbObject = currentSession().get(ProbeSet.class, ids.get(i));
      assertEquals(dbObject.getId(), readLong(resultJson, "$[" + i + "].id"));
      assertEquals(dbObject.getName(), JsonPath.read(resultJson, "$[" + i + "].name"));
    }
  }

  @Test
  public void testList() throws Exception {
    List<Long> ids = Arrays.asList(1L, 2L);
    String responseJson = testStaticListPage(CONTROLLER_BASE + "/list", "data");
    assertDbVsModelObjects(responseJson, ids);
  }

  @Test
  public void testCreate() throws Exception {
    baseTestNewModel(CONTROLLER_BASE + "/new", "New Probe Set");
  }

  @Test
  public void testEdit() throws Exception {
    baseTestEditModel(CONTROLLER_BASE + "/1");
  }

  @Test
  public void testBulkEditProbes() throws Exception {
    String url = CONTROLLER_BASE + "/1/probes";
    getMockMvc().perform(get(url).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
