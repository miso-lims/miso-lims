package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
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
    assertEquals(IDENTITY_CLASS_ID, createdRel.getParent().getId(), "parent ID mismatch");
    assertFalse(createdRel.isArchived(), "relationship archived should be false");
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
    SampleClassImpl existing = (SampleClassImpl) currentSession().find(ENTITY_CLASS, (long) UPDATE_CLASS_ID);
    assertNotNull(existing);

    int parentCountBefore = existing.getParentRelationships().size();
    Set<Long> parentIdsBefore = existing.getParentRelationships().stream()
        .map(rel -> rel.getParent().getId())
        .collect(Collectors.toSet());

    SampleClassDto dto = Dtos.asDto(existing);
    dto.setAlias("Updated gDNA Aliquot Class");

    SampleClassImpl returned = baseTestUpdate(CONTROLLER_BASE, dto, UPDATE_CLASS_ID, ENTITY_CLASS);
    assertNotNull(returned);

    assertEquals("Updated gDNA Aliquot Class", returned.getAlias());
    assertEquals(existing.getSampleCategory(), returned.getSampleCategory(), "category changed");
    assertEquals(existing.getSampleSubcategory(), returned.getSampleSubcategory(), "subcategory changed");
    assertEquals(existing.getSuffix(), returned.getSuffix(), "suffix changed");
    assertEquals(existing.isArchived(), returned.isArchived(), "archived changed");
    assertEquals(existing.isDirectCreationAllowed(), returned.isDirectCreationAllowed(),
        "directCreationAllowed changed");

    assertNotNull(returned.getParentRelationships(), "parent relationships is null");
    assertFalse(returned.getParentRelationships().isEmpty(), "parent relationships is empty");
    assertEquals(parentCountBefore, returned.getParentRelationships().size(), "parent relationships count changed");

    Set<Long> parentIdsAfter = returned.getParentRelationships().stream()
        .map(rel -> rel.getParent().getId())
        .collect(Collectors.toSet());

    assertEquals(parentIdsBefore, parentIdsAfter, "parent relationships changed");
  }

  @Test
  public void testUpdateUnauthorized() throws Exception {
    SampleClassImpl existing = (SampleClassImpl) currentSession().find(ENTITY_CLASS, (long) UPDATE_CLASS_ID);
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
