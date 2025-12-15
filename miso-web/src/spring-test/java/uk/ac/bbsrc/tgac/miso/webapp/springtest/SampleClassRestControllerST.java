package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.core.service.SampleClassService;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.SampleClassDto;

public class SampleClassRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/sampleclasses";
  private static final long EXISTING_ID = 28L;

  @Autowired
  private SampleClassService sampleClassService;

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testCreateSuccess() throws Exception {
    SampleClass template = pickCloneableTemplate();
    assertNotNull(template);

    SampleClassDto dto = Dtos.asDto(template);

    dto.setId(null);
    dto.setChildRelationships(null);

    dto.setAlias("ST Created Sample Class");
    dto.setSuffix(pickUnusedOneCharSuffix(getAllSuffixes()));

    MvcResult res = getMockMvc().perform(post(CONTROLLER_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andReturn();

    assertStatusOrFailWithBody(res, 201);
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testCreateSecondIdentityClassFails() throws Exception {
    SampleClass template = pickCloneableTemplate();
    assertNotNull(template);

    SampleClassDto dto = Dtos.asDto(template);

    dto.setId(null);
    dto.setChildRelationships(null);

    dto.setAlias("Should Fail Identity Duplicate");
    dto.setSuffix(pickUnusedOneCharSuffix(getAllSuffixes()));

    dto.setSampleCategory("Identity");
    dto.setSampleSubcategory("");

    getMockMvc().perform(post(CONTROLLER_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testUpdateSuccess() throws Exception {
    SampleClass existing = sampleClassService.get(EXISTING_ID);
    assertNotNull(existing);

    SampleClassDto dto = Dtos.asDto(existing);
    dto.setAlias(dto.getAlias() + " (Updated)");

    MvcResult res = getMockMvc().perform(put(CONTROLLER_BASE + "/" + EXISTING_ID)
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andReturn();

    assertStatusOrFailWithBody(res, 200);

    SampleClass reloaded = sampleClassService.get(EXISTING_ID);
    assertEquals(dto.getAlias(), reloaded.getAlias());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testUpdateNonExistent() throws Exception {
    SampleClass template = pickCloneableTemplate();
    assertNotNull(template);

    SampleClassDto dto = Dtos.asDto(template);
    dto.setId(999999L);
    dto.setAlias("Nonexistent");
    dto.setSuffix(pickUnusedOneCharSuffix(getAllSuffixes()));

    getMockMvc().perform(put(CONTROLLER_BASE + "/999999")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testBulkDeleteSuccess() throws Exception {
    assertNotNull(sampleClassService.get(EXISTING_ID));

    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(Arrays.asList(EXISTING_ID))))
        .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testBulkDeleteNonExistent() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(Arrays.asList(999999L))))
        .andExpect(status().isBadRequest());
  }

  private void assertStatusOrFailWithBody(MvcResult res, int expected) throws Exception {
    if (res.getResponse().getStatus() != expected) {
      fail("Expected HTTP " + expected + " but got "
          + res.getResponse().getStatus()
          + ". Response body:\n"
          + res.getResponse().getContentAsString());
    }
  }


  private SampleClass pickCloneableTemplate() throws Exception {
    for (SampleClass sc : sampleClassService.list()) {
      if (sc == null)
        continue;
      if ("Identity".equals(sc.getSampleCategory()))
        continue;

      SampleClassDto dto = Dtos.asDto(sc);
      if (dto.getParentRelationships() == null || dto.getParentRelationships().isEmpty())
        continue;

      return sc;
    }
    return null;
  }

  private Set<String> getAllSuffixes() throws Exception {
    Set<String> suffixes = new HashSet<>();
    for (SampleClass sc : sampleClassService.list()) {
      if (sc.getSuffix() != null) {
        suffixes.add(sc.getSuffix());
      }
    }
    return suffixes;
  }

  private String pickUnusedOneCharSuffix(Set<String> used) {
    for (char c = 'A'; c <= 'Z'; c++) {
      String s = String.valueOf(c);
      if (!used.contains(s))
        return s;
    }
    return "Z";
  }
}
