package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.*;

import uk.ac.bbsrc.tgac.miso.core.data.BoxUse;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.BoxUseDto;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.ArrayList;

public class BoxUseRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/boxuses";
  private static final Class<BoxUse> entityClass = BoxUse.class;


  private List<BoxUseDto> makeCreateDtos() {
    BoxUseDto dto1 = new BoxUseDto();
    dto1.setAlias("use 1");

    BoxUseDto dto2 = new BoxUseDto();
    dto2.setAlias("use 2");

    List<BoxUseDto> dtos = new ArrayList<BoxUseDto>();
    dtos.add(dto1);
    dtos.add(dto2);
    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<BoxUse> uses = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("use 1", uses.get(0).getAlias());
    assertEquals("use 2", uses.get(1).getAlias());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // box use creation is for admin only, so this test is expecting failure due to insufficent
    // permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    BoxUseDto dna = Dtos.asDto(currentSession().find(entityClass, 1));
    BoxUseDto rna = Dtos.asDto(currentSession().find(entityClass, 2));

    dna.setAlias("deoxy");
    rna.setAlias("non-deoxy");

    List<BoxUseDto> dtos = new ArrayList<BoxUseDto>();
    dtos.add(dna);
    dtos.add(rna);

    List<BoxUse> uses =
        (List<BoxUse>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos, BoxUseDto::getId);

    assertEquals(1L, uses.get(0).getId());
    assertEquals(2L, uses.get(1).getId());
    assertEquals("deoxy", uses.get(0).getAlias());
    assertEquals("non-deoxy", uses.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateFail() throws Exception {
    BoxUseDto dna = Dtos.asDto(currentSession().find(entityClass, 1));
    BoxUseDto rna = Dtos.asDto(currentSession().find(entityClass, 2));

    dna.setAlias("deoxy");
    rna.setAlias("non-deoxy");

    List<BoxUseDto> dtos = new ArrayList<BoxUseDto>();
    dtos.add(dna);
    dtos.add(rna);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteArray() throws Exception {
    testBulkDelete(entityClass, 6, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 6, CONTROLLER_BASE);
  }


}
