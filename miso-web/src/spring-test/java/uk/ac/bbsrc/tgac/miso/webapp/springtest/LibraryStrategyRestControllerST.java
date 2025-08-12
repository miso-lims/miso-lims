package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.dto.LibraryStrategyTypeDto;


public class LibraryStrategyRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/librarystrategies";
  private static final Class<LibraryStrategyType> entityClass = LibraryStrategyType.class;

  private List<LibraryStrategyTypeDto> makeCreateDtos() {

    List<LibraryStrategyTypeDto> dtos = new ArrayList<LibraryStrategyTypeDto>();
    LibraryStrategyTypeDto one = new LibraryStrategyTypeDto();
    one.setDescription("first");
    one.setName("one");

    LibraryStrategyTypeDto two = new LibraryStrategyTypeDto();
    two.setName("two");
    two.setDescription("second");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibraryStrategyType> libraryStrategyTypes =
        baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("one", libraryStrategyTypes.get(0).getName());
    assertEquals("first", libraryStrategyTypes.get(0).getDescription());

    assertEquals("two", libraryStrategyTypes.get(1).getName());
    assertEquals("second", libraryStrategyTypes.get(1).getDescription());

  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibraryStrategyType creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update these
    LibraryStrategyTypeDto one = Dtos.asDto(currentSession().get(LibraryStrategyType.class, 1));
    LibraryStrategyTypeDto three = Dtos.asDto(currentSession().get(LibraryStrategyType.class, 3));
    one.setName("one");
    three.setName("three");

    List<LibraryStrategyTypeDto> dtos = new ArrayList<LibraryStrategyTypeDto>();
    dtos.add(one);
    dtos.add(three);


    List<LibraryStrategyType> libraryStrategyTypes =
        baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
            LibraryStrategyTypeDto::getId);
    assertEquals("one", libraryStrategyTypes.get(0).getName());
    assertEquals("three", libraryStrategyTypes.get(1).getName());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // only admin can update these
    LibraryStrategyTypeDto one = Dtos.asDto(currentSession().get(LibraryStrategyType.class, 1));
    LibraryStrategyTypeDto three = Dtos.asDto(currentSession().get(LibraryStrategyType.class, 3));
    one.setName("one");
    three.setName("three");

    List<LibraryStrategyTypeDto> dtos = new ArrayList<LibraryStrategyTypeDto>();
    dtos.add(one);
    dtos.add(three);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibraryStrategyType() throws Exception {
    testBulkDelete(entityClass, 11, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 11, CONTROLLER_BASE);
  }
}
