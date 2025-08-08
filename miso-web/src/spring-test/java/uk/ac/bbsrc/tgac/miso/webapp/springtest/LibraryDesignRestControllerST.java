package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDesignDto;



public class LibraryDesignRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/librarydesigns";
  private static final Class<LibraryDesign> entityClass = LibraryDesign.class;

  private List<LibraryDesignDto> makeCreateDtos() {

    List<LibraryDesignDto> dtos = new ArrayList<LibraryDesignDto>();
    LibraryDesignDto one = new LibraryDesignDto();
    one.setName("1");
    one.setSampleClassId(1L);
    one.setStrategyId(20L);
    one.setSelectionId(15L);
    one.setDesignCodeId(7L);

    LibraryDesignDto two = new LibraryDesignDto();
    two.setName("2");
    two.setSampleClassId(23L);
    two.setStrategyId(2L);
    two.setSelectionId(16L);
    two.setDesignCodeId(8L);

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibraryDesign> codes = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("1", codes.get(0).getName());
    assertEquals(20L, codes.get(0).getSampleClass().getId());
    assertEquals(1L, codes.get(0).getLibraryStrategyType().getId());
    assertEquals(15L, codes.get(0).getLibrarySelectionType().getId());
    assertEquals(7L, codes.get(0).getLibraryDesignCode().getId());

    assertEquals("2", codes.get(1).getName());
    assertEquals(23L, codes.get(1).getSampleClass().getId());
    assertEquals(2L, codes.get(1).getLibraryStrategyType().getId());
    assertEquals(16L, codes.get(1).getLibrarySelectionType().getId());
    assertEquals(8L, codes.get(1).getLibraryDesignCode().getId());

  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibraryDesign creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // the admin user made these LibraryDesigns so only admin can update them
    LibraryDesignDto t = Dtos.asDto(currentSession().get(LibraryDesign.class, 2));
    LibraryDesignDto t2 = Dtos.asDto(currentSession().get(LibraryDesign.class, 3));
    t.setName("this");
    t2.setName("this2");

    List<LibraryDesignDto> dtos = new ArrayList<LibraryDesignDto>();
    dtos.add(t);
    dtos.add(t2);

    List<LibraryDesign> libraryDesigns =
        (List<LibraryDesign>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos, LibraryDesignDto::getId);
    assertEquals("this", libraryDesigns.get(0).getName());
    assertEquals("this2", libraryDesigns.get(1).getName());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // the admin user made these LibraryDesigns so only admin can update them
    LibraryDesignDto t = Dtos.asDto(currentSession().get(LibraryDesign.class, 2));
    LibraryDesignDto t2 = Dtos.asDto(currentSession().get(LibraryDesign.class, 3));
    t.setName("this");
    t2.setName("this2");

    List<LibraryDesignDto> dtos = new ArrayList<LibraryDesignDto>();
    dtos.add(t);
    dtos.add(t2);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibraryDesign() throws Exception {
    testBulkDelete(entityClass, 20, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 20, CONTROLLER_BASE);
  }
}
