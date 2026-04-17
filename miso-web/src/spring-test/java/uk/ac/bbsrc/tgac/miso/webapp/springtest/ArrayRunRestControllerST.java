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
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample.ArrayRunSampleId;
import uk.ac.bbsrc.tgac.miso.dto.ArrayRunDto;
import uk.ac.bbsrc.tgac.miso.dto.ArrayRunSampleDto;
import uk.ac.bbsrc.tgac.miso.core.data.Instrument;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

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
    ArrayRunDto arr = Dtos.asDto(currentSession().get(controllerClass, 1));

    arr.setAlias("modified");
    ArrayRun updatedArr = baseTestUpdate(CONTROLLER_BASE, arr, 1, controllerClass);

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

  @Test
  public void testListSamplesWithQc() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/samples")
        .param("iDisplayStart", "0")
        .param("iDisplayLength", "25")
        .param("iSortCol_0", "0")
        .param("mDataProp_0", "position")
        .param("sSortDir_0", "asc")
        .param("sEcho", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.iTotalRecords").value(2))
        .andExpect(jsonPath("$.iTotalDisplayRecords").value(2))
        .andExpect(jsonPath("$.sEcho").value(1))
        .andExpect(jsonPath("$.aaData", hasSize(2)))
        .andExpect(jsonPath("$.aaData[0].position").value("R01C01"))
        .andExpect(jsonPath("$.aaData[0].sampleAlias").value("TEST_0001_Bn_R_nn_1-1_D_1"))
        .andExpect(jsonPath("$.aaData[0].qcStatusId").value(1))
        .andExpect(jsonPath("$.aaData[0].qcNote").value("remove qc"))
        .andExpect(jsonPath("$.aaData[0].qcUserName").value("user"))
        .andExpect(jsonPath("$.aaData[1].position").value("R02C01"))
        .andExpect(jsonPath("$.aaData[1].qcNote").value("keep qc"));
  }

  @Test
  public void testSaveSamplesSetsQc() throws Exception {
    ArrayRunSampleDto dto = new ArrayRunSampleDto();
    dto.setArrayRunId(1L);
    dto.setArrayId(1L);
    dto.setPosition("R01C01");
    dto.setSampleId(8L);
    dto.setQcStatusId(3L);
    dto.setQcNote("saved from st");

    getMockMvc().perform(put(CONTROLLER_BASE + "/1/samples")
        .contentType(MediaType.APPLICATION_JSON)
        .content(makeJsonForGenericList(Arrays.asList(dto)))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    currentSession().clear();

    ArrayRun run = currentSession().get(ArrayRun.class, 1L);
    Array array = currentSession().get(Array.class, 1L);
    SampleImpl sample = currentSession().get(SampleImpl.class, 8L);
    ArrayRunSample saved =
        currentSession().get(ArrayRunSample.class, new ArrayRunSampleId(run, array, "R01C01", sample));

    assertNotNull(saved);
    assertNotNull(saved.getQcStatus());
    assertEquals(3L, saved.getQcStatus().getId());
    assertEquals("saved from st", saved.getQcNote());
    assertNotNull(saved.getQcUser());
    assertEquals(3L, saved.getQcUser().getId());
    assertNotNull(saved.getQcDate());
  }

  @Test
  public void testListSamplesDifferentRunQc() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/samples")
        .param("iDisplayStart", "0")
        .param("iDisplayLength", "25")
        .param("iSortCol_0", "0")
        .param("mDataProp_0", "position")
        .param("sSortDir_0", "asc")
        .param("sEcho", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.aaData[0].sampleId").value(8))
        .andExpect(jsonPath("$.aaData[0].qcStatusId").value(1))
        .andExpect(jsonPath("$.aaData[0].qcNote").value("remove qc"));

    getMockMvc().perform(get(CONTROLLER_BASE + "/3/samples")
        .param("iDisplayStart", "0")
        .param("iDisplayLength", "25")
        .param("iSortCol_0", "0")
        .param("mDataProp_0", "position")
        .param("sSortDir_0", "asc")
        .param("sEcho", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.aaData[0].position").value("R01C01"))
        .andExpect(jsonPath("$.aaData[0].sampleId").value(8))
        .andExpect(jsonPath("$.aaData[0].qcStatusId").value(3))
        .andExpect(jsonPath("$.aaData[0].qcNote").value("same sample different run"));
  }

  @Test
  public void testUpdateRemoveArrayClearsSamples() throws Exception {
    ArrayRunDto arr = Dtos.asDto(currentSession().get(controllerClass, 2));
    arr.setArrayId(null);

    ArrayRun run = currentSession().get(controllerClass, 2L);
    Array array = currentSession().get(Array.class, 1L);
    SampleImpl sample1 = currentSession().get(SampleImpl.class, 8L);
    SampleImpl sample2 = currentSession().get(SampleImpl.class, 9L);
    ArrayRunSampleId id1 = new ArrayRunSampleId(run, array, "R01C01", sample1);
    ArrayRunSampleId id2 = new ArrayRunSampleId(run, array, "R02C01", sample2);
    assertNotNull(currentSession().get(ArrayRunSample.class, id1));
    assertNotNull(currentSession().get(ArrayRunSample.class, id2));

    ArrayRun updatedArr = baseTestUpdate(CONTROLLER_BASE, arr, 2, controllerClass);

    assertNull(updatedArr.getArray());
    currentSession().clear();
    assertNull(currentSession().get(ArrayRunSample.class, id1));
    assertNull(currentSession().get(ArrayRunSample.class, id2));

    getMockMvc().perform(get(CONTROLLER_BASE + "/2/samples")
        .param("iDisplayStart", "0")
        .param("iDisplayLength", "25")
        .param("iSortCol_0", "0")
        .param("mDataProp_0", "position")
        .param("sSortDir_0", "asc")
        .param("sEcho", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.iTotalRecords").value(0))
        .andExpect(jsonPath("$.iTotalDisplayRecords").value(0))
        .andExpect(jsonPath("$.aaData", hasSize(0)));
  }

}
