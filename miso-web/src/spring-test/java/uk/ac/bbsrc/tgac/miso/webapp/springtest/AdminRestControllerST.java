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
import org.springframework.test.context.TestPropertySource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;

import org.springframework.test.web.servlet.MvcResult;
import uk.ac.bbsrc.tgac.miso.dto.Dtos;

import org.springframework.web.servlet.View;

import org.springframework.security.test.context.support.WithMockUser;
import com.jayway.jsonpath.JsonPath;
import com.eaglegenomics.simlims.core.User;
import jakarta.transaction.Transactional;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.springframework.test.web.servlet.MockMvc;

public class AdminRestControllerST extends AbstractST {

  private static final String CONTROLLER_BASE = "/rest/admin";


  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testClearCache() throws Exception {


    MvcResult mvcResult =
        getMockMvc().perform(post(CONTROLLER_BASE + "/cache/clear")).andExpect(status().isOk()).andReturn();
    // check if cache is cleared somehow
    // I guess response body

    assertEquals("true", mvcResult.getResponse().getContentAsString());
  }

  @Test
  public void failClearCache() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/cache/clear"))
        .andDo(print())
        .andExpect(status().isInternalServerError());
  }

  @Test
  @WithMockUser(username = "admin", password = "admin", roles = {"INTERNAL", "ADMIN"})
  public void testRegenBarcodes() throws Exception {

    MvcResult mvcResult = getMockMvc().perform(post(CONTROLLER_BASE + "/barcode/regen"))
        .andDo(print())
        .andExpect(status().isOk()).andReturn();

    assertNotNull(mvcResult.getResponse());

  }

  @Test
  public void failRegenBarcodes() throws Exception {
    getMockMvc().perform(post(CONTROLLER_BASE + "/barcode/regen"))
        .andDo(print())
        .andExpect(status().isInternalServerError());
  }


  @Test
  public void testRefreshConstants() throws Exception {
    MvcResult mvcResult = getMockMvc().perform(post(CONTROLLER_BASE + "/constants/refresh"))
        .andExpect(status().isOk()).andReturn();

    assertEquals("0", mvcResult.getResponse().getContentAsString());


  }



}
