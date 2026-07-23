package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;


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
    Project proj = currentSession().find(ProjectImpl.class, 1);


    getMockMvc().perform(get(CONTROLLER_BASE + "/code/PONE")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(model().attribute("title", "Project 1"))
        .andExpect(model().attribute("project", proj));
  }

  @Test
  public void testSetupById() throws Exception {
    Project proj = currentSession().find(ProjectImpl.class, 1);

    getMockMvc().perform(get(CONTROLLER_BASE + "/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(model().attribute("title", "Project 1"))
        .andExpect(model().attribute("project", proj));

  }

}
