package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import uk.ac.bbsrc.tgac.miso.core.data.SampleValidRelationship;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleClassImpl;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
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
    assertNotNull(created);

    assertEquals("Test Tissue Class", created.getAlias());
    assertEquals("Tissue", created.getSampleCategory());
    assertNull(created.getSampleSubcategory());
    assertNull(created.getSuffix());
    assertFalse(created.isArchived());
    assertTrue(created.isDirectCreationAllowed());

    assertNotNull(created.getParentRelationships());
    assertEquals(1, created.getParentRelationships().size());

    SampleValidRelationship createdRel = created.getParentRelationships().iterator().next();
    assertNotNull(createdRel.getParent());
    assertEquals("parent ID mismatch", IDENTITY_CLASS_ID, createdRel.getParent().getId());
    assertFalse("relationship archived should be false", createdRel.isArchived());
  }

  @Test
  public void testCreateUnauthorized() throws Exception {
    SampleClassDto dto = new SampleClassDto();
    dto.setAlias("Test Tissue Class");
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
  public void testUpdateSuccess() throws Exception {
    SampleClassImpl existing = (SampleClassImpl) currentSession().get(ENTITY_CLASS, (long) UPDATE_CLASS_ID);
    assertNotNull(existing);

    Set<Long> parentIdsBefore = existing.getParentRelationships().stream()
        .filter(rel -> rel.getParent() != null)
        .map(rel -> rel.getParent().getId())
        .collect(Collectors.toSet());

    SampleClassDto dto = Dtos.asDto(existing);
    dto.setAlias("Updated gDNA Aliquot Class");

    SampleClassImpl returned = baseTestUpdate(CONTROLLER_BASE, dto, UPDATE_CLASS_ID, ENTITY_CLASS);
    assertNotNull(returned);

    assertEquals("Updated gDNA Aliquot Class", returned.getAlias());
    assertEquals("category changed", existing.getSampleCategory(), returned.getSampleCategory());
    assertEquals("subcategory changed", existing.getSampleSubcategory(), returned.getSampleSubcategory());
    assertEquals("suffix changed", existing.getSuffix(), returned.getSuffix());
    assertEquals("archived changed", existing.isArchived(), returned.isArchived());
    assertEquals("directCreationAllowed changed", existing.isDirectCreationAllowed(),
        returned.isDirectCreationAllowed());

    assertNotNull("parent relationships is null", returned.getParentRelationships());
    assertFalse("parent relationships is empty", returned.getParentRelationships().isEmpty());

    Set<Long> parentIdsAfter = returned.getParentRelationships().stream()
        .filter(rel -> rel.getParent() != null)
        .map(rel -> rel.getParent().getId())
        .collect(Collectors.toSet());

    assertEquals("parent relationships changed", parentIdsBefore, parentIdsAfter);
  }

  @Test
  public void testUpdateUnauthorized() throws Exception {
    SampleClassImpl existing = (SampleClassImpl) currentSession().get(ENTITY_CLASS, (long) UPDATE_CLASS_ID);
    assertNotNull(existing);

    SampleClassDto dto = Dtos.asDto(existing);
    dto.setAlias("Should Fail Update");

    testUpdateUnauthorized(CONTROLLER_BASE, dto, UPDATE_CLASS_ID, ENTITY_CLASS);
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
}
