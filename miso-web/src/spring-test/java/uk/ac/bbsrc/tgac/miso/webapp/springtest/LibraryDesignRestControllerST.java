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
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDesignDto;

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


public class LibraryDesignRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/librarydesigns";
  private static final Class<LibraryDesign> controllerClass = LibraryDesign.class;

  private List<LibraryDesignDto> makeCreateDtos() {

    List<LibraryDesignDto> dtos = new ArrayList<LibraryDesignDto>();
    LibraryDesignDto one = new LibraryDesignDto();
    one.setName("1");
    one.setSampleClassId(1L);
    one.setStrategyId(1L);
    one.setSelectionId(1L);
    one.setDesignCodeId(1L);

    LibraryDesignDto two = new LibraryDesignDto();
    two.setName("2");
    two.setSampleClassId(1L);
    two.setStrategyId(1L);
    two.setSelectionId(1L);
    two.setDesignCodeId(1L);

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibraryDesign> codes = baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
    assertEquals(codes.get(0).getName(), "1");
    assertEquals(codes.get(1).getName(), "2");
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibraryDesign creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // the admin user made these LibraryDesigns so only admin can update them
    LibraryDesignDto t = Dtos.asDto(currentSession().get(LibraryDesign.class, 2));
    LibraryDesignDto t2 = Dtos.asDto(currentSession().get(LibraryDesign.class, 3));
    t.setName("this");
    t2.setName("this2");

    List<LibraryDesignDto> dtos = new ArrayList<LibraryDesignDto>();
    dtos.add(t);
    dtos.add(t2);

    List<LibraryDesign> libraryDesigns =
        (List<LibraryDesign>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, Arrays.asList(2, 3));
    assertEquals("this", libraryDesigns.get(0).getName());
    assertEquals("this2", libraryDesigns.get(1).getName());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // the admin user made these LibraryDesigns so only admin can update them
    LibraryDesignDto t = Dtos.asDto(currentSession().get(LibraryDesign.class, 2));
    LibraryDesignDto t2 = Dtos.asDto(currentSession().get(LibraryDesign.class, 3));
    t.setName("this");
    t2.setName("this2");

    List<LibraryDesignDto> dtos = new ArrayList<LibraryDesignDto>();
    dtos.add(t);
    dtos.add(t2);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibraryDesign() throws Exception {
    testBulkDelete(controllerClass, 20, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 20, CONTROLLER_BASE);
  }
}
