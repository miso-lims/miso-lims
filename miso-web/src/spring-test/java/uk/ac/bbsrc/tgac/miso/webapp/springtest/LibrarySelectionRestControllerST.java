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
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.dto.LibrarySelectionTypeDto;

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


public class LibrarySelectionRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/libraryselections";
  private static final Class<LibrarySelectionType> controllerClass = LibrarySelectionType.class;

  private List<LibrarySelectionTypeDto> makeCreateDtos() {

    List<LibrarySelectionTypeDto> dtos = new ArrayList<LibrarySelectionTypeDto>();
    LibrarySelectionTypeDto one = new LibrarySelectionTypeDto();
    one.setName("one");
    one.setDescription("first");

    LibrarySelectionTypeDto two = new LibrarySelectionTypeDto();
    two.setName("two");
    two.setDescription("second");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibrarySelectionType> librarySelectionTypes =
        baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
    assertEquals(librarySelectionTypes.get(0).getName(), "one");
    assertEquals(librarySelectionTypes.get(1).getName(), "two");
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibrarySelectionType creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update these
    LibrarySelectionTypeDto one = Dtos.asDto(currentSession().get(LibrarySelectionType.class, 1));
    LibrarySelectionTypeDto three = Dtos.asDto(currentSession().get(LibrarySelectionType.class, 3));
    one.setName("one");
    three.setName("three");

    List<LibrarySelectionTypeDto> dtos = new ArrayList<LibrarySelectionTypeDto>();
    dtos.add(one);
    dtos.add(three);


    List<LibrarySelectionType> librarySelectionTypes =
        (List<LibrarySelectionType>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos,
            LibrarySelectionTypeDto::getId);
    assertEquals("one", librarySelectionTypes.get(0).getName());
    assertEquals("three", librarySelectionTypes.get(1).getName());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // only admin can update these
    LibrarySelectionTypeDto one = Dtos.asDto(currentSession().get(LibrarySelectionType.class, 1));
    LibrarySelectionTypeDto three = Dtos.asDto(currentSession().get(LibrarySelectionType.class, 3));
    one.setName("one");
    three.setName("three");

    List<LibrarySelectionTypeDto> dtos = new ArrayList<LibrarySelectionTypeDto>();
    dtos.add(one);
    dtos.add(three);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibrarySelectionType() throws Exception {
    testBulkDelete(controllerClass, 26, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 26, CONTROLLER_BASE);
  }
}
