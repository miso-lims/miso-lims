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

import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.core.data.ArrayModel;
import uk.ac.bbsrc.tgac.miso.dto.ArrayModelDto;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.View;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.security.test.context.support.WithMockUser;
import uk.ac.bbsrc.tgac.miso.core.data.type.StatusType;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationException;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.test.web.servlet.MockMvc;
import java.util.Date;


public class ArrayModelRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/arraymodels";
  private static final Class<ArrayModel> controllerClass = ArrayModel.class;


  private List<ArrayModelDto> makeCreateDtos() {
    ArrayModelDto arrone = new ArrayModelDto();
    arrone.setAlias("array model one");

    ArrayModelDto arrtwo = new ArrayModelDto();
    arrtwo.setAlias("array model two");

    List<ArrayModelDto> dtos = new ArrayList<ArrayModelDto>();
    dtos.add(arrone);
    dtos.add(arrtwo);

    return dtos;
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkCreateAsync() throws Exception {
    baseTestBulkCreateAsync(CONTROLLER_BASE, controllerClass, makeCreateDtos());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkUpdateAsync() throws Exception {
    // admin perms not needed, admin is just the one that made these objects

    ArrayModelDto beadchip = Dtos.asDto(currentSession().get(controllerClass, 1));
    ArrayModelDto unused = Dtos.asDto(currentSession().get(controllerClass, 2));

    beadchip.setAlias("beady");
    unused.setAlias("not used");

    List<ArrayModelDto> dtos = new ArrayList<ArrayModelDto>();
    dtos.add(beadchip);
    dtos.add(unused);

    List<ArrayModel> arrayModels =
        (List<ArrayModel>) baseTestBulkUpdateAsync(CONTROLLER_BASE, controllerClass, dtos, Arrays.asList(1, 2));

    assertEquals("beady", arrayModels.get(0).getAlias());
    assertEquals("not used", arrayModels.get(1).getAlias());
  }

  @Test
  public void testBulkCreateFail() throws Exception {
    // array models can only be created by an admin user, so this test expected failure due to
    // insufficient permissions

    testBulkCreateAsyncUnauthorized(CONTROLLER_BASE, controllerClass, makeCreateDtos());

  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testDeleteArrayModel() throws Exception {
    testBulkDelete(controllerClass, 2, CONTROLLER_BASE);
  }


  @Test
  @WithMockUser(username = "hhenderson", roles = {"INTERNAL"})
  public void testDeleteFail() throws Exception {
    testDeleteUnauthorized(controllerClass, 2, CONTROLLER_BASE);
  }



}
