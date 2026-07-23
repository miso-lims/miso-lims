package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.*;

import uk.ac.bbsrc.tgac.miso.core.data.BoxSize;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.BoxSizeDto;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

public class BoxSizeRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/boxsizes";
  private static final Class<BoxSize> entityClass = BoxSize.class;


  private List<BoxSizeDto> makeCreateDtos() {
    BoxSizeDto dto1 = new BoxSizeDto();
    dto1.setColumns(5);
    dto1.setRows(5);
    dto1.setScannable(false);
    dto1.setBoxType("STORAGE");

    BoxSizeDto dto2 = new BoxSizeDto();
    dto2.setColumns(6);
    dto2.setRows(6);
    dto2.setScannable(false);
    dto2.setBoxType("PLATE");

    List<BoxSizeDto> dtos = new ArrayList<BoxSizeDto>();
    dtos.add(dto1);
    dtos.add(dto2);
    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<BoxSize> sizes = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals(5, sizes.get(0).getRows().intValue());
    assertEquals(5, sizes.get(0).getColumns().intValue());
    assertEquals("STORAGE", sizes.get(0).getBoxType().toString());
    assertFalse(sizes.get(0).getScannable());


    assertEquals(6, sizes.get(1).getRows().intValue());
    assertEquals(6, sizes.get(1).getColumns().intValue());
    assertEquals("PLATE", sizes.get(1).getBoxType().toString());
    assertFalse(sizes.get(1).getScannable());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // box size creation is for admin only, so this test is expecting failure due to insufficent
    // permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    BoxSizeDto size2 = Dtos.asDto(currentSession().find(entityClass, 2));
    BoxSizeDto size3 = Dtos.asDto(currentSession().find(entityClass, 3));

    size2.setRows(15);
    size3.setRows(17);

    List<BoxSizeDto> dtos = new ArrayList<BoxSizeDto>();
    dtos.add(size2);
    dtos.add(size3);

    List<BoxSize> sizes =
        (List<BoxSize>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos, BoxSizeDto::getId);

    assertEquals(2L, sizes.get(0).getId());
    assertEquals(3L, sizes.get(1).getId());
    assertEquals(15, sizes.get(0).getRows().intValue());
    assertEquals(17, sizes.get(1).getRows().intValue());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    BoxSizeDto size2 = Dtos.asDto(currentSession().find(entityClass, 2));
    BoxSizeDto size3 = Dtos.asDto(currentSession().find(entityClass, 3));

    size2.setRows(15);
    size3.setRows(17);

    List<BoxSizeDto> dtos = new ArrayList<BoxSizeDto>();
    dtos.add(size2);
    dtos.add(size3);
    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteArray() throws Exception {
    testBulkDelete(entityClass, 3, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 3, CONTROLLER_BASE);
  }


}
