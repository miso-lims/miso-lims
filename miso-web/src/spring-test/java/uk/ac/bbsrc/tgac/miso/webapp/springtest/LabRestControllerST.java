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
import uk.ac.bbsrc.tgac.miso.core.data.Lab;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LabImpl;
import uk.ac.bbsrc.tgac.miso.dto.LabDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

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


public class LabRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/labs";
  private static final Class<LabImpl> controllerClass = LabImpl.class;

  @Test
  public void testBulkCreateAsync() throws Exception {
    LabDto lone = new LabDto();
    lone.setAlias("lab1");

    LabDto ltwo = new LabDto();
    ltwo.setAlias("lab2");

    List<LabDto> dtos = new ArrayList<LabDto>();
    dtos.add(lone);
    dtos.add(ltwo);

    abstractTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, dtos);
  }



  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // admin permissions are not required to update, however only the creator can update a lab, which in
    // this case happens to be the admin user
    LabDto bioBank = Dtos.asDto(currentSession().get(controllerClass, 1));
    LabDto pathology = Dtos.asDto(currentSession().get(controllerClass, 2));

    bioBank.setAlias("bioBank");
    pathology.setAlias("pathology");

    List<LabDto> dtos = new ArrayList<LabDto>();
    dtos.add(bioBank);
    dtos.add(pathology);

    List<LabImpl> labs =
        (List<LabImpl>) abstractTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, Arrays.asList(1, 2));

    assertEquals("| Biobank not updated. |", "bioBank", labs.get(0).getAlias());
    assertEquals("| Pathology not updated | ", "pathology", labs.get(1).getAlias());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLab() throws Exception {
    abstractTestDelete(controllerClass, 3, CONTROLLER_BASE);
  }


  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    abstractTestDeleteFail(controllerClass, 3, CONTROLLER_BASE);
  }


}
