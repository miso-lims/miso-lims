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

import org.springframework.security.test.context.support.WithMockUser;
import static org.junit.Assert.*;
import java.util.Collections;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;



public class BoxableRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/boxables";

  @Test
  public void testSearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/search", "SAM1", Arrays.asList(1));
  }

  @Test
  public void testQueryByBox() throws Exception {
    // queries for a specific box and returns the contents

    List<String> boxNames = new ArrayList<String>();
    boxNames.add("BOX1");
    List<Integer> ids = Arrays.asList(205, 206, 204, 2, 3, 4, 7, 8, 1, 1, 1);
    List<String> entityTypes = Arrays.asList("SAMPLE", "SAMPLE", "SAMPLE", "SAMPLE", "SAMPLE", "SAMPLE", "SAMPLE",
        "SAMPLE", "LIBRARY", "LIBRARY_ALIQUOT", "POOL");

    ResultActions ac = getMockMvc()
        .perform(post(CONTROLLER_BASE + "/query-by-box").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(makeJson(boxNames)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(11)));

    for (int i = 0; i < ids.size(); i++) {
      ac = ac.andExpect(jsonPath("$[" + i + "].id").value(ids.get(i)));
      ac = ac.andExpect(jsonPath("$[" + i + "].entityType").value(entityTypes.get(i)));

    }
  }

  public void testQueryDoesntExist() throws Exception {
    List<String> boxNames = Arrays.asList("fake");
    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/query-by-box").contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(makeJson(boxNames)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(0)));
  }
}
