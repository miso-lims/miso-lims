package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.servlet.ModelAndView;

import com.jayway.jsonpath.JsonPath;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;

public class SopControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/sop";

  @Test
  public void testListRendersTabbedList() throws Exception {
    ModelAndView mv = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/list").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assertNotNull(mv);
    assertEquals("/WEB-INF/pages/listTabbed.jsp", mv.getViewName());

    Map<String, Object> model = mv.getModel();
    assertEquals("SOPs", model.get("title"));

    assertEquals("ListTarget.sop", model.get("targetType"));
    assertEquals("category", model.get("property"));
    assertNotNull(model.get("config"));
    assertNotNull(model.get("tabs"));

    @SuppressWarnings("unchecked")
    Map<String, String> tabs = (Map<String, String>) model.get("tabs");
    assertTrue("Expected SAMPLE tab", tabs.containsKey(SopCategory.SAMPLE.getLabel()));
    assertTrue("Expected LIBRARY tab", tabs.containsKey(SopCategory.LIBRARY.getLabel()));
    assertTrue("Expected RUN tab", tabs.containsKey(SopCategory.RUN.getLabel()));

    String configJson = model.get("config").toString();
    assertTrue("Expected config to include isAdmin but was: " + configJson, configJson.contains("\"isAdmin\""));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreateGetNewRendersEditSopPage() throws Exception {
    ModelAndView mv = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/new").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assertNotNull(mv);
    assertEquals("/WEB-INF/pages/editSop.jsp", mv.getViewName());

    Map<String, Object> model = mv.getModel();
    assertEquals("Create SOP", model.get("title"));
    assertEquals("create", model.get("pageMode"));
    assertNotNull("Expected sopJson in model", model.get("sopJson"));
    assertNotNull("Expected sopCategories in model", model.get("sopCategories"));
    assertNotNull("Expected isAdmin in model", model.get("isAdmin"));

    String sopJson = model.get("sopJson").toString();
    assertEquals(0L, readLong(sopJson, "$.id"));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreateGetWithBaseIdCopiesButForcesCreateMode() throws Exception {
    long sourceId = 1L;
    Sop db = currentSession().get(Sop.class, sourceId);
    assertNotNull("Missing SOP test data with ID " + sourceId, db);

    ModelAndView mv = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/new")
            .param("baseId", Long.toString(sourceId))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assertNotNull(mv);
    assertEquals("/WEB-INF/pages/editSop.jsp", mv.getViewName());

    Map<String, Object> model = mv.getModel();
    assertEquals("Create SOP", model.get("title"));
    assertEquals("create", model.get("pageMode"));

    String sopJson = model.get("sopJson").toString();

    assertEquals(0L, readLong(sopJson, "$.id"));
    assertEquals(db.getAlias(), JsonPath.read(sopJson, "$.alias"));
    assertEquals(db.getVersion(), JsonPath.read(sopJson, "$.version"));
    assertEquals(db.getUrl(), JsonPath.read(sopJson, "$.url"));
    assertEquals(db.isArchived(), (boolean) JsonPath.read(sopJson, "$.archived"));
    assertEquals(db.getCategory().name(), JsonPath.read(sopJson, "$.category"));

    // If fields exist, IDs must be reset for create-mode copy
    if (JsonPath.read(sopJson, "$.sopFields") != null) {
      int size = JsonPath.read(sopJson, "$.sopFields.length()");
      for (int i = 0; i < size; i++) {
        assertEquals(0, (int) JsonPath.read(sopJson, "$.sopFields[" + i + "].id"));
      }
    }
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreateGetWithCopyIdIsBackwardCompatible() throws Exception {
    long sourceId = 3L;
    Sop db = currentSession().get(Sop.class, sourceId);
    assertNotNull("Missing SOP test data with ID " + sourceId, db);

    ModelAndView mv = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/new")
            .param("copyId", Long.toString(sourceId))
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assertNotNull(mv);
    assertEquals("/WEB-INF/pages/editSop.jsp", mv.getViewName());

    Map<String, Object> model = mv.getModel();
    assertEquals("Create SOP", model.get("title"));
    assertEquals("create", model.get("pageMode"));

    String sopJson = model.get("sopJson").toString();
    assertEquals(0L, readLong(sopJson, "$.id"));
    assertEquals(db.getAlias(), JsonPath.read(sopJson, "$.alias"));
  }

  /**
   * NOTE: In ST context, AuthorizationManager.throwIfNonAdmin() is not translated into a
   * 403/redirect. It bubbles up and becomes a 500. So for "forbidden" tests we assert 5xx + exception
   * present.
   */
  @Test
  public void testCreateGetNewForbiddenForAnonymous() throws Exception {
    Exception ex = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/new").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError())
        .andReturn()
        .getResolvedException();

    assertNotNull("Expected authorization exception", ex);
  }

  @Test
  @WithMockUser(username = "user", password = "user", roles = {"INTERNAL"})
  public void testCreateGetNewForbiddenForNonAdminUser() throws Exception {
    Exception ex = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/new").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError())
        .andReturn()
        .getResolvedException();

    assertNotNull("Expected authorization exception", ex);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testEditGetRendersEditSopPage() throws Exception {
    long id = 1L;
    Sop db = currentSession().get(Sop.class, id);
    assertNotNull("Missing SOP test data with ID " + id, db);

    ModelAndView mv = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/" + id).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assertNotNull(mv);
    assertEquals("/WEB-INF/pages/editSop.jsp", mv.getViewName());

    Map<String, Object> model = mv.getModel();
    assertEquals("SOP " + id, model.get("title"));
    assertEquals("edit", model.get("pageMode"));
    assertNotNull(model.get("sopJson"));
    assertNotNull(model.get("sopCategories"));
    assertNotNull(model.get("isAdmin"));

    String sopJson = model.get("sopJson").toString();
    assertEquals(id, readLong(sopJson, "$.id"));
    assertEquals(db.getAlias(), JsonPath.read(sopJson, "$.alias"));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testEditGetRedirectsToListWhenNotFound() throws Exception {
    long missingId = 999999L;

    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/" + missingId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/sop/list"));
  }

  @Test
  @WithMockUser(username = "user", password = "user", roles = {"INTERNAL"})
  public void testEditGetForbiddenForNonAdminUser() throws Exception {
    Exception ex = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError())
        .andReturn()
        .getResolvedException();

    assertNotNull("Expected authorization exception", ex);
  }

}
