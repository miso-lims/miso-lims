package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import org.springframework.web.servlet.*;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRun;
import uk.ac.bbsrc.tgac.miso.core.data.Array;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayRunSample.ArrayRunSampleId;
import uk.ac.bbsrc.tgac.miso.dto.ArrayRunDto;
import uk.ac.bbsrc.tgac.miso.dto.ArrayRunSampleDto;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

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
    ArrayRunDto arr = Dtos.asDto(currentSession().find(controllerClass, 1));

    arr.setAlias("modified");
    ArrayRun updatedArr = baseTestUpdate(CONTROLLER_BASE, arr, 1, controllerClass);

    assertEquals("modified", updatedArr.getAlias());
  }

  @Test
  public void testFindArrays() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/array-search", "1234", List.of(1));

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
        .andExpect(jsonPath("$.iTotalRecords").value(1))
        .andExpect(jsonPath("$.iTotalDisplayRecords").value(1))
        .andExpect(jsonPath("$.sEcho").value(1))
        .andExpect(jsonPath("$.aaData", hasSize(1)))
        .andExpect(jsonPath("$.aaData[0].position").value("R01C01"))
        .andExpect(jsonPath("$.aaData[0].sampleAlias").value("TEST_0001_Bn_R_nn_1-1_D_1"))
        .andExpect(jsonPath("$.aaData[0].qcStatusId").value(1))
        .andExpect(jsonPath("$.aaData[0].qcNote").value("remove qc"))
        .andExpect(jsonPath("$.aaData[0].qcUserName").value("user"));
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
        .content(makeJsonForGenericList(List.of(dto)))
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    currentSession().clear();

    ArrayRun run = currentSession().find(ArrayRun.class, 1L);
    Array array = currentSession().find(Array.class, 1L);
    SampleImpl sample = currentSession().find(SampleImpl.class, 8L);
    ArrayRunSample saved =
        currentSession().find(ArrayRunSample.class, new ArrayRunSampleId(run, array, "R01C01", sample));

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
    ArrayRunDto arr = Dtos.asDto(currentSession().find(controllerClass, 2));
    arr.setArrayId(null);

    ArrayRun run = currentSession().find(controllerClass, 2L);
    Array array = currentSession().find(Array.class, 1L);
    SampleImpl sample1 = currentSession().find(SampleImpl.class, 8L);
    ArrayRunSampleId id1 = new ArrayRunSampleId(run, array, "R01C01", sample1);
    assertNotNull(currentSession().find(ArrayRunSample.class, id1));

    ArrayRun updatedArr = baseTestUpdate(CONTROLLER_BASE, arr, 2, controllerClass);

    assertNull(updatedArr.getArray());
    currentSession().clear();
    assertNull(currentSession().find(ArrayRunSample.class, id1));
  }

}
