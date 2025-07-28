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
import uk.ac.bbsrc.tgac.miso.core.data.impl.Assay;
import uk.ac.bbsrc.tgac.miso.dto.AssayDto;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

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


public class AssayRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/assays";
  private static final Class<Assay> controllerClass = Assay.class;

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    // must be admin to create an assay
    AssayDto assay = new AssayDto();
    assay.setAlias("tester");
    assay.setVersion("1.0");

    Assay newAssay = baseTestCreate(CONTROLLER_BASE, assay, controllerClass, 201);
    assertEquals("tester", newAssay.getAlias());
  }

  @Test
  public void testCreateFail() throws Exception {
    // must be admin to create an assay
    AssayDto assay = new AssayDto();
    assay.setAlias("tester");
    assay.setVersion("1.0");

    testCreateUnauthorized(CONTROLLER_BASE, assay, controllerClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testUpdate() throws Exception {
    // must be admin to change an assay
    AssayDto assay = AssayDto.from(currentSession().get(controllerClass, 1));

    assay.setAlias("modified");
    Assay updatedAssay = baseTestUpdate(CONTROLLER_BASE, assay, 1, controllerClass);
    assertEquals("modified", updatedAssay.getAlias());
  }

  @Test
  public void testUpdateFail() throws Exception {
    AssayDto assay = AssayDto.from(currentSession().get(controllerClass, 1));

    assay.setAlias("modified");
    testUpdateUnauthorized(CONTROLLER_BASE, assay, 1, controllerClass);
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDelete() throws Exception {
    testBulkDelete(controllerClass, 4, CONTROLLER_BASE);
  }

  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 4, CONTROLLER_BASE);
  }
}
