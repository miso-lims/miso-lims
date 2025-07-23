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

import org.apache.commons.collections.map.MultiValueMap;
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
  private static final Class<ProjectImpl> controllerClass = ProjectImpl.class;


  @Test
  public void testGetById() throws Exception {

    baseTestGetById(CONTROLLER_BASE, 1).andExpect(jsonPath("$.title").value("Project One"))
        .andExpect(jsonPath("$.name").value("PRO1"))
        .andExpect(jsonPath("$.code").value("PONE"))
        .andExpect(jsonPath("$.creationDate").value("2017-06-26"));
  }

  @Test
  public void testGetBySearch() throws Exception {
    baseSearchByTerm(CONTROLLER_BASE + "/search", searchTerm("PRO1"), Arrays.asList(1, 100001, 110001, 120001));
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


    Project createdProject = baseTestCreate(CONTROLLER_BASE, project, controllerClass, 200);
    assertEquals("TESTCODE", createdProject.getCode());
    assertEquals("test title", createdProject.getTitle());
  }

  @Test
  public void testUpdate() throws Exception {

    Project proj = currentSession().get(ProjectImpl.class, 1);
    ProjectDto dto = Dtos.asDto(proj, true);
    dto.setTitle("changed testing project");

    ProjectImpl updatedProj = baseTestUpdate(CONTROLLER_BASE, dto, 1, controllerClass);
    assertEquals("Update didn't go through", "changed testing project", updatedProj.getTitle());
  }

  @Test
  public void testDelete() throws Exception {
    testBulkDelete(controllerClass, 7, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    // must be admin to delete project
    testDeleteUnauthorized(controllerClass, 7, CONTROLLER_BASE);
  }


  @Test
  public void testGetLibraryAliquots() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2, 3, 4, 5, 6, 7,
        100001, 110001, 120001, 200001, 200, 300, 400, 500, 4440, 2200),
        true);


  }
}
