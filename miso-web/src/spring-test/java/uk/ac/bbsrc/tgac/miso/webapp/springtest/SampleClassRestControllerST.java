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

import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.dto.SampleClassDto;
import uk.ac.bbsrc.tgac.miso.dto.SampleValidRelationshipDto;

public class SampleClassRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/sampleclasses";
  private static final Class<SampleClassImpl> ENTITY_CLASS = SampleClassImpl.class;

  private static final long IDENTITY_CLASS_ID = 1L;

  // For delete success we keep using the explicit "Unused" fixture
  private static final int UNUSED_CLASS_ID = 28;

  // For update success, use an existing class with existing valid relationships
  private static final int UPDATE_CLASS_ID = 15; // gDNA (aliquot)

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    SampleClassDto dto = new SampleClassDto();
    dto.setAlias("Test Tissue Class");
    dto.setSampleCategory("Tissue");
    dto.setSampleSubcategory(null);
    dto.setSuffix(null);
    dto.setArchived(false);
    dto.setDirectCreationAllowed(true);

    // Tissue classes must have a single Identity parent relationship
    SampleValidRelationshipDto parentRel = new SampleValidRelationshipDto();
    parentRel.setParentId(IDENTITY_CLASS_ID);
    parentRel.setArchived(false);
    dto.setParentRelationships(Collections.singletonList(parentRel));

    SampleClassImpl created = baseTestCreate(CONTROLLER_BASE, dto, ENTITY_CLASS, 201);
    Assert.assertNotNull(created);
    Assert.assertEquals("Test Tissue Class", created.getAlias());
  }

  @Test
  public void testCreateFail() throws Exception {
    SampleClassDto dto = new SampleClassDto();
    dto.setAlias("Should Fail");
    dto.setSampleCategory("Tissue");
    dto.setArchived(false);
    dto.setDirectCreationAllowed(true);

    // include required parent relationship so this fails for auth, not validation
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
  public void testUpdate() throws Exception {
    SampleClassImpl existing = (SampleClassImpl) currentSession().get(SampleClassImpl.class, (long) UPDATE_CLASS_ID);
    Assert.assertNotNull(existing);
    Assert.assertFalse(existing.getParentRelationships().isEmpty());

    long parentRelationshipId = existing.getParentRelationships().iterator().next().getId();
    Assert.assertTrue(parentRelationshipId > 0);

    SampleClassDto dto = new SampleClassDto();
    dto.setId((long) UPDATE_CLASS_ID);
    dto.setAlias("Updated gDNA Aliquot Class");
    dto.setSampleCategory("Aliquot"); // keep existing category
    dto.setSampleSubcategory(existing.getSampleSubcategory());
    dto.setSuffix(existing.getSuffix());
    dto.setArchived(false);
    dto.setDirectCreationAllowed(existing.isDirectCreationAllowed());

    // Reuse existing relationship by ID so we are not "creating" relationships as part of update
    SampleValidRelationshipDto parentRel = new SampleValidRelationshipDto();
    parentRel.setId(parentRelationshipId);
    parentRel.setArchived(false);
    dto.setParentRelationships(Collections.singletonList(parentRel));

    SampleClassImpl updated = baseTestUpdate(CONTROLLER_BASE, dto, UPDATE_CLASS_ID, ENTITY_CLASS);
    Assert.assertNotNull(updated);
    Assert.assertEquals("Updated gDNA Aliquot Class", updated.getAlias());
  }

  @Test
  public void testUpdateFail() throws Exception {
    // Ensure failure is due to auth, not validation. Reuse existing relationship ID like success test.
    SampleClassImpl existing = (SampleClassImpl) currentSession().get(SampleClassImpl.class, (long) UPDATE_CLASS_ID);
    Assert.assertNotNull(existing);
    Assert.assertFalse(existing.getParentRelationships().isEmpty());

    long parentRelationshipId = existing.getParentRelationships().iterator().next().getId();
    Assert.assertTrue(parentRelationshipId > 0);

    SampleClassDto dto = new SampleClassDto();
    dto.setId((long) UPDATE_CLASS_ID);
    dto.setAlias("Should Fail Update");
    dto.setSampleCategory("Aliquot");

    SampleValidRelationshipDto parentRel = new SampleValidRelationshipDto();
    parentRel.setId(parentRelationshipId);
    parentRel.setArchived(false);
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

    // include required parent relationship so it fails for NOT FOUND, not validation
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
  public void testDeleteFail() throws Exception {
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
