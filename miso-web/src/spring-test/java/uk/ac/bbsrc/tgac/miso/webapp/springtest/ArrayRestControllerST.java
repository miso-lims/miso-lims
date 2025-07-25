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
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.dto.ArrayDto;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

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


public class ArrayRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/arrays";
  private static final Class<Array> controllerClass = Array.class;


  @Test
  public void testDtResponse() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1,2));
  }

  @Test
  public void testSave() throws Exception {
    ArrayDto saver = new ArrayDto();
    saver.setAlias("new test array");
    saver.setArrayModelId(1L);
    saver.setLastModified("2025-07-09");
    saver.setSerialNumber("1453");

    Array saved = baseTestCreate(CONTROLLER_BASE, saver, controllerClass, 201);
    assertEquals(saver.getAlias(), saved.getAlias());
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testUpdate() throws Exception {
    ArrayDto array = Dtos.asDto(currentSession().get(controllerClass, 2));
    array.setAlias("testing array");

    Array updatedArray = baseTestUpdate(CONTROLLER_BASE, array, 2, controllerClass);
    assertEquals("testing array", updatedArray.getAlias());
  }

  @Test
  public void testRemoveSample() throws Exception {
    Array updatedArray = currentSession().get(controllerClass, 1);
    assertNotNull(updatedArray.getSample("R01C01"));

    getMockMvc()
        .perform(delete(CONTROLLER_BASE + "/1/positions/R01C01").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));


    updatedArray = currentSession().get(controllerClass, 1);
    assertNull(updatedArray.getSample("R01C01"));
  }

  @Test
  public void testAddSample() throws Exception {
    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1/positions/R02C01").param("sampleId", "9").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));


    Array updatedArray = currentSession().get(controllerClass, 1);
    SampleImpl addedSample = currentSession().get(SampleImpl.class, 9);
    assertEquals(updatedArray.getSample("R02C01"), addedSample);
  }

  @Test
  public void testFindSamples() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/sample-search", "SAM8", Arrays.asList(8));
  }

  @Test
  public void testGetChangelog() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/changelog")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$[1].summary").value("SAM8 added to R01C01"))
        .andDo(print());

  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteArray() throws Exception {
    testBulkDelete(controllerClass, 2, CONTROLLER_BASE);
  }


  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 2, CONTROLLER_BASE);
  }
}
