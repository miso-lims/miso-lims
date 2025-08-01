package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;


import static org.hamcrest.Matchers.*;


import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import uk.ac.bbsrc.tgac.miso.core.data.impl.Requisition;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;

import uk.ac.bbsrc.tgac.miso.dto.RequisitionDto;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.Collections;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RequisitionRestController.*;
import java.util.List;
import java.util.function.Function;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;



public class RequisitionRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/requisitions";

  private static final Class<Requisition> entityClass = Requisition.class;

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2));
  }

  private void testMove(int id, String type, MoveItemsRequest req) throws Exception {
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/" + id + "/" + type + "/move").content(makeJson(req))
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk());

    // idk what this does
  }

  @Test
  public void testCreate() throws Exception {
    RequisitionDto stoppedReq = new RequisitionDto();
    stoppedReq.setAlias("req2");
    stoppedReq.setStopped(true);
    stoppedReq.setStopReason("too expensive");

    Requisition newReq = baseTestCreate(CONTROLLER_BASE, stoppedReq, entityClass, 201);
    assertEquals(stoppedReq.getAlias(), newReq.getAlias());
    assertEquals(stoppedReq.isStopped(), newReq.isStopped());
    assertEquals(stoppedReq.getStopReason(), newReq.getStopReason());

  }

  @Test
  public void testUpdate() throws Exception {
    RequisitionDto dto = RequisitionDto.from(currentSession().get(entityClass, 1));
    dto.setAlias("updated");

    Requisition updated = baseTestUpdate(CONTROLLER_BASE, dto, 1, entityClass);
    assertEquals(dto.getAlias(), updated.getAlias());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    // must be admin or creator of requisition to delete
    testBulkDelete(entityClass, 1, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 1, CONTROLLER_BASE);
  }

  @Test
  public void testAddSamples() throws Exception {
    testAddAsync(2L, CONTROLLER_BASE + "/2/samples", Arrays.asList(3L), SampleImpl.class,
        CONTROLLER_BASE + "/samplesupdate",
        SampleImpl::getRequisition);
  }

  @Test
  public void testRemoveSamples() throws Exception {
    testRemoveAsync(1L, CONTROLLER_BASE + "/1/samples/remove", Arrays.asList(2L), SampleImpl.class,
        CONTROLLER_BASE + "/samplesupdate",
        SampleImpl::getRequisition);

  }

  @Test
  public void testMoveSamples() throws Exception {
    MoveItemsRequest req = new MoveItemsRequest(1L, "Req One", 1L, false, "N/A", Arrays.asList(2L));
    testMove(2, "samples", req);

  }

  @Test
  public void testAddSupplementalSamples() throws Exception {

  }

  @Test
  public void testRemoveSupplementalSamples() throws Exception {

  }

  @Test
  public void testAddLibraries() throws Exception {

  }

  @Test
  public void testRemoveLibraries() throws Exception {

  }

  @Test
  public void testMoveLibraries() throws Exception {

  }

  @Test
  public void testAddSupplementalLibraries() throws Exception {

  }

  @Test
  public void testRemoveSupplementalLibraries() throws Exception {

  }

  @Test
  public void testSearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/search", "Req One", Arrays.asList(1));

  }

  @Test
  public void testListRunLibraries() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/runlibraries"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(1)))
        .andExpect(jsonPath("$[0].runId").value(1))
        .andExpect(jsonPath("$[0].runAlias").value("MiSeq_Run_1"));
  }


  @Test
  public void testSearchPaused() throws Exception {

  }

  @Test
  public void testBulkResume() throws Exception {

  }

  @Test
  public void testGetRequisitionProgress() throws Exception {

  }


}
