package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;
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
import com.jayway.jsonpath.JsonPath;

import jakarta.transaction.Transactional;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import java.util.Collections;


import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class LibraryTemplateRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/librarytemplates";
  private static final Class<LibraryTemplate> controllerClass = LibraryTemplate.class;

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2, 3));
  }

  @Test
  public void testDatatableByProject() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/project/2", Arrays.asList(1, 2));
  }

  @Test
  public void testBulkAddProject() throws Exception {

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/project/add").param("projectId", "2").content(makeJson(Arrays.asList(1, 2)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    Project proj = currentSession().get(ProjectImpl.class, 1);

    LibraryTemplate temp1 = currentSession().get(controllerClass, 1);
    LibraryTemplate temp2 = currentSession().get(controllerClass, 2);
    assertTrue(temp1.getProjects().contains(proj));
    assertTrue(temp2.getProjects().contains(proj));
  }

  @Test
  public void testBulkRemoveProject() throws Exception {
    // TODO
  }

  @Test
  public void testAddIndices() throws Exception {
    // TODO

  }

  @Test
  public void testUpdateIndices() throws Exception {
    // TODO

  }

  @Test
  public void testCreate() throws Exception {
    LibraryTemplate fam = baseTestCreate(CONTROLLER_BASE, makeCreateDtos().get(0), controllerClass, 200);
    assertEquals(fam.getAlias(), "one");
  }

  @Test
  public void testUpdate() throws Exception {
    LibraryTemplateDto testlibtemp = Dtos.asDto(currentSession().get(LibraryTemplate.class, 1));
    testlibtemp.setAlias("tester");

    LibraryTemplate updated = baseTestUpdate(CONTROLLER_BASE, testlibtemp, 1, controllerClass);
    assertEquals("tester", updated.getAlias());
  }



  private List<LibraryTemplateDto> makeCreateDtos() {

    List<LibraryTemplateDto> dtos = new ArrayList<LibraryTemplateDto>();
    LibraryTemplateDto one = new LibraryTemplateDto();
    one.setAlias("one");

    LibraryTemplateDto two = new LibraryTemplateDto();
    two.setAlias("two");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  public void testBulkCreateAsync() throws Exception {
    List<LibraryTemplate> libraryTemplates =
        baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
    assertEquals("one",libraryTemplates.get(0).getAlias());
    assertEquals("two", libraryTemplates.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update these
    LibraryTemplateDto one = Dtos.asDto(currentSession().get(LibraryTemplate.class, 1));
    LibraryTemplateDto three = Dtos.asDto(currentSession().get(LibraryTemplate.class, 3));
    one.setAlias("one");
    three.setAlias("three");

    List<LibraryTemplateDto> dtos = new ArrayList<LibraryTemplateDto>();
    dtos.add(one);
    dtos.add(three);


    List<LibraryTemplate> libraryTemplates =baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, LibraryTemplateDto::getId);
    assertEquals("one", libraryTemplates.get(0).getAlias());
    assertEquals("three", libraryTemplates.get(1).getAlias());
  }



  @Test
  public void testDeleteLibraryTemplate() throws Exception {
    testBulkDelete(controllerClass, 3, CONTROLLER_BASE);
  }


}
