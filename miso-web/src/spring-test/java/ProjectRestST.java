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

import uk.ac.bbsrc.tgac.miso.core.service.UserService;
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

public class ProjectRestST extends AbstractST {

  private String controllerBase = "/rest/projects";


  @Autowired
  private UserService userService;

  @Test
  public void testGetById() throws Exception {

    // remember that the mockmvc is configured to always be ok (change later for exception testing if
    // need be)

    getMockMvc().perform(get(controllerBase + "/1")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
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
        .andExpect(jsonPath("$").exists())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(17))); // 17 = number of projects in test script

  }

  @Test
  public void testGetAttachments() throws Exception {
    getMockMvc().perform(get(controllerBase + "/1/files").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists());
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "INTERNAL"})
  public void testCreate() throws Exception {

    // project impl is needed here to set the reference genome
    Project project = new ProjectImpl();
    Pipeline pipeline = (Pipeline) currentSession().get(Pipeline.class, 1L);
    project.setTitle("test title");
    project.setPipeline(pipeline);
    project.setStatus(StatusType.ACTIVE);
    ReferenceGenome referenceGenome = new ReferenceGenomeImpl();
    referenceGenome.setId(1L);
    referenceGenome.setAlias("hg19");
    project.setReferenceGenome(referenceGenome);
    project.setCreationTime(new Date());
    project.setLastModified(new Date());
    project.setName("testproj");
    project.setCode("TESTCODE");
    project.setId(0L); // unsaved project ID

    ProjectDto dto = Dtos.asDto(project, false);


    getMockMvc().perform(post(controllerBase).contentType(MediaType.APPLICATION_JSON).content(jsonMaker(dto)))
        .andExpect(status().isOk());

    assertNotNull(currentSession().get(ProjectImpl.class, 200002)); // test proper creation

  }


  private String jsonMaker(Object dto) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(dto);
    return requestJson;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "INTERNAL"})
  public void testUpdate() throws Exception {

    Project proj = currentSession().get(ProjectImpl.class, 1);
    proj.setTitle("changed testing project");
    ProjectDto dto = Dtos.asDto(proj, true);

    getMockMvc().perform(put(controllerBase + "/1").contentType(MediaType.APPLICATION_JSON).content(jsonMaker(dto)))
        .andExpect(status().isOk());


    ProjectImpl updatedProj = currentSession().get(ProjectImpl.class, 1);
    assertNotNull(updatedProj);
    assertEquals("Update didn't go through", "changed testing project", updatedProj.getTitle());
  }


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"ADMIN", "INTERNAL"})
  public void testBulkDelete() throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(7L));

    getMockMvc()
        .perform(post(controllerBase + "/bulk-delete").contentType(MediaType.APPLICATION_JSON).content(jsonMaker(ids)))
        .andDo(print())
        .andExpect(status().isNoContent());

    // now check that the project was actually deleted
    assertNull(currentSession().get(ProjectImpl.class, 7));
  }


  @Test
  public void testGetLibraryAliquots() throws Exception {
    getMockMvc().perform(get(controllerBase + "/dt").accept(MediaType.APPLICATION_JSON)
        .param("iDisplayStart", "0")
        .param("iDisplayLength", "25")
        .param("mDataProp_0", "id")
        .param("sSortDir_0", "asc")
        .param("iSortCol_0", "3")
        .param("sEcho", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists());

  }
}
