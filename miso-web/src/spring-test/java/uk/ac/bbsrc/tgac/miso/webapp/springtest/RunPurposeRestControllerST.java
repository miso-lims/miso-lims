package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;
import org.springframework.web.servlet.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import uk.ac.bbsrc.tgac.miso.core.data.impl.RunPurpose;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.dto.RunPurposeDto;
import static org.hamcrest.Matchers.*;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.Assert.*;
import org.springframework.test.web.servlet.MockMvc;


public class RunPurposeRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/runpurposes";
  private static final Class<RunPurpose> entityClass = RunPurpose.class;

  private List<RunPurposeDto> makeCreateDtos() {
    RunPurposeDto dto1 = new RunPurposeDto();
    dto1.setAlias("newOne");

    RunPurposeDto dto2 = new RunPurposeDto();
    dto2.setAlias("newTwo");

    return Arrays.asList(dto1, dto2);
  }

  @Test
  public void testBulkCreateAsync() throws Exception {
    List<RunPurpose> purposes = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("newOne", purposes.get(0).getAlias());
    assertEquals("newTwo", purposes.get(1).getAlias());
  }


  @Test
  public void testBulkUpdateAsync() throws Exception {
    RunPurposeDto purposeOne = Dtos.asDto(currentSession().get(entityClass, 1));
    RunPurposeDto purposeTwo = Dtos.asDto(currentSession().get(entityClass, 2));
    purposeOne.setAlias("one");
    purposeTwo.setAlias("two");

    List<RunPurpose> purposes =
        (List<RunPurpose>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass,
            Arrays.asList(purposeOne, purposeTwo), RunPurposeDto::getId);
    assertEquals(purposeOne.getAlias(), purposes.get(0).getAlias());
    assertEquals(purposeTwo.getAlias(), purposes.get(1).getAlias());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // only admin can delete
    testBulkDelete(entityClass, 3, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 3, CONTROLLER_BASE);
  }


}
