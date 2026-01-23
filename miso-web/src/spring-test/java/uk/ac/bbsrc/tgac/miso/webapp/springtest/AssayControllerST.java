package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;

import java.util.Arrays;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import javax.ws.rs.core.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import static org.junit.Assert.*;
import com.jayway.jsonpath.JsonPath;

import java.util.List;
import static org.junit.Assert.assertEquals;


public class AssayControllerST extends AbstractST {
  private static final String CONTROLLER_BASE = "/assay";
  private static final Class<Assay> entityClass = Assay.class;


  private void assertDbVsModelObjects(String resultJson, List<Long> ids) {
    assertEquals(Integer.valueOf(ids.size()), JsonPath.read(resultJson, "$.length()"));

    for (int i = 0; i < ids.size(); i++) {
      Assay dbObject = currentSession().get(entityClass, ids.get(i));
      assertEquals(dbObject.getId(), readLong(resultJson, "$[" + i + "].id"));
      assertEquals(dbObject.getAlias(), JsonPath.read(resultJson, "$[" + i + "].alias"));
    }
  }

  @Test
  public void testList() throws Exception {

    List<Long> ids = Arrays.asList(1L, 2L, 3L, 4L);
    String responseJson = testStaticListPage(CONTROLLER_BASE + "/list", "data");
    assertDbVsModelObjects(responseJson, ids);
  }

  @Test
  public void testNew() throws Exception {
    baseTestNewModel(CONTROLLER_BASE + "/new", "New Assay");
  }

  @Test
  public void testCopy() throws Exception {
    int copiedId = 1;
    Assay base = currentSession().get(Assay.class, copiedId);

    String response =
        getMockMvc()
            .perform(get(CONTROLLER_BASE + "/new").accept(MediaType.APPLICATION_JSON).param("baseId",
                Integer.toString(copiedId)))
            .andExpect(status().isOk())
            .andExpect(model().attribute("title", "New Assay"))
            .andReturn().getModelAndView().getModel().get("assayDto").toString();

    assertEquals(base.getVersion() + " COPY", JsonPath.read(response, "$.version"));
  }

  @Test
  public void testEdit() throws Exception {
    baseTestEditModel(CONTROLLER_BASE + "/1");
  }

}
