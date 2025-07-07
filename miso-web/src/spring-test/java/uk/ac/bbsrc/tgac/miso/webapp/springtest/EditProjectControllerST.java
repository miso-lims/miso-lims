package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.*;
import org.springframework.test.context.web.WebAppConfiguration;
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

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.dto.ProjectDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import org.springframework.web.servlet.View;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.ProjectRestController;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;

import uk.ac.bbsrc.tgac.miso.core.data.ReferenceGenome;
import uk.ac.bbsrc.tgac.miso.core.data.impl.Pipeline;
import java.util.Date;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import jakarta.transaction.Transactional;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;


public class EditProjectControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/project";


  @Test
  public void testNewProject() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/new").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(model().attribute("title", "New Project"));
  }


  @Test
  public void testEditByShortName() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/shortname/PONE")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl("/project/code/PONE"));
  }

  @Test
  public void testByProjectCode() throws Exception {
    Project proj = currentSession().get(ProjectImpl.class, 1);


    getMockMvc().perform(get(CONTROLLER_BASE + "/code/PONE")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(model().attribute("title", "Project 1"))
        .andExpect(model().attribute("project", proj));
  }

  @Test
  public void testSetupById() throws Exception {
    Project proj = currentSession().get(ProjectImpl.class, 1);

    getMockMvc().perform(get(CONTROLLER_BASE + "/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(model().attribute("title", "Project 1"))
        .andExpect(model().attribute("project", proj));

  }

}
