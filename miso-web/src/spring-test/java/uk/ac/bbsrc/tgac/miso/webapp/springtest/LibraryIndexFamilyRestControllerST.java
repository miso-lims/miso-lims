package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.dto.LibraryIndexFamilyDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryIndexDto;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;


public class LibraryIndexFamilyRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/libraryindexfamilies";
  private static final Class<LibraryIndexFamily> entityClass = LibraryIndexFamily.class;

  private LibraryIndexFamilyDto makeCreateDto() {

    LibraryIndexFamilyDto one = new LibraryIndexFamilyDto();
    one.setArchived(false);
    one.setFakeSequence(false);
    one.setUniqueDualIndex(false);
    one.setPlatformType("ILLUMINA");
    one.setName("one");
    one.setIndices(new ArrayList<LibraryIndexDto>());


    return one;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    LibraryIndexFamily fam = baseTestCreate(CONTROLLER_BASE, makeCreateDto(), entityClass, 200);
    assertEquals("one", fam.getName());
    assertEquals(false, fam.getArchived());
    assertEquals(false, fam.hasFakeSequence());
    assertEquals(false, fam.isUniqueDualIndex());
    assertEquals(PlatformType.ILLUMINA, fam.getPlatformType());

  }

  @Test
  public void testCreateFail() throws Exception {
    // LibraryIndexFamily creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testCreateUnauthorized(CONTROLLER_BASE, makeCreateDto(), entityClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // only admin can update these
    LibraryIndexFamilyDto single = Dtos.asDto(currentSession().get(LibraryIndexFamily.class, 1));
    single.setName("single");

    LibraryIndexFamily updated = baseTestUpdate(CONTROLLER_BASE, single, 1, entityClass);
    assertEquals("single", updated.getName());
  }

  @Test
  public void testBulkUpdateFail() throws Exception {
    // only admin can update these
    LibraryIndexFamilyDto single = Dtos.asDto(currentSession().get(LibraryIndexFamily.class, 1));
    single.setName("single");

    testUpdateUnauthorized(CONTROLLER_BASE, single, 1, entityClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibraryIndexFamily() throws Exception {
    testBulkDelete(entityClass, 4, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 4, CONTROLLER_BASE);
  }
}
