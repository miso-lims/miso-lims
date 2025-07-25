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
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryType;
import uk.ac.bbsrc.tgac.miso.dto.LibraryTypeDto;

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


public class LibraryTypeRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/librarytypes";
  private static final Class<LibraryType> entityClass = LibraryType.class;

  private List<LibraryTypeDto> makeCreateDtos() {

    List<LibraryTypeDto> dtos = new ArrayList<LibraryTypeDto>();
    LibraryTypeDto one = new LibraryTypeDto();
    one.setPlatform("ILLUMINA");
    one.setDescription("one");
    one.setArchived(false);
    one.setAbbreviation("ON");

    LibraryTypeDto two = new LibraryTypeDto();
    two.setPlatform("ILLUMINA");
    two.setDescription("two");
    two.setArchived(false);
    two.setAbbreviation("TW");

    dtos.add(one);
    dtos.add(two);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    List<LibraryType> libins = baseTestBulkCreateAsync(CONTROLLER_BASE, entityClass, makeCreateDtos());
    assertEquals("ON", libins.get(0).getAbbreviation());
    assertEquals("TW", libins.get(1).getAbbreviation());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // LibraryType creation is for admin only, so this test is expecting failure due to
    // insufficent permission
    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, entityClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // only admin can update these
    LibraryTypeDto one = Dtos.asDto(currentSession().get(entityType, 1));
    LibraryTypeDto two = Dtos.asDto(currentSession().get(entityType, 2));
    one.setAbbreviation("ON");
    two.setAbbreviation("TW");

    List<LibraryTypeDto> dtos = new ArrayList<LibraryTypeDto>();
    dtos.add(one);
    dtos.add(two);


    List<LibraryType> libraryTypes =
        (List<LibraryType>) baseTestBulkUpdateAsync(CONTROLLER_BASE, entityClass, dtos, Arrays.asList(1, 2));
    
    assertEquals(1L, libraryTypes.get(0).getId());
    assertEquals(2L, libraryTypes.get(1).getId());
    assertEquals("ON", libraryTypes.get(0).getAbbreviation());
    assertEquals("TW", libraryTypes.get(1).getAbbreviation());
  }

  @Test
  public void testBulkUpdateAsyncFail() throws Exception {
    // only admin can update these
    LibraryTypeDto one = Dtos.asDto(currentSession().get(entityType, 1));
    LibraryTypeDto two = Dtos.asDto(currentSession().get(entityType, 2));
    one.setAbbreviation("ON");
    two.setAbbreviation("TW");

    List<LibraryTypeDto> dtos = new ArrayList<LibraryTypeDto>();
    dtos.add(one);
    dtos.add(two);
    testBulkUpdateAsyncUnauthorized(CONTROLLER_BASE, entityClass, dtos);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteLibraryType() throws Exception {
    testBulkDelete(entityClass, 28, CONTROLLER_BASE);
  }

  @Test
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(entityClass, 28, CONTROLLER_BASE);
  }
}
