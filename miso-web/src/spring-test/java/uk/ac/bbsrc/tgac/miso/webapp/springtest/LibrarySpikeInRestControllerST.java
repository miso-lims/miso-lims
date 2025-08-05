package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.dto.LibrarySpikeInDto;


public class LibrarySpikeInRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/libraryspikeins";
  private static final Class<LibrarySpikeIn> entityClass = LibrarySpikeIn.class;

  private List<LibrarySpikeInDto> makeCreateDtos() {

    List<LibrarySpikeInDto> dtos = new ArrayList<LibrarySpikeInDto>();
    LibrarySpikeInDto one = new LibrarySpikeInDto();
    one.setAlias("one");

    LibrarySpikeInDto two = new LibrarySpikeInDto();
    two.setAlias("two");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibrarySpikeIn> librarySpikeIns =
        baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("one", librarySpikeIns.get(0).getAlias());
    assertEquals("two", librarySpikeIns.get(1).getAlias());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibrarySpikeIn creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update these
    LibrarySpikeInDto one = Dtos.asDto(currentSession().get(LibrarySpikeIn.class, 1));
    LibrarySpikeInDto three = Dtos.asDto(currentSession().get(LibrarySpikeIn.class, 3));
    one.setAlias("one");
    three.setAlias("three");

    List<LibrarySpikeInDto> dtos = new ArrayList<LibrarySpikeInDto>();
    dtos.add(one);
    dtos.add(three);


    List<LibrarySpikeIn> librarySpikeIns =
        (List<LibrarySpikeIn>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
            LibrarySpikeInDto::getId);

    assertEquals(1L, librarySpikeIns.get(0).getId());
    assertEquals(3L, librarySpikeIns.get(1).getId());
    assertEquals("one", librarySpikeIns.get(0).getAlias());
    assertEquals("three", librarySpikeIns.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // only admin can update these
    LibrarySpikeInDto one = Dtos.asDto(currentSession().get(LibrarySpikeIn.class, 1));
    LibrarySpikeInDto three = Dtos.asDto(currentSession().get(LibrarySpikeIn.class, 3));
    one.setAlias("one");
    three.setAlias("three");

    List<LibrarySpikeInDto> dtos = new ArrayList<LibrarySpikeInDto>();
    dtos.add(one);
    dtos.add(three);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibrarySpikeIn() throws Exception {
    testBulkDelete(entityClass, 3, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 3, CONTROLLER_BASE);
  }
}
