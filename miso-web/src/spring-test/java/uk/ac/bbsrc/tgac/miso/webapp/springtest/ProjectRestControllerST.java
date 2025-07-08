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

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import uk.ac.bbsrc.tgac.miso.dto.ProjectDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import org.springframework.web.servlet.View;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.ProjectRestController;

import org.springframework.security.test.context.support.WithMockUser;
import com.jayway.jsonpath.JsonPath;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ReferenceGenomeImpl;
import com.eaglegenomics.simlims.core.User;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import jakarta.transaction.Transactional;
import java.util.Date;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;

public class ProjectRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/projects";

  @Test
  public void testGetById() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.title").value("Project One"))
        .andExpect(jsonPath("$.name").value("PRO1"))
        .andExpect(jsonPath("$.code").value("PONE"))
        .andExpect(jsonPath("$.creationDate").value("2017-06-26"));
  }

  @Test
  public void testGetBySearch() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/search").param("q", "PRO1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(4)));

  }

  @Test
  public void testGetAttachments() throws Exception {
    getMockMvc().perform(get(CONTROLLER_BASE + "/1/files").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$").isEmpty()); // assert no attachments
  }

  @Test
  public void testCreate() throws Exception {

    ProjectDto project = new ProjectDto();
    project.setTitle("test title");
    project.setPipelineId(1L);
    project.setStatus("Active");
    project.setReferenceGenomeId(1L);
    project.setCode("TESTCODE");


    MvcResult result = getMockMvc()
        .perform(post(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(project)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value("TESTCODE"))
        .andReturn();

    Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    Project createdProject = currentSession().get(ProjectImpl.class, id);
    assertNotNull(createdProject); // test that project was successfully created
    assertEquals("TESTCODE", createdProject.getCode());
    assertEquals("test title", createdProject.getTitle());
  }

  @Test
  public void testUpdate() throws Exception {

    Project proj = currentSession().get(ProjectImpl.class, 1);
    ProjectDto dto = Dtos.asDto(proj, true);
    dto.setTitle("changed testing project");

    getMockMvc()
        .perform(put(CONTROLLER_BASE + "/1").contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andExpect(status().isOk());

    ProjectImpl updatedProj = currentSession().get(ProjectImpl.class, 1);
    assertNotNull(updatedProj);
    assertEquals("Update didn't go through", "changed testing project", updatedProj.getTitle());
  }

  @Test
  public void testBulkDelete() throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(7L));

    // check that the project we want to delete exists
    assertNotNull(currentSession().get(ProjectImpl.class, 7));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-delete").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(ids)))
        .andExpect(status().isNoContent());

    // now check that the project was actually deleted
    assertNull(currentSession().get(ProjectImpl.class, 7));
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(7L));

    // check that the project we want to delete exists
    assertNotNull(currentSession().get(ProjectImpl.class, 7));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-delete").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(ids)))
        .andExpect(status().isUnauthorized());
    // this user is not an admin or the project creator, so delete should be unauthorized
  }


  @Test
  public void testGetLibraryAliquots() throws Exception {
    ResultActions result = performDtRequest(CONTROLLER_BASE + "/dt", 25, "id", 3);
    result
        .andExpect(jsonPath("$.iTotalRecords").value(17)) // 17 = number of projects in test data script
        .andExpect(jsonPath("$.aaData[0].description").value("integration test project one"))
        .andExpect(jsonPath("$.aaData[10].title").value("Tubes In Boxes"))
        .andExpect(jsonPath("$.aaData[5].name").value("PRO6"))
        .andExpect(jsonPath("$.aaData[13].id").value(100001));


  }
}
