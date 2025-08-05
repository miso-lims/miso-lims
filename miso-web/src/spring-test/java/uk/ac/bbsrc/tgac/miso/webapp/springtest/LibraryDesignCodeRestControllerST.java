package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDesignCodeDto;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.Assert.*;
import java.util.List;
import java.util.ArrayList;

public class LibraryDesignCodeRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/librarydesigncodes";
  private static final Class<LibraryDesignCode> entityClass = LibraryDesignCode.class;

  private List<LibraryDesignCodeDto> makeCreateDtos() {

    List<LibraryDesignCodeDto> dtos = new ArrayList<LibraryDesignCodeDto>();
    LibraryDesignCodeDto one = new LibraryDesignCodeDto();
    one.setCode("AB");
    one.setTargetedSequencingRequired(false);
    one.setDescription("one");

    LibraryDesignCodeDto two = new LibraryDesignCodeDto();
    two.setCode("CD");
    two.setTargetedSequencingRequired(false);
    two.setDescription("two");


    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibraryDesignCode> codes = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("AB", codes.get(0).getCode());
    assertEquals(false, codes.get(0).isTargetedSequencingRequired());
    assertEquals("one", codes.get(0).getDescription());

    assertEquals("CD", codes.get(1).getCode());
    assertEquals(false, codes.get(1).isTargetedSequencingRequired());
    assertEquals("two", codes.get(1).getDescription());

  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibraryDesignCode creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // the admin user made these LibraryDesignCodes so only admin can update them
    LibraryDesignCodeDto bt = Dtos.asDto(currentSession().get(LibraryDesignCode.class, 1));
    LibraryDesignCodeDto d6 = Dtos.asDto(currentSession().get(LibraryDesignCode.class, 3));
    bt.setCode("BT");
    d6.setCode("D6");

    List<LibraryDesignCodeDto> dtos = new ArrayList<LibraryDesignCodeDto>();
    dtos.add(bt);
    dtos.add(d6);

    List<LibraryDesignCode> libraryDesignCodes =
        (List<LibraryDesignCode>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
            LibraryDesignCodeDto::getId);

    assertEquals(1L, libraryDesignCodes.get(0).getId());
    assertEquals(3L, libraryDesignCodes.get(1).getId());
    assertEquals("BT", libraryDesignCodes.get(0).getCode());
    assertEquals("D6", libraryDesignCodes.get(1).getCode());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // the admin user made these LibraryDesignCodes so only admin can update them
    LibraryDesignCodeDto bt = Dtos.asDto(currentSession().get(LibraryDesignCode.class, 1));
    LibraryDesignCodeDto d6 = Dtos.asDto(currentSession().get(LibraryDesignCode.class, 3));
    bt.setCode("BT");
    d6.setCode("D6");

    List<LibraryDesignCodeDto> dtos = new ArrayList<LibraryDesignCodeDto>();
    dtos.add(d6);
    dtos.add(bt);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibraryDesignCode() throws Exception {
    testBulkDelete(entityClass, 18, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 18, CONTROLLER_BASE);
  }
}
