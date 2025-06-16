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

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import org.springframework.web.servlet.View;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.ProjectRestController;

import static org.junit.Assert.*;

import org.springframework.test.web.servlet.MockMvc;

@WebAppConfiguration
public class ProjectRestST extends AbstractST {


  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext wac;


  @Before
  public void setup() {
    // config for now
    this.mockMvc = webAppContextSetup(wac)
        .defaultRequest(get("/").contextPath("/rest").servletPath("/projects").accept(MediaType.APPLICATION_JSON))
        .alwaysExpect(status().isOk()).alwaysExpect(content().contentType("application/json;charset=UTF-8"))
        .build();
  }

  @Test
  public void initTest() {
    assertNotNull(wac);
  }


  @Test
  public void testGetById() throws Exception {

    // remember that the mockmvc is configured to always be ok (change later for exception testing if
    // need be)
    mockMvc.perform(get("/rest/projects/1")).andDo(print());

    // MvcResult testProj = this.mockMvc.perform(get("/rest/projects/1")).getResponse();;

    // Project testProj = mmockMvc.perform(get("/rest/projects/1")).;

    // assertNotNull(testProj);
    // // assertTrue(testProj.getName().equals("PRO1"));
    // // assertTrue(testProj.getDescription().equals("Test project"));
    // // assertTrue(testProj.getCreationTime().equals(Date("2015-08-27 15:40:15")));
    // // assertTrue(testProj.getTitle().equals("TEST1"));
    // // assertTrue(testProj.getCode().equals("TEST1"));
    // // assertTrue(testProj.getLastModified().equals(testProj))
  }



}
