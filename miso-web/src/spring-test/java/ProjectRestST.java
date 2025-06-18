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


import uk.ac.bbsrc.tgac.miso.core.data.Project;
import org.springframework.web.servlet.View;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.ProjectRestController;

import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;

import org.springframework.test.web.servlet.MockMvc;

// might need webappconfig annotation here
public class ProjectRestST extends AbstractST {

  @Test
  public void testGetById() throws Exception {

    // remember that the mockmvc is configured to always be ok (change later for exception testing if
    // need be)

    getMockMvc().perform(get("/rest/projects/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(print());

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
