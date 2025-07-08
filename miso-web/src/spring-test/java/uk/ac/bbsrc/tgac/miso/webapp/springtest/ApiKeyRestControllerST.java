package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.*;
import org.springframework.test.context.web.WebAppConfiguration;
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

import static org.hamcrest.Matchers.*;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;
import uk.ac.bbsrc.tgac.miso.webapp.controller.rest.ApiKeyRestController.CreateApiKeyRequest;

import org.springframework.web.servlet.View;

import org.springframework.security.test.context.support.WithMockUser;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import uk.ac.bbsrc.tgac.miso.core.data.impl.ApiKey;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;
import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;
import com.eaglegenomics.simlims.core.User;
import java.util.Date;


public class ApiKeyRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/apikeys";

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testCreate() throws Exception {
    // must be admin to create an api key
    CreateApiKeyRequest request = new CreateApiKeyRequest("testname");
    MvcResult mvcResult =
        getMockMvc().perform(post(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

    assertNotNull(mvcResult.getResponse());
  }

  @Test
  public void testCreateFail() throws Exception {
    CreateApiKeyRequest request = new CreateApiKeyRequest("testname");
    getMockMvc().perform(post(CONTROLLER_BASE).contentType(MediaType.APPLICATION_JSON).content(makeJson(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testBulkDelete() throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(2L));


    // check that the api key we want to delete exists
    assertNotNull(currentSession().get(ApiKey.class, 2));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-delete").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(ids)))
        .andExpect(status().isNoContent());

    // now check that the api key was actually deleted
    assertNull(currentSession().get(ApiKey.class, 2));
  }

  @Test
  public void testDeleteFail() throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(2L));


    // check that the api key we want to delete exists
    assertNotNull(currentSession().get(ApiKey.class, 2));

    getMockMvc()
        .perform(post(CONTROLLER_BASE + "/bulk-delete").contentType(MediaType.APPLICATION_JSON)
            .content(makeJson(ids)))
        .andExpect(status().isUnauthorized());
  }
}
