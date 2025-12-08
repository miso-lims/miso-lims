package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.dto.SampleClassDto;

public class SampleClassRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/sampleclasses";

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testCreate() throws Exception {
    SampleClassDto dto = makeDto("Test Class", null);

    SampleClass created = baseTestCreate(CONTROLLER_BASE, dto, SampleClass.class, 201);

    assertNotNull(created);
    assertEquals("Test Class", created.getAlias());
    assertNull(created.getSuffix());
    assertEquals("Identity", created.getSampleCategory());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testCreateWithNullAlias() throws Exception {
    SampleClassDto dto = makeDto(null, null);

    getMockMvc().perform(post(CONTROLLER_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testUpdate() throws Exception {
    long existingId = 1L;

    SampleClassDto updateDto = makeDto("Updated Identity", null);
    updateDto.setId(existingId);

    SampleClass updated = baseTestUpdate(CONTROLLER_BASE, updateDto, (int) existingId, SampleClass.class);

    assertEquals("Updated Identity", updated.getAlias());
    assertEquals("Identity", updated.getSampleCategory());
    assertNull(updated.getSuffix());
  }

  @Test
  public void testUpdateNonExistent() throws Exception {
    SampleClassDto dto = makeDto("Nonexistent", "NE");
    dto.setId(999999L);

    getMockMvc().perform(put(CONTROLLER_BASE + "/999999")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testBulkDelete() throws Exception {
    int id = 28;


    assertNotNull(currentSession().get(SampleClass.class, id));

    testBulkDelete(SampleClass.class, id, CONTROLLER_BASE);

    assertNull(currentSession().get(SampleClass.class, id));
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testBulkDeleteMultiple() throws Exception {
    List<Long> ids = Arrays.asList(26L, 27L);

    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(ids)))
        .andExpect(status().isNoContent());

    assertNull(currentSession().get(SampleClass.class, 26));
    assertNull(currentSession().get(SampleClass.class, 27));
  }

  @Test
  public void testDeleteNonExistent() throws Exception {
    List<Long> ids = Arrays.asList(999999L);

    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(ids)))
        .andExpect(status().isBadRequest());
  }

  private SampleClassDto makeDto(String alias, String suffix) {
    SampleClassDto dto = new SampleClassDto();
    dto.setAlias(alias);
    dto.setSampleCategory("Identity");
    dto.setSampleSubcategory(null);
    dto.setSuffix(suffix);
    return dto;
  }
}
