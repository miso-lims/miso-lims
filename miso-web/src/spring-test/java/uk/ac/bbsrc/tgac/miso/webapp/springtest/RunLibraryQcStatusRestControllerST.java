package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import uk.ac.bbsrc.tgac.miso.core.data.RunLibraryQcStatus;
import uk.ac.bbsrc.tgac.miso.dto.RunLibraryQcStatusDto;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;



public class RunLibraryQcStatusRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/runlibraryqcstatuses";
  private static final Class<RunLibraryQcStatus> entityClass = RunLibraryQcStatus.class;

  private List<RunLibraryQcStatusDto> makeCreateDtos() {
    RunLibraryQcStatusDto dto1 = new RunLibraryQcStatusDto();
    dto1.setQcPassed(true);
    dto1.setDescription("one");



    RunLibraryQcStatusDto dto2 = new RunLibraryQcStatusDto();
    dto2.setQcPassed(false);
    dto2.setDescription("two");

    return Arrays.asList(dto1, dto2);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    // only admin can create
    List<RunLibraryQcStatus> statuses = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("one", statuses.get(0).getDescription());
    assertEquals("two", statuses.get(1).getDescription());

    assertEquals(true, statuses.get(0).getQcPassed());
    assertEquals(false, statuses.get(1).getQcPassed());
  }

  @Test
  public void testCreateFail() throws Exception {
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update
    RunLibraryQcStatusDto statOne = Dtos.asDto(currentSession().get(entityClass, 1));
    RunLibraryQcStatusDto statTwo = Dtos.asDto(currentSession().get(entityClass, 2));
    statOne.setDescription("one");
    statTwo.setDescription("two");

    List<RunLibraryQcStatus> statuses =
        (List<RunLibraryQcStatus>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass,
            Arrays.asList(statOne, statTwo), RunLibraryQcStatusDto::getId);
    assertEquals(statOne.getDescription(), statuses.get(0).getDescription());
    assertEquals(statTwo.getDescription(), statuses.get(1).getDescription());

  }

  @Test
  public void testUpdateFail() throws Exception {
    RunLibraryQcStatusDto statOne = Dtos.asDto(currentSession().get(entityClass, 1));
    RunLibraryQcStatusDto statTwo = Dtos.asDto(currentSession().get(entityClass, 2));
    statOne.setDescription("one");
    statTwo.setDescription("two");
    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, Arrays.asList(statOne, statTwo));
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // only admin can delete
    testBulkDelete(entityClass, 4, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 4, CONTROLLER_BASE);
  }
}
