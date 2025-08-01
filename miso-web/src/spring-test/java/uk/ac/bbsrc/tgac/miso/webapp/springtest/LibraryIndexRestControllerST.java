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
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndex;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryIndexFamily;
import uk.ac.bbsrc.tgac.miso.dto.LibraryIndexDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import java.util.Collections;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.LibraryIndexRestController.IndexSearchRequest;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.LibraryIndexRestController.IndexSearchResult;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class LibraryIndexRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/libraryindices";
  private static final Class<LibraryIndex> controllerClass = LibraryIndex.class;

  private List<LibraryIndexDto> makeCreateDtos() {

    List<LibraryIndexDto> dtos = new ArrayList<LibraryIndexDto>();
    LibraryIndexDto one = new LibraryIndexDto();
    one.setFamily(Dtos.asDto(currentSession().get(LibraryIndexFamily.class, 1)));
    one.setName("one");
    one.setPosition(1);
    one.setSequence("TTTTTT");

    LibraryIndexDto two = new LibraryIndexDto();
    two.setFamily(Dtos.asDto(currentSession().get(LibraryIndexFamily.class, 1)));
    two.setName("two");
    two.setPosition(2);
    two.setSequence("GGGGGG");


    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibraryIndex> libins = baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
    assertEquals(libins.get(0).getName(), "one");
    assertEquals(libins.get(1).getName(), "two");
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibraryIndex creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update these
    LibraryIndexDto one = Dtos.asDto(currentSession().get(LibraryIndex.class, 1));
    LibraryIndexDto three = Dtos.asDto(currentSession().get(LibraryIndex.class, 3));
    one.setName("one");
    three.setName("three");

    List<LibraryIndexDto> dtos = new ArrayList<LibraryIndexDto>();
    dtos.add(one);
    dtos.add(three);


    List<LibraryIndex> libraryIndexes =
        (List<LibraryIndex>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, LibraryIndexDto::getId);
    assertEquals("one", libraryIndexes.get(0).getName());
    assertEquals("three", libraryIndexes.get(1).getName());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // only admin can update these
    LibraryIndexDto one = Dtos.asDto(currentSession().get(LibraryIndex.class, 1));
    LibraryIndexDto three = Dtos.asDto(currentSession().get(LibraryIndex.class, 3));
    one.setName("one");
    three.setName("three");

    List<LibraryIndexDto> dtos = new ArrayList<LibraryIndexDto>();
    dtos.add(one);
    dtos.add(three);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibraryIndex() throws Exception {
    testBulkDelete(controllerClass, 18, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 18, CONTROLLER_BASE);
  }


  @Test
  public void testDatatable() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt",
        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18));
  }

  @Test
  public void testDatatableByPlatform() throws Exception {
    testDtRequest(CONTROLLER_BASE + "/dt/platform/PACBIO", Arrays.asList(13, 14));
  }

  @Test
  public void testSearchIndexFamilies() throws Exception {
    IndexSearchRequest req = new IndexSearchRequest();
    req.setPosition1Indices(Arrays.asList("AAAAAA"));
    req.setPosition2Indices(Arrays.asList("AAATTT"));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/search").content(makeJson(req)).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.*", hasSize(3)))
        .andExpect(jsonPath("$[0].indexFamily").value("Single Index 6bp"))
        .andExpect(jsonPath("$[0].position1Matches").value(1))
        .andExpect(jsonPath("$[1].indexFamily").value("Dual Index 6bp"))
        .andExpect(jsonPath("$[1].position2Matches").value(1))
        .andExpect(jsonPath("$[2].indexFamily").value("Unused Family"))
        .andExpect(jsonPath("$[2].position1Matches").value(1))

    ;
  }
}
