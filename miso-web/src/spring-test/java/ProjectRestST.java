package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.*;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;


import uk.ac.bbsrc.tgac.miso.core.data.Project;
import org.springframework.web.servlet.View;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.ProjectRestController;

import static org.junit.Assert.*;

import org.springframework.test.web.servlet.MockMvc;

public class ProjectRestST extends AbstractST {

  private String controllerBase = "/rest/projects";

  @Test
  public void testGetById() throws Exception {

    // remember that the mockmvc is configured to always be ok (change later for exception testing if
    // need be)

    getMockMvc().perform(get(controllerBase + "/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value("Project One"))
        .andExpect(jsonPath("$.name").value("PRO1"))
        .andExpect(jsonPath("$.code").value("PONE"))
        .andExpect(jsonPath("$.creationDate").value("2017-06-26"));// .andDo(print()).andReturn(); // for testing
  }

  @Test
  public void testGetBySearch() throws Exception {
    getMockMvc().perform(get(controllerBase + "/search").param("q", "PRO").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(17)));

  }

}
