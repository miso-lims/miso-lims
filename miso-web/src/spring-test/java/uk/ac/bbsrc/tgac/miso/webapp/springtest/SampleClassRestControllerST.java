package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.core.data.SampleClass;
import uk.ac.bbsrc.tgac.miso.dto.SampleClassDto;

public class SampleClassRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/sampleclasses";

  @Test
  public void testCreate() throws Exception {
    SampleClassDto dto = makeDto("Test Class", "TC");

    SampleClass created = baseTestCreate(CONTROLLER_BASE, dto, SampleClass.class, 201);

    assertNotNull(created);
    assertEquals("Test Class", created.getAlias());
    assertEquals("TC", created.getSuffix());
    assertEquals("identity", created.getSampleCategory());
    assertFalse(created.isArchived());
    assertTrue(created.isDirectCreationAllowed());
  }

  @Test
  public void testCreateWithNullAlias() throws Exception {
    SampleClassDto dto = makeDto(null, "TC");

    getMockMvc().perform(post(CONTROLLER_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void testUpdate() throws Exception {
    // create one first
    SampleClassDto createDto = makeDto("Original", "OR");
    SampleClass created = baseTestCreate(CONTROLLER_BASE, createDto, SampleClass.class, 201);

    // now update it
    SampleClassDto updateDto = makeDto("Updated", "UP");
    updateDto.setId(created.getId());
    updateDto.setArchived(true);

    SampleClass updated = baseTestUpdate(CONTROLLER_BASE, updateDto, (int) created.getId(), SampleClass.class);

    assertEquals("Updated", updated.getAlias());
    assertEquals("UP", updated.getSuffix());
    assertTrue(updated.isArchived());
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
  public void testBulkDelete() throws Exception {
    SampleClassDto dto = makeDto("To Delete", "TD");
    SampleClass created = baseTestCreate(CONTROLLER_BASE, dto, SampleClass.class, 201);
    int id = (int) created.getId();

    // check if exists first
    assertNotNull(currentSession().get(SampleClass.class, id));

    testBulkDelete(SampleClass.class, id, CONTROLLER_BASE);

    // verify it's actually gone
    assertNull(currentSession().get(SampleClass.class, id));
  }

  @Test
  public void testBulkDeleteMultiple() throws Exception {
    // create a couple to delete
    SampleClass sc1 = baseTestCreate(CONTROLLER_BASE, makeDto("Class1", "C1"), SampleClass.class, 201);
    SampleClass sc2 = baseTestCreate(CONTROLLER_BASE, makeDto("Class2", "C2"), SampleClass.class, 201);

    List<Long> ids = Arrays.asList(sc1.getId(), sc2.getId());

    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(ids)))
        .andExpect(status().isNoContent());

    // both should be gone now
    assertNull(currentSession().get(SampleClass.class, (int) sc1.getId().longValue()));
    assertNull(currentSession().get(SampleClass.class, (int) sc2.getId().longValue()));
  }

  @Test
  public void testDeleteNonExistent() throws Exception {
    List<Long> ids = Arrays.asList(999999L);

    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(ids)))
        .andExpect(status().isNotFound());
  }

  private SampleClassDto makeDto(String alias, String suffix) {
    SampleClassDto dto = new SampleClassDto();
    dto.setAlias(alias);
    dto.setSampleCategory("identity");
    dto.setSuffix(suffix);
    dto.setArchived(false);
    dto.setDirectCreationAllowed(true);
    return dto;
  }
}
