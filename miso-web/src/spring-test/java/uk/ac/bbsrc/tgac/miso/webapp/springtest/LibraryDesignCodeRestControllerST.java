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
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.dto.LibraryDesignCodeDto;

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


public class LibraryDesignCodeRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/librarydesigncodes";
  private static final Class<LibraryDesignCode> controllerClass = LibraryDesignCode.class;

  private List<LibraryDesignCodeDto> makeCreateDtos() {

    List<LibraryDesignCodeDto> dtos = new ArrayList<LibraryDesignCodeDto>();
    LibraryDesignCodeDto one = new LibraryDesignCodeDto();
    one.setCode("AB");
    one.setTargetedSequencingRequired(false);
    one.setDescription("one");

    LibraryDesignCodeDto two = new LibraryDesignCodeDto();
    two.setCode("CD");
    two.setTargetedSequencingRequired(false);
    two.setDescription("two");


    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibraryDesignCode> codes = baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
    assertEquals(codes.get(0).getCode(), "AB");
    assertEquals(codes.get(1).getCode(), "CD");
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibraryDesignCode creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // the admin user made these LibraryDesignCodes so only admin can update them
    LibraryDesignCodeDto bt = Dtos.asDto(currentSession().get(LibraryDesignCode.class, 1));
    LibraryDesignCodeDto d6 = Dtos.asDto(currentSession().get(LibraryDesignCode.class, 3));
    bt.setCode("BT");
    d6.setCode("D6");

    List<LibraryDesignCodeDto> dtos = new ArrayList<LibraryDesignCodeDto>();
    dtos.add(d6);
    dtos.add(bt);

    List<LibraryDesignCode> libraryDesignCodes =
        (List<LibraryDesignCode>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, Arrays.asList(1, 3));
    assertEquals("BT", libraryDesignCodes.get(0).getCode());
    assertEquals("D6", libraryDesignCodes.get(1).getCode());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // the admin user made these LibraryDesignCodes so only admin can update them
    LibraryDesignCodeDto bt = Dtos.asDto(currentSession().get(LibraryDesignCode.class, 1));
    LibraryDesignCodeDto d6 = Dtos.asDto(currentSession().get(LibraryDesignCode.class, 3));
    bt.setCode("BT");
    d6.setCode("D6");

    List<LibraryDesignCodeDto> dtos = new ArrayList<LibraryDesignCodeDto>();
    dtos.add(d6);
    dtos.add(bt);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibraryDesignCode() throws Exception {
    testBulkDelete(controllerClass, 18, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 18, CONTROLLER_BASE);
  }
}
