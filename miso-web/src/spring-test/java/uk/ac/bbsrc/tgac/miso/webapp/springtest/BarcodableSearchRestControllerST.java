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


public class BarcodableSearchRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/barcodables";

  public void testSearch(String barcode, String entityType, int id) throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/search").param("q", barcode).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(id))
        .andExpect(jsonPath("$[0].entityType").value(entityType));
  }

  @Test
  public void searchForSample() throws Exception {
    testSearch("11111", "SAMPLE", 1);
  }

  @Test
  public void searchForPool() throws Exception {
    testSearch("12341", "POOL", 1);

  }

  @Test
  public void searchForLibraryAliquot() throws Exception {
    testSearch("12321", "LIBRARY_ALIQUOT", 1);
  }

  @Test
  public void searchForLibrary() throws Exception {
    testSearch("11211", "LIBRARY", 1);
  }

  @Test
  public void searchForContainer() throws Exception {
    testSearch("MISEQXX", "CONTAINER", 1);
  }


  @Test
  public void searchForBox() throws Exception {
    testSearch("19841", "BOX", 1);
  }
}
