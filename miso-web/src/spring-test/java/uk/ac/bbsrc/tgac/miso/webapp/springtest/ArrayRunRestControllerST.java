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
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.dto.ArrayRunDto;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import static org.junit.Assert.*;
import java.util.Collections;
import uk.ac.bbsrc.tgac.miso.core.data.type.HealthType;


import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;
import java.time.LocalDate;


public class ArrayRunRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/arrayruns";
  private static final Class<ArrayRun> controllerClass = ArrayRun.class;


  @Test
  public void testDtResponse() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2, 3));

  }

  @Test
  public void testDtByProj() throws Exception {
    // project 3 has the sample that is shared by the test array runs
    testDtRequest(CONTROLLER_BASE + "/dt/project/3", Arrays.asList(1, 2, 3));

  }

  @Test
  public void testDtByReq() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/requisition/1", Arrays.asList(1, 2, 3));

  }

  @Test
  public void testSave() throws Exception {

    ArrayRunDto arr = new ArrayRunDto();
    arr.setStatus("Running");
    arr.setInstrumentId(4L);
    arr.setAlias("tester");
    arr.setStartDate("2025-07-11");

    ArrayRun newArr = baseTestCreate(CONTROLLER_BASE, arr, controllerClass, 201);

    assertEquals("tester", newArr.getAlias());
  }

  @Test
  public void testUpdate() throws Exception {
    ArrayRun arr = currentSession().get(controllerClass, 1);

    arr.setAlias("modified");
    ArrayRun updatedArr = baseTestUpdate(CONTROLLER_BASE, Dtos.asDto(arr), 1, controllerClass);

    assertEquals("modified", updatedArr.getAlias());
  }

  @Test
  public void testFindArrays() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/array-search", "1234", Arrays.asList(1));

  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(controllerClass, 2, CONTROLLER_BASE);
  }


  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 2, CONTROLLER_BASE);
  }

}
