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
import uk.ac.bbsrc.tgac.miso.core.data.DetailedQcStatus;
import uk.ac.bbsrc.tgac.miso.core.data.impl.DetailedQcStatusImpl;
import uk.ac.bbsrc.tgac.miso.dto.DetailedQcStatusDto;

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


public class DetailedQcStatusRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/detailedqcstatuses";
  private static final Class<DetailedQcStatusImpl> controllerClass = DetailedQcStatusImpl.class;

  private List<DetailedQcStatusDto> makeCreateDtos() {
    DetailedQcStatusDto stat1 = new DetailedQcStatusDto();
    stat1.setArchived(false);
    stat1.setDescription("status 1");
    stat1.setNoteRequired(true);
    stat1.setStatus(true);

    DetailedQcStatusDto stat2 = new DetailedQcStatusDto();
    stat2.setArchived(false);
    stat2.setDescription("status 2");
    stat2.setNoteRequired(false);
    stat2.setStatus(false);

    List<DetailedQcStatusDto> dtos = new ArrayList<DetailedQcStatusDto>();
    dtos.add(stat1);
    dtos.add(stat2);
    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    // must be admin to update
    List<DetailedQcStatusImpl> statuses = baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
    assertEquals("status 1", statuses.get(0).getDescription());
    assertEquals("status 2", statuses.get(1).getDescription());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // must be admin to update
    DetailedQcStatusImpl str = currentSession().get(controllerClass, 5);
    DetailedQcStatusImpl diagnosis = currentSession().get(controllerClass, 6);
    str.setDescription("STR");
    diagnosis.setDescription("diagnosis");


    List<DetailedQcStatusDto> dtos = new ArrayList<DetailedQcStatusDto>();
    dtos.add(Dtos.asDto(str));
    dtos.add(Dtos.asDto(diagnosis));


    List<DetailedQcStatusImpl> detailedQcStatuses =
        (List<DetailedQcStatusImpl>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos,
            Arrays.asList(5, 6));

    assertEquals("STR", detailedQcStatuses.get(0).getDescription());
    assertEquals("diagnosis", detailedQcStatuses.get(1).getDescription());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    DetailedQcStatusImpl str = currentSession().get(controllerClass, 5);
    DetailedQcStatusImpl diagnosis = currentSession().get(controllerClass, 6);
    str.setDescription("STR");
    diagnosis.setDescription("diagnosis");

    List<DetailedQcStatusDto> dtos = new ArrayList<DetailedQcStatusDto>();
    dtos.add(Dtos.asDto(str));
    dtos.add(Dtos.asDto(diagnosis));

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(controllerClass, 10, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 10, CONTROLLER_BASE);
  }
}
