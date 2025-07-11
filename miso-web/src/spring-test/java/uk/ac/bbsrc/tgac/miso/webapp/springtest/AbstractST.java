package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import org.hibernate.Session;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.transaction.Transactional;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.webapp.context.MisoAppListener;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;


import java.util.ArrayList;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import org.springframework.test.web.servlet.ResultActions;
import javax.ws.rs.core.MediaType;
import com.jayway.jsonpath.JsonPath;


@RunWith(SpringRunner.class)
@ContextConfiguration("/st-context.xml")
@WebAppConfiguration
@PropertySource("/tomcat-config/miso.it.properties")
@TestExecutionListeners(value = SpringTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@WithMockUser(username = "user", password = "user", roles = {"INTERNAL"})
public abstract class AbstractST {
  private static final Logger log = LoggerFactory.getLogger(AbstractST.class);

  private static final String SCRIPT_DIR = System.getProperty("basedir") + "/src/it/resources/db/migration/";

  private static final String CLEAR_DATA_SCRIPT = "clear_test_data.sql";
  private static final String DETAILED_SCRIPT = "integration_test_data.sql";

  private static Boolean constantsComplete = false;

  @Autowired
  protected WebApplicationContext wac;

  private MockMvc mockMvc;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private DataSource dataSource;

  @Mock
  @Autowired
  private AuthorizationManager authorizationManager;


  private ObjectMapper mapper;
  private ObjectWriter ow;

  @Before
  public final void setupAbstractTest() throws IOException {

    // reset test data for each test
    Resource clearData = new FileSystemResource(getScript(CLEAR_DATA_SCRIPT));
    Resource testData = new FileSystemResource(getScript(DETAILED_SCRIPT));

    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(clearData, testData);
    populator.execute(dataSource);

    this.mockMvc = webAppContextSetup(this.wac).build();

    mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ow = mapper.writer().withDefaultPrettyPrinter();
  }

  public Session currentSession() {
    return entityManager.unwrap(Session.class);
  }

  private File getScript(String filename) {
    File script = new File(SCRIPT_DIR + filename);
    if (!script.exists()) {
      throw new IllegalStateException("Script not found: " + filename);
    }
    return script;
  }

  public String makeJson(Object obj) throws Exception {
    String requestJson = ow.writeValueAsString(obj);
    return requestJson;
  }

  @Test
  public void initialization() {
    assertNotNull(wac);
  }

  protected MockMvc getMockMvc() {
    return mockMvc;
  }

  protected String pollingResponse(String url) throws Exception {
    String response =
        getMockMvc().perform(get(url)).andReturn().getResponse().getContentAsString();
    String status = JsonPath.read(response, "$.status");
    while (status.equals("running")) {
      response =
          getMockMvc().perform(get(url)).andReturn().getResponse().getContentAsString();
      status = JsonPath.read(response, "$.status");
      Thread.sleep(1000);
    }
    return response;
  }

  protected <T> void abstractTestBulkCreateAsync(String controllerBase, Class<T> createType, List<?> dtos)
      throws Exception {

    String response = pollingResponserHelper("post", dtos, controllerBase)[0];

    for (int i = 0; i < dtos.size(); i++) {
      Integer id = JsonPath.read(response, "$.data[" + i + "].id");
      assertNotNull(currentSession().get(createType, id));
    }
  }

  protected <T> void abstractBulkCreateAsyncFail(String controllerBase, Class<T> createType, List<?> dtos)
      throws Exception {
    // tests failure for async create endpoints where admin permissions are required
    String status = pollingResponserHelper("post", dtos, controllerBase)[1];
    assertEquals("failed", status); // request should fail without admin permissions
  }

  protected <T> List<T> abstractTestBulkUpdateAsync(String controllerBase, Class<T> updateType, List<?> dtos,
      List<Integer> ids)
      throws Exception {
    pollingResponserHelper("put", dtos, controllerBase);

    // now check if the updates went through
    List<T> objects = new ArrayList<T>();
    for (int id : ids) {
      T obj = currentSession().get(updateType, id);
      assertNotNull(obj);
      objects.add(obj);
    }
    return objects;
  }

  private String[] pollingResponserHelper(String requestType, List<?> dtos, String controllerBase) throws Exception {
    // helper method for async requests
    MvcResult mvcResult;
    switch (requestType) {
      case "post":
        mvcResult = getMockMvc()
            .perform(post(controllerBase + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
            .andExpect(status().isAccepted())
            .andReturn();
        break;

      case "put":
        mvcResult = getMockMvc()
            .perform(put(controllerBase + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
            .andExpect(status().isAccepted())
            .andReturn();

        break;
      default:
        mvcResult = getMockMvc()
            .perform(post(controllerBase + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
            .andExpect(status().isAccepted())
            .andReturn();
        break;
    }

    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    String response = pollingResponse(controllerBase + "/bulk/" + id);
    String status = JsonPath.read(response, "$.status");
    return new String[] {response, status}; // lets the caller choose what they want to use
  }


  protected <T> void abstractTestDelete(Class<T> deleteType, int id, String controllerBase) throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(Long.valueOf(id)));

    assertNotNull(currentSession().get(deleteType, id)); // first check that it exists

    getMockMvc()
        .perform(post(controllerBase + "/bulk-delete").contentType(MediaType.APPLICATION_JSON).content(makeJson(ids)))
        .andExpect(status().isNoContent());

    // now check that the lab was actually deleted
    assertNull(currentSession().get(deleteType, id));
  }

  protected <T> void abstractTestDeleteFail(Class<T> deleteType, int id, String controllerBase) throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(Long.valueOf(id)));

    assertNotNull(currentSession().get(deleteType, id)); // first check that it exists

    getMockMvc()
        .perform(post(controllerBase + "/bulk-delete").contentType(MediaType.APPLICATION_JSON).content(makeJson(ids)))
        .andExpect(status().isUnauthorized());
    // this user doesn't have permissions to delete things
  }

  protected <T, D> T abstractCreateAndReturnEntity(String controllerBase, D dto, Class<T> controllerClass, int status)
      throws Exception {

    MvcResult result = getMockMvc()
        .perform(post(controllerBase).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andExpect(status().is(status)) // either created (201) or OK (200) dependening on the controller
        .andExpect(jsonPath("$").exists())
        .andReturn();

    Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    assertNotNull(currentSession().get(controllerClass, id));
    return currentSession().get(controllerClass, id);
  }

  protected <T, D> T abstractUpdateAndReturnEntity(String controllerBase, D dto, int id, Class<T> controllerClass)
      throws Exception {
    getMockMvc()
        .perform(put(controllerBase + "/" + id).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)))
        .andExpect(status().isOk());

    assertNotNull(currentSession().get(controllerClass, id));
    return currentSession().get(controllerClass, id);
  }

  protected void abstractSearchByTermWithExpectedNumResults(String url, String searchTerm, int expectedSize)
      throws Exception {
    getMockMvc().perform(get(url).param("q", searchTerm).accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(expectedSize)));
  }

  protected ResultActions performDtRequest(String url, int displayLength, String dataProp, int sortCol)
      throws Exception {
    return getMockMvc().perform(get(url).accept(MediaType.APPLICATION_JSON)
        .param("iDisplayStart", "0")
        .param("iDisplayLength", Integer.toString(displayLength))
        .param("mDataProp_0", dataProp)
        .param("sSortDir_0", "asc")
        .param("iSortCol_0", Integer.toString(sortCol))
        .param("sEcho", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists());
  }

  // version of the dt response method with default values, because java doesn't support default
  // method param values
  protected ResultActions performDtRequest(String url)
      throws Exception {

    int displayLength = 25;
    String dataProp = "id";
    int sortCol = 3;
    return getMockMvc().perform(get(url).accept(MediaType.APPLICATION_JSON)
        .param("iDisplayStart", "0")
        .param("iDisplayLength", Integer.toString(displayLength))
        .param("mDataProp_0", dataProp)
        .param("sSortDir_0", "asc")
        .param("iSortCol_0", Integer.toString(sortCol))
        .param("sEcho", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists());
  }



}
