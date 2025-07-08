package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class LabRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/labs";

  @Test
  @WithMockUser(username = "user", password = "user", roles = {"INTERNAL"})
  public void testBulkCreateAsync() throws Exception {
    LabDto lone = new LabDto();
    lone.setAlias("lab1");

    LabDto ltwo = new LabDto();
    ltwo.setAlias("lab2");

    List<LabDto> dtos = new ArrayList<LabDto>();
    dtos.add(lone);
    dtos.add(ltwo);

    MvcResult mvcResult = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
        .andExpect(status().isAccepted())
        .andReturn();

    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    String response = pollingResponse(id);

    Integer id1 = JsonPath.read(response, "$.data[0].id");
    Integer id2 = JsonPath.read(response, "$.data[1].id");

    assertNotNull(currentSession().get(LabImpl.class, id1));
    assertNotNull(currentSession().get(LabImpl.class, id2));
  }



  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // admin permissions are not required to update, however only the creator can update a lab, which in
    // this case happens to be the admin user
    LabDto bioBank = Dtos.asDto(currentSession().get(LabImpl.class, 1));
    LabDto pathology = Dtos.asDto(currentSession().get(LabImpl.class, 2));

    bioBank.setAlias("bioBank");
    pathology.setAlias("pathology");

    List<LabDto> dtos = new ArrayList<LabDto>();
    dtos.add(bioBank);
    dtos.add(pathology);

    MvcResult mvcResult = getMockMvc()
        .perform(put(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
        .andExpect(status().isAccepted())
        .andReturn();

    String status = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.status");

    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    pollingResponse(id);

    // now check if the updates went through
    LabImpl updatedBioBank = currentSession().get(LabImpl.class, 1);
    LabImpl updatedPathology = currentSession().get(LabImpl.class, 2);

    assertNotNull(updatedBioBank);
    assertNotNull(updatedPathology);
    assertEquals("| Biobank not updated. |", "bioBank", updatedBioBank.getAlias());
    assertEquals("| Pathology not updated | ", "pathology", updatedPathology.getAlias());
  }


  private String pollingResponse(String id) throws Exception {
    String response =
        getMockMvc().perform(get(CONTROLLER_BASE + "/bulk/" + id)).andReturn().getResponse().getContentAsString();
    String status = JsonPath.read(response, "$.status");
    while (status.equals("running")) {
      response =
          getMockMvc().perform(get(CONTROLLER_BASE + "/bulk/" + id)).andReturn().getResponse().getContentAsString();
      status = JsonPath.read(response, "$.status");
      Thread.sleep(1000);
    }
    return response;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLab() throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(3L)); // lab 3 is the designated deletable lab in the test data

    assertNotNull(currentSession().get(LabImpl.class, 3)); // first check that it exists

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-delete").contentType(MediaType.APPLICATION_JSON).content(makeJson(ids)))
        .andExpect(status().isNoContent());

    // now check that the lab was actually deleted
    assertNull(currentSession().get(LabImpl.class, 3));
  }


  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(3L));

    // check that the lab we want to delete exists
    assertNotNull(currentSession().get(LabImpl.class, 3));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-delete").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(ids)))
        .andExpect(status().isUnauthorized());
    // this user is not an admin or the lab creator, so delete should be unauthorized
  }


}
