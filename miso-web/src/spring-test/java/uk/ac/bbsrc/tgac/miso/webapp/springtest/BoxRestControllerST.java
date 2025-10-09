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

/**
 * Integration tests for BoxRestController
 * Tests CRUD operations and search functionality for Box entities
 */
public class BoxRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/boxes";

  @Test
  public void testGetBox() throws Exception {
    // Test retrieving a single box by ID
    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/1")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1));
  }

  @Test
  public void testGetBoxNotFound() throws Exception {
    // Test retrieving non-existent box returns 404
    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/999999")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound());
  }

  @Test
  public void testListAllBoxes() throws Exception {
    // Test listing all boxes
    getMockMvc()
        .perform(get(CONTROLLER_BASE)
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  public void testSearchByAlias() throws Exception {
    // Test searching boxes by alias
    // Using inherited method from AbstractST
    baseSearchByTerm(CONTROLLER_BASE + "/search", "BOX", Collections.emptyList());
  }

  @Test
  public void testSearchByAliasPattern() throws Exception {
    // Test search that returns results
    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/search")
            .param("q", "BOX")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  public void testSearchNoResults() throws Exception {
    // Test search with no matching results
    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/search")
            .param("q", "NONEXISTENT_BOX_XXXXXX_12345")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.*", hasSize(0)));
  }

  @Test
  public void testGetBoxContents() throws Exception {
    // Test retrieving the contents/items stored in a box
    // Box 1 should exist in test data
    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/1/contents")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  public void testGetBoxSizes() throws Exception {
    // Test retrieving available box sizes
    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/sizes")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  public void testGetBoxUses() throws Exception {
    // Test retrieving available box use types
    getMockMvc()
        .perform(get(CONTROLLER_BASE + "/uses")
            .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }
}