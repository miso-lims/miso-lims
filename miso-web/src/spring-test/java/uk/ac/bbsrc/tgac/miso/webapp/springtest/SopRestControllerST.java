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
import java.util.Collections;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class SopRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/sops";
  private static final Class<Sop> controllerClass = Sop.class;

  private List<SopDto> makeCreateDtos() {

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

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {

    baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // SOP creation is for admin only, so this test is expecting failure due to insufficent permission
    baseBulkCreateAsyncFail(CONTROLLER_BASE, controllerClass, makeCreateDtos());
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

    List<Sop> sops =
        (List<Sop>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, Arrays.asList(1, 3));
    assertEquals("Sop not updated", "sampler", sops.get(0).getAlias());
    assertEquals("Sop not updated", "libraryer", sops.get(1).getAlias());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteSop() throws Exception {
    baseTestDelete(controllerClass, 5, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    baseTestDeleteFail(controllerClass, 5, CONTROLLER_BASE);
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
