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
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop;
import uk.ac.bbsrc.tgac.miso.dto.SopDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Sop.SopCategory;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class SopRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/sops";

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {

    SopDto sone = new SopDto();
    sone.setAlias("sop one");
    sone.setVersion("1.0");
    sone.setCategory("SAMPLE");
    sone.setUrl("http://sops.test.com/test_sop_1");
    sone.setArchived(false);

    SopDto stwo = new SopDto();
    stwo.setAlias("sop two");
    stwo.setVersion("1.0");
    stwo.setCategory("SAMPLE");
    stwo.setUrl("http://sops.test.com/test_sop_2");
    stwo.setArchived(false);

    List<SopDto> dtos = new ArrayList<SopDto>();
    dtos.add(sone);
    dtos.add(stwo);


    MvcResult mvcResult = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
        .andExpect(status().isAccepted())
        .andReturn();

    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    String response = pollingResponse(id, CONTROLLER_BASE, "/bulk/");

    Integer id1 = JsonPath.read(response, "$.data[0].id");
    Integer id2 = JsonPath.read(response, "$.data[1].id");

    assertNotNull(currentSession().get(Sop.class, id1));
    assertNotNull(currentSession().get(Sop.class, id2));
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // SOP creation is for admin only

    SopDto sone = new SopDto();
    sone.setAlias("sop one");
    sone.setVersion("1.0");
    sone.setCategory("SAMPLE");
    sone.setUrl("http://sops.test.com/test_sop_1");
    sone.setArchived(false);

    List<SopDto> dtos = new ArrayList<SopDto>();
    dtos.add(sone);

    MvcResult mvcResult = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
        .andExpect(status().isAccepted())
        .andReturn();

    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    String response = pollingResponse(id, CONTROLLER_BASE, "/bulk/");
    String status = JsonPath.read(response, "$.status");
    assertEquals("failed", status); // request should fail without admin permissions
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // the admin user made these SOPs so only admin can update them
    SopDto sampleSop = Dtos.asDto(currentSession().get(Sop.class, 1));
    SopDto librarySop = Dtos.asDto(currentSession().get(Sop.class, 3));

    sampleSop.setAlias("sampler");
    librarySop.setAlias("libraryer");

    List<SopDto> dtos = new ArrayList<SopDto>();
    dtos.add(sampleSop);
    dtos.add(librarySop);

    MvcResult mvcResult = getMockMvc()
        .perform(put(CONTROLLER_BASE + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
        .andExpect(status().isAccepted())
        .andReturn();

    String status = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.status");

    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    pollingResponse(id, CONTROLLER_BASE, "/bulk/");

    Sop updatedSampleSop = currentSession().get(Sop.class, 1);
    Sop updatedLibrarySop = currentSession().get(Sop.class, 3);

    assertNotNull(updatedSampleSop);
    assertNotNull(updatedLibrarySop);

    assertEquals("Sop not updated", "sampler", updatedSampleSop.getAlias());
    assertEquals("Sop not updated", "libraryer", updatedLibrarySop.getAlias());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteSop() throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(5L));

    assertNotNull(currentSession().get(Sop.class, 5));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-delete").contentType(MediaType.APPLICATION_JSON).content(makeJson(ids)))
        .andExpect(status().isNoContent());

    assertNull(currentSession().get(Sop.class, 5));
  }


  @Test
  public void testDeleteFail() throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(5L));

    assertNotNull(currentSession().get(Sop.class, 5));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-delete").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(ids)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testDataTableByCategory() throws Exception {
    ResultActions sampleResult = performDtRequest(CONTROLLER_BASE + "/dt/category/SAMPLE", 25, "id", 3);
    sampleResult
        .andExpect(jsonPath("$.iTotalRecords").value(2))
        .andExpect(jsonPath("$.aaData[0].alias").value("Sample SOP 1"))
        .andExpect(jsonPath("$.aaData[1].alias").value("Sample SOP 2"))
        .andExpect(jsonPath("$.aaData[0].archived").value(false))
        .andExpect(jsonPath("$.aaData[1].version").value("1.0"));


    ResultActions libraryResult = performDtRequest(CONTROLLER_BASE + "/dt/category/LIBRARY", 25, "id", 3);
    libraryResult
        .andExpect(jsonPath("$.iTotalRecords").value(3))
        .andExpect(jsonPath("$.aaData[0].alias").value("Library SOP 1"))
        .andExpect(jsonPath("$.aaData[1].alias").value("Library SOP 1"))
        .andExpect(jsonPath("$.aaData[2].archived").value(true))
        .andExpect(jsonPath("$.aaData[1].version").value("2.0"))
        .andExpect(jsonPath("$.aaData[2].alias").value("SOP to delete"));

    ResultActions runResult = performDtRequest(CONTROLLER_BASE + "/dt/category/RUN", 25, "id", 3);
    runResult
        .andExpect(jsonPath("$.iTotalRecords").value(1))
        .andExpect(jsonPath("$.aaData[0].alias").value("Run SOP 1"))
        .andExpect(jsonPath("$.aaData[0].version").value("1.0"))
        .andExpect(jsonPath("$.aaData[0].archived").value(false))
        .andExpect(jsonPath("$.aaData[0].id").value(6));

  }


}
