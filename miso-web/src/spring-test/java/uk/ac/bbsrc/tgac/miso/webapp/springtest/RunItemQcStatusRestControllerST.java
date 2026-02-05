package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import uk.ac.bbsrc.tgac.miso.core.data.RunItemQcStatus;
import uk.ac.bbsrc.tgac.miso.dto.RunItemQcStatusDto;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;



public class RunItemQcStatusRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/RunItemQcStatuses";
  private static final Class<RunItemQcStatus> entityClass = RunItemQcStatus.class;

  private List<RunItemQcStatusDto> makeCreateDtos() {
    RunItemQcStatusDto dto1 = new RunItemQcStatusDto();
    dto1.setQcPassed(true);
    dto1.setDescription("one");



    RunItemQcStatusDto dto2 = new RunItemQcStatusDto();
    dto2.setQcPassed(false);
    dto2.setDescription("two");

    return Arrays.asList(dto1, dto2);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    // only admin can create
    List<RunItemQcStatusDto> dtos = makeCreateDtos();
    List<RunItemQcStatus> statuses = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, dtos);
    assertEquals(dtos.get(0).getDescription(), statuses.get(0).getDescription());
    assertEquals(dtos.get(1).getDescription(), statuses.get(1).getDescription());

    assertEquals(dtos.get(0).getQcPassed(), statuses.get(0).getQcPassed());
    assertEquals(dtos.get(1).getQcPassed(), statuses.get(1).getQcPassed());
  }

  @Test
  public void testCreateFail() throws Exception {
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update
    RunItemQcStatusDto statOne = Dtos.asDto(currentSession().get(entityClass, 1));
    RunItemQcStatusDto statTwo = Dtos.asDto(currentSession().get(entityClass, 2));
    statOne.setDescription("one");
    statTwo.setDescription("two");

    List<RunItemQcStatus> statuses = baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass,
        Arrays.asList(statOne, statTwo), RunItemQcStatusDto::getId);
    assertEquals(statOne.getDescription(), statuses.get(0).getDescription());
    assertEquals(statTwo.getDescription(), statuses.get(1).getDescription());

  }

  @Test
  public void testUpdateFail() throws Exception {
    RunItemQcStatusDto statOne = Dtos.asDto(currentSession().get(entityClass, 1));
    RunItemQcStatusDto statTwo = Dtos.asDto(currentSession().get(entityClass, 2));
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
