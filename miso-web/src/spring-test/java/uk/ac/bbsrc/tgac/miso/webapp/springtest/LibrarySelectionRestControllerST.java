package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.dto.LibrarySelectionTypeDto;



public class LibrarySelectionRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/libraryselections";
  private static final Class<LibrarySelectionType> entityClass = LibrarySelectionType.class;

  private List<LibrarySelectionTypeDto> makeCreateDtos() {

    List<LibrarySelectionTypeDto> dtos = new ArrayList<LibrarySelectionTypeDto>();
    LibrarySelectionTypeDto one = new LibrarySelectionTypeDto();
    one.setName("one");
    one.setDescription("first");

    LibrarySelectionTypeDto two = new LibrarySelectionTypeDto();
    two.setName("two");
    two.setDescription("second");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibrarySelectionType> librarySelectionTypes =
        baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("one", librarySelectionTypes.get(0).getName());
    assertEquals("first", librarySelectionTypes.get(0).getDescription());

    assertEquals("two", librarySelectionTypes.get(1).getName());
    assertEquals("second", librarySelectionTypes.get(1).getDescription());

  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibrarySelectionType creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update these
    LibrarySelectionTypeDto one = Dtos.asDto(currentSession().get(LibrarySelectionType.class, 1));
    LibrarySelectionTypeDto three = Dtos.asDto(currentSession().get(LibrarySelectionType.class, 3));
    one.setName("one");
    three.setName("three");

    List<LibrarySelectionTypeDto> dtos = new ArrayList<LibrarySelectionTypeDto>();
    dtos.add(one);
    dtos.add(three);


    List<LibrarySelectionType> librarySelectionTypes =
        (List<LibrarySelectionType>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
            LibrarySelectionTypeDto::getId);
    assertEquals("one", librarySelectionTypes.get(0).getName());
    assertEquals("three", librarySelectionTypes.get(1).getName());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // only admin can update these
    LibrarySelectionTypeDto one = Dtos.asDto(currentSession().get(LibrarySelectionType.class, 1));
    LibrarySelectionTypeDto three = Dtos.asDto(currentSession().get(LibrarySelectionType.class, 3));
    one.setName("one");
    three.setName("three");

    List<LibrarySelectionTypeDto> dtos = new ArrayList<LibrarySelectionTypeDto>();
    dtos.add(one);
    dtos.add(three);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibrarySelectionType() throws Exception {
    testBulkDelete(entityClass, 26, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 26, CONTROLLER_BASE);
  }
}
