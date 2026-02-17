package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasKey;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import com.jayway.jsonpath.JsonPath;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;

public class SopControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/sop";
  private static final Class<Sop> entityClass = Sop.class;

  @Test
  public void testList() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/list"))
        .andExpect(status().isOk())
        .andExpect(view().name("/WEB-INF/pages/listTabbed.jsp"))
        .andExpect(model().attribute("title", "SOPs"))
        .andExpect(model().attribute("targetType", "ListTarget.sop"))
        .andExpect(model().attribute("property", "category"))
        .andExpect(model().attribute("tabs", hasKey(SopCategory.SAMPLE.getLabel())))
        .andExpect(model().attribute("tabs", hasKey(SopCategory.LIBRARY.getLabel())))
        .andExpect(model().attribute("tabs", hasKey(SopCategory.RUN.getLabel())))
        .andExpect(model().attributeExists("config"));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testNew() throws Exception {
    baseTestNewModel(CONTROLLER_BASE + "/new", "New SOP");
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCopy() throws Exception {
    long sourceId = 1L;
    Sop base = currentSession().get(entityClass, sourceId);
    assertNotNull("Missing SOP test data with ID " + sourceId, base);

    String response = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/new")
            .param("baseId", Long.toString(sourceId))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(model().attribute("title", "New SOP"))
        .andReturn().getModelAndView().getModel().get("sopDto").toString();

    assertEquals(0L, readLong(response, "$.id"));
    assertEquals(base.getAlias(), JsonPath.read(response, "$.alias"));
    assertEquals(base.getVersion(), JsonPath.read(response, "$.version"));

    // If fields exist, IDs must be reset for create-mode copy
    if (JsonPath.read(response, "$.fields") != null) {
      int size = JsonPath.read(response, "$.fields.length()");
      for (int i = 0; i < size; i++) {
        assertEquals(0, (int) JsonPath.read(response, "$.fields[" + i + "].id"));
      }
    }
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testEdit() throws Exception {
    long id = 1L;
    Sop db = currentSession().get(entityClass, id);
    assertNotNull("Missing SOP test data with ID " + id, db);

    Map<String, Object> model = baseTestEditModel(CONTROLLER_BASE + "/" + id);
    assertEquals("SOP " + id, model.get("title"));
    assertNotNull(model.get("sopDto"));

    String sopDto = model.get("sopDto").toString();
    assertEquals(id, readLong(sopDto, "$.id"));
    assertEquals(db.getAlias(), JsonPath.read(sopDto, "$.alias"));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testEditNotFound() throws Exception {
    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/999999"))
        .andExpect(status().isNotFound());
  }

  /**
   * NOTE: In ST context, AuthorizationManager.throwIfNonAdmin() is not translated into a
   * 403/redirect. It bubbles up and becomes a 500. So for "forbidden" tests we assert 5xx + exception
   * present.
   */
  @Test
  public void testNewForbiddenForAnonymous() throws Exception {
    Exception ex = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/new"))
        .andExpect(status().is5xxServerError())
        .andReturn()
        .getResolvedException();

    assertNotNull("Expected authorization exception", ex);
  }

  @Test
  @WithMockUser(username = "user", password = "user", roles = {"INTERNAL"})
  public void testNewForbiddenForNonAdminUser() throws Exception {
    Exception ex = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/new"))
        .andExpect(status().is5xxServerError())
        .andReturn()
        .getResolvedException();

    assertNotNull("Expected authorization exception", ex);
  }

  @Test
  @WithMockUser(username = "user", password = "user", roles = {"INTERNAL"})
  public void testEditForbiddenForNonAdminUser() throws Exception {
    Exception ex = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/1"))
        .andExpect(status().is5xxServerError())
        .andReturn()
        .getResolvedException();

    assertNotNull("Expected authorization exception", ex);
  }

}
