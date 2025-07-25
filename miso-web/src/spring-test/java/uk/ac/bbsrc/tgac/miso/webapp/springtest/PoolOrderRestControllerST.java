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

import jakarta.transaction.Transactional;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolOrder;
import uk.ac.bbsrc.tgac.miso.dto.PoolOrderDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.security.access.method.P;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class PoolOrderRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/poolorders";
  private static final Class<PoolOrder> controllerClass = PoolOrder.class;

  @Test
  public void testCreate() throws Exception {
    PoolOrderDto one = new PoolOrderDto();
    one.setAlias("one");
    one.setPurposeId(1L);
    one.setDraft(true);
    PoolOrder newPoolOrder = baseTestCreate(CONTROLLER_BASE, one, controllerClass, 200);
    assertEquals(newPoolOrder.getAlias(), one.getAlias());
  }

  @Test
  public void testUpdate() throws Exception {
    PoolOrder poolOrder = currentSession().get(controllerClass, 1);
    poolOrder.setAlias("updated");

    PoolOrder updated = baseTestUpdate(CONTROLLER_BASE, Dtos.asDto(poolOrder), 1, controllerClass);
    assertEquals(updated.getAlias(), poolOrder.getAlias());
  }

  @Test
  public void testIndexChecker() throws Exception {
    List<Long> ids = new ArrayList<Long>();
    ids.add(1001L);
    ids.add(1002L);
    ids.add(801L);
    ids.add(802L);

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/indexchecker").content(makeJson(ids)).contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(2)))
        .andExpect(jsonPath("$.duplicateIndices", hasSize(1))) // empty string, so size 1
        .andExpect(jsonPath("$.nearDuplicateIndices", hasSize(3)))
        .andExpect(jsonPath("$.nearDuplicateIndices[1]").value("AAAAGT"))
        .andExpect(jsonPath("$.nearDuplicateIndices[2]").value("AAAAAC"));
  }

  @Test
  public void testList() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/Fulfilled", Arrays.asList(2))
        .andExpect(jsonPath("$.aaData[0].alias").value("Pool Order Two"))
        .andExpect(jsonPath("$.aaData[0].orderAliquots[0].aliquot.indexIds[0]").value(13))
        .andExpect(jsonPath("$.aaData[0].orderAliquots[0].aliquot.projectId").value(500));
  }

  @Test
  public void testDeletePoolOrder() throws Exception {
    // only creator or admin can delete pool order. this user has created pool order 3
    testBulkDelete(controllerClass, 3, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    // only create or admin can delete pool order
    // admin created pool order 1
    testDeleteUnauthorized(controllerClass, 1, CONTROLLER_BASE);
  }
}
