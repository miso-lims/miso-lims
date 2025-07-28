package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Deliverable;
import uk.ac.bbsrc.tgac.miso.dto.DeliverableDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import static org.junit.Assert.*;
import java.util.Collections;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class DeliverableRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/deliverables";
  private static final Class<Deliverable> entityClass = Deliverable.class;

  private List<DeliverableDto> makeCreateDtos() {
    DeliverableDto del1 = new DeliverableDto();
    del1.setName("del1");
    del1.setCategoryId(1L);

    DeliverableDto del2 = new DeliverableDto();
    del2.setName("del2");
    del2.setCategoryId(2L);

    List<DeliverableDto> dtos = new ArrayList<DeliverableDto>();
    dtos.add(del1);
    dtos.add(del2);
    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<Deliverable> deliverables = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("del1", deliverables.get(0).getName());
    assertEquals(1L, deliverables.get(0).getCategory().getId());
    assertEquals("del2", deliverables.get(1).getName());
    assertEquals(2L, deliverables.get(1).getCategory().getId());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    Deliverable del1 = currentSession().get(entityClass, 1);
    Deliverable del2 = currentSession().get(entityClass, 2);
    del1.setName("deliver 1");
    del2.setName("deliver 2");

    List<DeliverableDto> dtos = new ArrayList<DeliverableDto>();
    dtos.add(Dtos.asDto(del1));
    dtos.add(Dtos.asDto(del2));

    List<Deliverable> deliverables =
        (List<Deliverable>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
            Arrays.asList(1, 2));

    assertEquals(1L, deliverables.get(0).getId());
    assertEquals(2L, deliverables.get(1).getId());
    assertEquals("deliver 1", deliverables.get(0).getName());
    assertEquals("deliver 2", deliverables.get(1).getName());
  }

  @Test
  public void testBulkUpdateFail() throws Exception {
    Deliverable del1 = currentSession().get(entityClass, 1);
    Deliverable del2 = currentSession().get(entityClass, 2);
    del1.setName("deliver 1");
    del2.setName("deliver 2");

    List<DeliverableDto> dtos = new ArrayList<DeliverableDto>();
    dtos.add(Dtos.asDto(del1));
    dtos.add(Dtos.asDto(del2));
    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(entityClass, 3, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 3, CONTROLLER_BASE);
  }
}
