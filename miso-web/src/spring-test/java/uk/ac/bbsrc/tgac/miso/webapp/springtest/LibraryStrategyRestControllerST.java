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
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.dto.LibraryStrategyTypeDto;

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


public class LibraryStrategyRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/librarystrategies";
  private static final Class<LibraryStrategyType> controllerClass = LibraryStrategyType.class;

  private List<LibraryStrategyTypeDto> makeCreateDtos() {

    List<LibraryStrategyTypeDto> dtos = new ArrayList<LibraryStrategyTypeDto>();
    LibraryStrategyTypeDto one = new LibraryStrategyTypeDto();
    one.setDescription("first");
    one.setName("one");

    LibraryStrategyTypeDto two = new LibraryStrategyTypeDto();
    two.setName("two");
    two.setDescription("second");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibraryStrategyType> libraryStrategyTypes =
        baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
    assertEquals(libraryStrategyTypes.get(0).getName(), "one");
    assertEquals(libraryStrategyTypes.get(1).getName(), "two");
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibraryStrategyType creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update these
    LibraryStrategyTypeDto one = Dtos.asDto(currentSession().get(LibraryStrategyType.class, 1));
    LibraryStrategyTypeDto three = Dtos.asDto(currentSession().get(LibraryStrategyType.class, 3));
    one.setName("one");
    three.setName("three");

    List<LibraryStrategyTypeDto> dtos = new ArrayList<LibraryStrategyTypeDto>();
    dtos.add(one);
    dtos.add(three);


    List<LibraryStrategyType> libraryStrategyTypes =
        (List<LibraryStrategyType>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos,
            Arrays.asList(1, 3));
    assertEquals("one", libraryStrategyTypes.get(0).getName());
    assertEquals("three", libraryStrategyTypes.get(1).getName());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // only admin can update these
    LibraryStrategyTypeDto one = Dtos.asDto(currentSession().get(LibraryStrategyType.class, 1));
    LibraryStrategyTypeDto three = Dtos.asDto(currentSession().get(LibraryStrategyType.class, 3));
    one.setName("one");
    three.setName("three");

    List<LibraryStrategyTypeDto> dtos = new ArrayList<LibraryStrategyTypeDto>();
    dtos.add(one);
    dtos.add(three);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibraryStrategyType() throws Exception {
    testBulkDelete(controllerClass, 11, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 11, CONTROLLER_BASE);
  }
}
