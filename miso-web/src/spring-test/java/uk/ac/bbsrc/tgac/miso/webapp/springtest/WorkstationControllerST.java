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
import uk.ac.bbsrc.tgac.miso.dto.WorkstationDto;
import uk.ac.bbsrc.tgac.miso.core.data.Workstation;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;


import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class WorkstationControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/workstation";

  @Test
  public void testList() throws Exception {
    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/bulk/new")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(model().hasNoErrors())
        .andExpect(model().size(3));
  }

  @Test
  public void testCreate() throws Exception {

    String alias = "Test Workstation";
    WorkstationDto workstation = new WorkstationDto();
    workstation.setAlias(alias);

    MvcResult result = getMockMvc()
        .perform(get(CONTROLLER_BASE + "/bulk/new").param("quantity", "1").param("title", "Tester")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print())
        .andReturn();

    Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    Workstation createdWorkstation = currentSession().get(Workstation.class, id);
    assertNotNull(createdWorkstation);
    assertEquals("Test Workstation", createdWorkstation.getAlias());


  }

  @Test
  public void testUpdate() throws Exception {
    Workstation work = currentSession().get(Workstation.class, 1);
    WorkstationDto workDto = Dtos.asDto(work);
    workDto.setAlias("tester");

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk/edit").param("ids", "1").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(workDto)))
        .andDo(print())
        .andExpect(status().isOk());

    Thread.sleep(1000);

    Workstation updatedWork = currentSession().get(Workstation.class, 1);
    assertNotNull(updatedWork);
    assertEquals("tester", updatedWork.getAlias()); // update not going through

  }

  @Test
  public void testViewWorkStation() throws Exception {
    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/1").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(model().hasNoErrors())
        .andExpect(model().size(8))
        .andExpect(model().attribute("title", "Workstation 1"))
        .andExpect(model().attribute("autoGenerateIdBarcodes", false));
  }

}
