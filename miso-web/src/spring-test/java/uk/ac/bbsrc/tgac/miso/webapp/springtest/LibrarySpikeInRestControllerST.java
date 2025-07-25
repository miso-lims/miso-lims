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
import uk.ac.bbsrc.tgac.miso.core.data.LibrarySpikeIn;
import uk.ac.bbsrc.tgac.miso.dto.LibrarySpikeInDto;

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


public class LibrarySpikeInRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/libraryspikeins";
  private static final Class<LibrarySpikeIn> entityClass = LibrarySpikeIn.class;

  private List<LibrarySpikeInDto> makeCreateDtos() {

    List<LibrarySpikeInDto> dtos = new ArrayList<LibrarySpikeInDto>();
    LibrarySpikeInDto one = new LibrarySpikeInDto();
    one.setAlias("one");

    LibrarySpikeInDto two = new LibrarySpikeInDto();
    two.setAlias("two");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibrarySpikeIn> librarySpikeIns =
        baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("one", librarySpikeIns.get(0).getAlias());
    assertEquals("two", librarySpikeIns.get(1).getAlias());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibrarySpikeIn creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update these
    LibrarySpikeInDto one = Dtos.asDto(currentSession().get(LibrarySpikeIn.class, 1));
    LibrarySpikeInDto three = Dtos.asDto(currentSession().get(LibrarySpikeIn.class, 3));
    one.setAlias("one");
    three.setAlias("three");

    List<LibrarySpikeInDto> dtos = new ArrayList<LibrarySpikeInDto>();
    dtos.add(one);
    dtos.add(three);


    List<LibrarySpikeIn> librarySpikeIns =
        (List<LibrarySpikeIn>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos,
            Arrays.asList(1, 3));

    assertEquals(1L, librarySpikeIns.get(0).getId());
    assertEquals(3L, librarySpikeIns.get(1).getId());
    assertEquals("one", librarySpikeIns.get(0).getAlias());
    assertEquals("three", librarySpikeIns.get(1).getAlias());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // only admin can update these
    LibrarySpikeInDto one = Dtos.asDto(currentSession().get(LibrarySpikeIn.class, 1));
    LibrarySpikeInDto three = Dtos.asDto(currentSession().get(LibrarySpikeIn.class, 3));
    one.setAlias("one");
    three.setAlias("three");

    List<LibrarySpikeInDto> dtos = new ArrayList<LibrarySpikeInDto>();
    dtos.add(one);
    dtos.add(three);

    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibrarySpikeIn() throws Exception {
    testBulkDelete(entityClass, 3, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 3, CONTROLLER_BASE);
  }
}
