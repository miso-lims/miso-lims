package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.dto.SampleClassDto;

public class SampleClassRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/sampleclasses";
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testCreate() throws Exception {
    // Create a new Identity sample class with specific unused values
    SampleClassDto dto = new SampleClassDto();
    dto.setAlias("Test Identity Class");
    dto.setSampleCategory("Identity");
    dto.setSampleSubcategory(null);
    dto.setSuffix(null);
    dto.setArchived(false);

    String json = makeJson(dto);
    String response = getMockMvc().perform(post(CONTROLLER_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    SampleClass created = mapper.readValue(response, SampleClass.class);

    // Assert all fields are saved as expected
    assertNotNull(created.getId());
    assertEquals("Test Identity Class", created.getAlias());
    assertEquals("Identity", created.getSampleCategory());
    assertNull(created.getSampleSubcategory());
    assertNull(created.getSuffix());
    assertFalse(created.isArchived());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testCreateWithNullAlias() throws Exception {
    // Test that null alias is rejected
    SampleClassDto dto = new SampleClassDto();
    dto.setAlias(null);
    dto.setSampleCategory("Identity");
    dto.setSampleSubcategory(null);
    dto.setSuffix(null);

    getMockMvc().perform(post(CONTROLLER_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testUpdate() throws Exception {
    // Test updating sample class ID 28 (Unused, Aliquot category)
    // This is a safe sample class to update from the test data
    long sampleClassId = 28L;

    SampleClassDto dto = new SampleClassDto();
    dto.setId(sampleClassId);
    dto.setAlias("Updated Unused Class");
    dto.setSampleCategory("Aliquot");
    dto.setSampleSubcategory(null);
    dto.setSuffix(null);
    dto.setArchived(false);

    String json = makeJson(dto);
    String response = getMockMvc().perform(put(CONTROLLER_BASE + "/" + sampleClassId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    SampleClass updated = mapper.readValue(response, SampleClass.class);

    // Assert all fields are saved as expected
    assertEquals(sampleClassId, updated.getId());
    assertEquals("Updated Unused Class", updated.getAlias());
    assertEquals("Aliquot", updated.getSampleCategory());
    assertNull(updated.getSampleSubcategory());
    assertNull(updated.getSuffix());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testUpdateNonExistent() throws Exception {
    // Try to update a non-existent sample class
    SampleClassDto dto = new SampleClassDto();
    dto.setId(999999L);
    dto.setAlias("Nonexistent Class");
    dto.setSampleCategory("Identity");
    dto.setSampleSubcategory(null);
    dto.setSuffix(null);

    getMockMvc().perform(put(CONTROLLER_BASE + "/999999")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testBulkDelete() throws Exception {
    // Delete sample class ID 28 (Unused) - safe to delete from test data
    List<Long> ids = Arrays.asList(28L);

    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(ids)))
        .andExpect(status().isNoContent());
  }

  @Test
  public void testDeleteFail() throws Exception {
    // Test that delete fails without admin role (using default INTERNAL role)
    List<Long> ids = Arrays.asList(28L);

    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(ids)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  public void testDeleteNonExistent() throws Exception {
    // Test that deleting non-existent IDs returns bad request
    List<Long> ids = Arrays.asList(999999L);

    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(ids)))
        .andExpect(status().isBadRequest());
  }
}
