package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.dto.SampleClassDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleValidRelationshipDto;

public class SampleClassRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/sampleclasses";
  private static final Class<SampleClassImpl> ENTITY_CLASS = SampleClassImpl.class;
  private static final long IDENTITY_CLASS_ID = 1L;
  private static final int UPDATE_CLASS_ID = 15;
  private static final int UNUSED_CLASS_ID = 28;

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreateSuccess() throws Exception {
    SampleClassDto dto = new SampleClassDto();
    dto.setAlias("Test Tissue Class");
    dto.setSampleCategory("Tissue");
    dto.setSampleSubcategory(null);
    dto.setSuffix(null);
    dto.setArchived(false);
    dto.setDirectCreationAllowed(true);

    SampleValidRelationshipDto parentRel = new SampleValidRelationshipDto();
    parentRel.setParentId(IDENTITY_CLASS_ID);
    parentRel.setArchived(false);
    dto.setParentRelationships(Collections.singletonList(parentRel));

    SampleClassImpl created = baseTestCreate(CONTROLLER_BASE, dto, ENTITY_CLASS, 201);

    Assert.assertNotNull(created);
    Assert.assertEquals("Test Tissue Class", created.getAlias());
  }

  @Test
  public void testCreateUnauthorized() throws Exception {
    SampleClassDto dto = new SampleClassDto();
    dto.setAlias("Should Fail");
    dto.setSampleCategory("Tissue");
    dto.setArchived(false);
    dto.setDirectCreationAllowed(true);

    SampleValidRelationshipDto parentRel = new SampleValidRelationshipDto();
    parentRel.setParentId(IDENTITY_CLASS_ID);
    parentRel.setArchived(false);
    dto.setParentRelationships(Collections.singletonList(parentRel));

    testCreateUnauthorized(CONTROLLER_BASE, dto, ENTITY_CLASS);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreateWithNullAlias() throws Exception {
    SampleClassDto dto = new SampleClassDto();
    dto.setAlias(null);
    dto.setSampleCategory("Tissue");
    dto.setArchived(false);
    dto.setDirectCreationAllowed(true);

    SampleValidRelationshipDto parentRel = new SampleValidRelationshipDto();
    parentRel.setParentId(IDENTITY_CLASS_ID);
    parentRel.setArchived(false);
    dto.setParentRelationships(Collections.singletonList(parentRel));

    getMockMvc().perform(post(CONTROLLER_BASE)
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdateSuccess() throws Exception {
    SampleClassImpl existing =
        (SampleClassImpl) currentSession().get(ENTITY_CLASS, (long) UPDATE_CLASS_ID);

    Assert.assertNotNull(existing);
    Assert.assertFalse(existing.getParentRelationships().isEmpty());

    SampleValidRelationship existingRel = existing.getParentRelationships().iterator().next();
    long parentRelationshipId = existingRel.getId();
    long parentId = existingRel.getParent().getId();

    SampleClassDto dto = new SampleClassDto();
    dto.setId(existing.getId());
    dto.setAlias("Updated gDNA Aliquot Class");
    dto.setSampleCategory(existing.getSampleCategory());
    dto.setSampleSubcategory(existing.getSampleSubcategory());
    dto.setSuffix(existing.getSuffix());
    dto.setArchived(existing.isArchived());
    dto.setDirectCreationAllowed(existing.isDirectCreationAllowed());

    SampleValidRelationshipDto parentRel = new SampleValidRelationshipDto();
    parentRel.setId(parentRelationshipId);
    parentRel.setParentId(parentId);
    parentRel.setArchived(existingRel.isArchived());
    dto.setParentRelationships(Collections.singletonList(parentRel));

    SampleClassImpl returned = baseTestUpdate(CONTROLLER_BASE, dto, UPDATE_CLASS_ID, ENTITY_CLASS);

    Assert.assertNotNull(returned);
    Assert.assertEquals(dto.getAlias(), returned.getAlias());
  }

  @Test
  public void testUpdateUnauthorized() throws Exception {
    SampleClassImpl existing =
        (SampleClassImpl) currentSession().get(ENTITY_CLASS, (long) UPDATE_CLASS_ID);

    Assert.assertNotNull(existing);
    Assert.assertFalse(existing.getParentRelationships().isEmpty());

    SampleValidRelationship existingRel = existing.getParentRelationships().iterator().next();
    long parentRelationshipId = existingRel.getId();
    long parentId = existingRel.getParent().getId();

    SampleClassDto dto = new SampleClassDto();
    dto.setId(existing.getId());
    dto.setAlias("Should Fail Update");

    dto.setSampleCategory(existing.getSampleCategory());
    dto.setSampleSubcategory(existing.getSampleSubcategory());
    dto.setSuffix(existing.getSuffix());
    dto.setArchived(existing.isArchived());
    dto.setDirectCreationAllowed(existing.isDirectCreationAllowed());

    SampleValidRelationshipDto parentRel = new SampleValidRelationshipDto();
    parentRel.setId(parentRelationshipId);
    parentRel.setParentId(parentId);
    parentRel.setArchived(existingRel.isArchived());
    dto.setParentRelationships(Collections.singletonList(parentRel));

    testUpdateUnauthorized(CONTROLLER_BASE, dto, UPDATE_CLASS_ID, ENTITY_CLASS);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdateNonExistent() throws Exception {
    SampleClassDto dto = new SampleClassDto();
    dto.setId(999999L);
    dto.setAlias("Nonexistent Class");
    dto.setSampleCategory("Tissue");
    dto.setArchived(false);
    dto.setDirectCreationAllowed(true);

    SampleValidRelationshipDto parentRel = new SampleValidRelationshipDto();
    parentRel.setParentId(IDENTITY_CLASS_ID);
    parentRel.setArchived(false);
    dto.setParentRelationships(Collections.singletonList(parentRel));

    getMockMvc().perform(put(CONTROLLER_BASE + "/999999")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(dto)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkDelete() throws Exception {
    testBulkDelete(ENTITY_CLASS, UNUSED_CLASS_ID, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testBulkDeleteUnauthorized() throws Exception {
    testDeleteUnauthorized(ENTITY_CLASS, UNUSED_CLASS_ID, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteNonExistent() throws Exception {
    List<Long> ids = Arrays.asList(999999L);

    getMockMvc().perform(post(CONTROLLER_BASE + "/bulk-delete")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJson(ids)))
        .andExpect(status().isBadRequest());
  }
}
