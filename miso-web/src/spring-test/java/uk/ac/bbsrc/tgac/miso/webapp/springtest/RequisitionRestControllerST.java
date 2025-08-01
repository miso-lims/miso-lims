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
import uk.ac.bbsrc.tgac.miso.dto.RequisitionDto;
import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.Collections;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.RequisitionRestController.*;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;



public class RequisitionRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/requisitions";

  private static final Class<Requisition> entityClass =  Requisition.class;

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE, Arrays.asList(1,2));
  }


  private void testAdd(int id, String type, List<Long> entityIds) throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/" + id + "/" + type).content(entityIds).contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk());
  }

  private void testRemove(int id, String type, List<Long> entityIds) throws Exception {
       getMockMvc().perform(post(CONTROLLER_BASE + "/" + id + "/" + type + "/remove").content(entityIds).contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk());

  }

  private void testMove(int id, String type, MoveItemsRequest req) throws Exception {
       getMockMvc().perform(post(CONTROLLER_BASE + "/" + id + "/" + type + "/move").content(req).contentType(MediaType.APPLICATION_JSON))
      .andDo(print())
      .andExpect(status().isOk());
  }

  @Test
  public void testCreate() throws Exception {
    RequisitionDto req = new RequisitionDto();
    req.setAlias("req1");
      
  }

  @Test
  public void testUpdate() throws Exception {

  }

  @Test
  public void testDelete() throws Exception {

  }

  @Test
  public void testDeleteFail() throws Exception {

  }

  @Test
  public void testAddSamples() throws Exception {

  }

  @Test
  public void testRemoveSamples() throws Exception {

  }

  @Test
  public void testMoveSamples() throws Exception {

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
        .andExpect(status().isOk());
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
