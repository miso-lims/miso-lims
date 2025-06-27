package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.*;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.junit.Assert.assertNotNull;
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
  @Transactional
  public void testCreate() throws Exception {

    Project project = new ProjectImpl();
    Pipeline pipeline = (Pipeline) currentSession().get(Pipeline.class, 1L);
    project.setTitle("test title");
    project.setPipeline(pipeline);
    project.setStatus(StatusType.ACTIVE);
    ReferenceGenome referenceGenome = new ReferenceGenomeImpl();
    referenceGenome.setId(1L);
    referenceGenome.setAlias("hg19");
    project.setReferenceGenome(referenceGenome);

    User user = new UserImpl();
    user.setId(1L);
    user.setPassword("Horsebatterychainkey!"); // random password to accomodate password reqs
    user.setActive(true);
    user.setAdmin(true);
    user.setFullName("testing user");
    user.setInternal(true);
    user.setLoginName("tester");

    userService.create(user); // puts it in the db

    clearSession();


    project.setCreator(user);
    project.setCreationTime(new Date());
    project.setLastModifier(user);
    project.setLastModified(new Date());

    project.setCode("TESTCODE");
    // project.setId(0L); // unsaved ID
    project.setId(8L); // unused ID
    // project.isSaved();
    // project.isSaved();
    // project.setSaved(false);


    assertNotNull(project.getCreator()); // this passes

    ProjectDto dto = Dtos.asDto(project, false);


    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(dto);

    getMockMvc().perform(post(controllerBase).contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(print())
        .andExpect(status().isOk()).andDo(print());

    clearSession();

    // use get request to verify that the put worked (testGetById is thus a higher priority test here)
    getMockMvc().perform(get(controllerBase + "/search").param("q", "TEST").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));

  }

  @Test
  public void testUpdate() throws Exception {
    MvcResult result = getMockMvc().perform(get(controllerBase + "/1")).andReturn();


    String projJson = result.getResponse().getContentAsString();
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ProjectImpl proj = mapper.readValue(projJson, ProjectImpl.class);

    ProjectDto dto = Dtos.asDto(proj, true);

    dto.setName("changed testing project");

    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(dto);


    getMockMvc().perform(put(controllerBase + "/1").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andExpect(status().isOk());

    getMockMvc()
        .perform(
            get(controllerBase + "/search").param("q", "changed testing project").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(1)));

  }


  @Test
  @WithMockUser(username = "admin", roles = {"ADMIN", "INTERNAL"})
  public void testBulkDelete() throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L));
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(ids);

    getMockMvc()
        .perform(post(controllerBase + "/bulk-delete").contentType(MediaType.APPLICATION_JSON).content(requestJson))
        .andDo(print())
        .andExpect(status().isNoContent());


    // now check that the project actually were deleted
    getMockMvc().perform(get(controllerBase + "/search").param("q", "PRO").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(10))); // 10 = number of projects in test script post deleting projects 1-7

  }


  @Test
  public void testGetLibraryAliquots() throws Exception {
    getMockMvc().perform(get(controllerBase + "/dt").accept(MediaType.APPLICATION_JSON)).andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists());

  }
}
