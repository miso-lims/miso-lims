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
import org.springframework.test.web.servlet.ResultActions;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.Project;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryTemplate;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ProjectImpl;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateDto;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTemplateIndexDto;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;

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
  private static final Class<LibraryTemplate> entityClass = LibraryTemplate.class;

  private List<LibraryTemplateDto> makeCreateDtos() {

      LibraryTemplateDto dto1 = new LibraryTemplateDto();
      dto1.setAlias("Template one");
      dto1.setLibraryTypeId(1L);
      dto1.setSelectionId(1L);
      dto1.setPlatformType(PlatformType.ILLUMINA.name());
      dto1.setDefaultVolume("10.5");
      dto1.setVolumeUnits(VolumeUnit.MICROLITRES.name());
      dto1.setIndexFamilyId(1L);

      LibraryTemplateDto dto2 = new LibraryTemplateDto();
      dto2.setAlias("Template two");
      dto2.setLibraryTypeId(1L);
      dto2.setSelectionId(1L);
      dto2.setPlatformType(PlatformType.ILLUMINA.name());
      dto2.setDefaultVolume("20.0");
      dto2.setVolumeUnits(VolumeUnit.MICROLITRES.name());
      dto2.setIndexFamilyId(1L);

      return Arrays.asList(dto1, dto2);
    }

  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt", Arrays.asList(1, 2, 3));
  }

  @Test
  public void testDatatableByProject() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/project/1", Arrays.asList(1, 2));
  }

  @Test
  public void testBulkCreateAsync() throws Exception {
      List<LibraryTemplate> libraryTemplates =
              baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
      assertEquals("Template one",libraryTemplates.get(0).getAlias());
      assertEquals(1L, libraryTemplates.get(0).getLibraryType().getId());
      assertEquals(10.5, libraryTemplates.get(0).getDefaultVolume().doubleValue(), 0.0);
      assertEquals(PlatformType.ILLUMINA, libraryTemplates.get(0).getPlatformType());

      assertEquals("Template two", libraryTemplates.get(1).getAlias());
      assertEquals(20, libraryTemplates.get(1).getDefaultVolume().doubleValue(), 0.0);
  }

  @Test
  public void testBulkUpdateAsync() throws Exception {
      LibraryTemplateDto one = Dtos.asDto(currentSession().get(LibraryTemplate.class, 1));
      LibraryTemplateDto three = Dtos.asDto(currentSession().get(LibraryTemplate.class, 3));
      one.setAlias("one");
      three.setAlias("three");

      List<LibraryTemplateDto> dtos = new ArrayList<LibraryTemplateDto>();
      dtos.add(one);
      dtos.add(three);

      List<LibraryTemplate> libraryTemplates =baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos, LibraryTemplateDto::getId);
      assertEquals("one", libraryTemplates.get(0).getAlias());
      assertEquals("three", libraryTemplates.get(1).getAlias());
  }


  @Test
  public void testCreate() throws Exception {
      LibraryTemplate fam = baseTestCreate(CONTROLLER_BASE, makeCreateDtos().get(0), entityClass, 200);
      assertEquals(fam.getAlias(), "Template one");
  }

  @Test
  public void testUpdate() throws Exception {
      LibraryTemplateDto testlibtemp = Dtos.asDto(currentSession().get(LibraryTemplate.class, 1));
      testlibtemp.setAlias("tester");

      LibraryTemplate updated = baseTestUpdate(CONTROLLER_BASE, testlibtemp, 1, entityClass);
      assertEquals("tester", updated.getAlias());
  }

  @Test
  public void testBulkAddProject() throws Exception {

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/project/add").param("projectId", "2").content(makeJson(Arrays.asList(1, 2)))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    Project proj = currentSession().get(ProjectImpl.class, 1);

    LibraryTemplate temp1 = currentSession().get(entityClass, 1);
    LibraryTemplate temp2 = currentSession().get(entityClass, 2);
    assertTrue(temp1.getProjects().contains(proj));
    assertTrue(temp2.getProjects().contains(proj));
  }

  @Test
  public void testBulkRemoveProject() throws Exception {
      getMockMvc()
              .perform(post(CONTROLLER_BASE + "/project/remove").param("projectId", "1").content(makeJson(Arrays.asList(1)))
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isNoContent());

      Project proj = currentSession().get(ProjectImpl.class, 1);
      LibraryTemplate temp = currentSession().get(entityClass, 1);

      assertTrue(temp.getProjects().stream().noneMatch(p -> p.getId() == 1));
  }

  @Test
  public void testBulkDelete() throws Exception{
      LibraryTemplateDto dto = makeCreateDtos().get(0);
      LibraryTemplate created = baseTestCreate(CONTROLLER_BASE, dto, entityClass, 200);
      long id = created.getId();

      assertNotNull(currentSession().get(entityClass, id));

      getMockMvc()
              .perform(post(CONTROLLER_BASE + "/bulk-delete").content(makeJson(Arrays.asList(id)))
                      .contentType(MediaType.APPLICATION_JSON))
              .andExpect(status().isNoContent());
      assertNull(currentSession().get(entityClass, id));
  }

  @Test
  public void testAddIndices() throws Exception {
    long templateId = 1L;

    LibraryTemplateIndexDto indexDto = new LibraryTemplateIndexDto();
    indexDto.setBoxPosition("A03");
    indexDto.setIndex1Id(3L);

    pollingResponserHelper("post", Arrays.asList(indexDto), CONTROLLER_BASE + "/" + templateId + "/indices", CONTROLLER_BASE + "/bulk/", 202);

    LibraryTemplate template = currentSession().get(entityClass, templateId);
    assertNotNull(template.getIndexOnes().get("A03"));
    assertEquals(3L, template.getIndexOnes().get("A03").getId());
  }

  @Test
  public void testUpdateIndices() throws Exception {
      long templateId = 1L;

      LibraryTemplateIndexDto indexDto = new LibraryTemplateIndexDto();
      indexDto.setBoxPosition("A01");
      indexDto.setIndex1Id(4L);

      pollingResponserHelper("put", Arrays.asList(indexDto), CONTROLLER_BASE + "/" + templateId + "/indices", CONTROLLER_BASE + "/bulk/", 202);

      LibraryTemplate template = currentSession().get(entityClass, templateId);
      assertNotNull(template.getIndexOnes().get("A01"));
      assertEquals(4L, template.getIndexOnes().get("A01").getId());
  }

  @Test
  public void testDeleteLibraryTemplate() throws Exception {
    testBulkDelete(entityClass, 3, CONTROLLER_BASE);
  }

}
