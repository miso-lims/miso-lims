package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.web.servlet.*;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import javax.ws.rs.core.MediaType;

import org.checkerframework.checker.units.qual.Temperature;
import org.junit.Before;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest;
import uk.ac.bbsrc.tgac.miso.dto.AssayTestDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import uk.ac.bbsrc.tgac.miso.core.data.impl.AssayTest.PermittedSamples;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import static org.junit.Assert.*;
import java.util.Collections;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class AssayTestRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/assaytests";
  private static final Class<AssayTest> controllerClass = AssayTest.class;

  private List<AssayTestDto> makeCreateDtos() {
    AssayTest atest1 = new AssayTest();
    atest1.setLibraryQualificationMethod(AssayTest.LibraryQualificationMethod.LOW_DEPTH_SEQUENCING);
    atest1.setPermittedSamples(AssayTest.PermittedSamples.ALL);

    AssayTestDto test1 = AssayTestDto.from(atest1);
    test1.setAlias("first");
    test1.setTissueTypeId(1L);
    test1.setExtractionClassId(11L);
    test1.setLibraryDesignCodeId(2L);


    AssayTest atest2 = new AssayTest();
    atest2.setLibraryQualificationMethod(AssayTest.LibraryQualificationMethod.NONE);
    atest2.setPermittedSamples(AssayTest.PermittedSamples.REQUISITIONED);

    AssayTestDto test2 = AssayTestDto.from(atest2);
    test2.setAlias("second");
    test2.setTissueTypeId(2L);
    test2.setExtractionClassId(12L);
    test2.setLibraryDesignCodeId(3L);


    List<AssayTestDto> dtos = new ArrayList<AssayTestDto>();
    dtos.add(test1);
    dtos.add(test2);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<AssayTest> tests = baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());

    assertEquals("first", tests.get(0).getAlias());
    assertEquals(1L, tests.get(0).getTissueType().getId());
    assertEquals(11L, tests.get(0).getExtractionClass().getId());
    assertEquals(2L, tests.get(0).getLibraryDesignCode().getId());
    assertEquals(AssayTest.LibraryQualificationMethod.LOW_DEPTH_SEQUENCING,
        tests.get(0).getLibraryQualificationMethod());
    assertEquals(AssayTest.PermittedSamples.ALL, tests.get(0).getPermittedSamples());

    assertEquals("second", tests.get(1).getAlias());
    assertEquals(2L, tests.get(1).getTissueType().getId());
    assertEquals(12L, tests.get(1).getExtractionClass().getId());
    assertEquals(3L, tests.get(1).getLibraryDesignCode().getId());
    assertEquals(AssayTest.LibraryQualificationMethod.NONE,
        tests.get(1).getLibraryQualificationMethod());
    assertEquals(AssayTest.PermittedSamples.REQUISITIONED, tests.get(1).getPermittedSamples());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // admin perms needed to update
    AssayTestDto one = AssayTestDto.from(currentSession().get(controllerClass, 1));
    AssayTestDto two = AssayTestDto.from(currentSession().get(controllerClass, 2));

    one.setAlias("one");
    two.setAlias("two");

    List<AssayTestDto> dtos = new ArrayList<AssayTestDto>();
    dtos.add(one);
    dtos.add(two);

    List<AssayTest> assayTests =
        (List<AssayTest>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, AssayTestDto::getId);

    assertEquals("one", assayTests.get(0).getAlias());
    assertEquals("two", assayTests.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    AssayTestDto one = AssayTestDto.from(currentSession().get(controllerClass, 1));
    AssayTestDto two = AssayTestDto.from(currentSession().get(controllerClass, 2));

    one.setAlias("one");
    two.setAlias("two");

    List<AssayTestDto> dtos = new ArrayList<AssayTestDto>();
    dtos.add(one);
    dtos.add(two);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteAssayTest() throws Exception {
    testBulkDelete(controllerClass, 4, CONTROLLER_BASE);
  }


  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 4, CONTROLLER_BASE);
  }
}
