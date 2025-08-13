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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.util.LinkedMultiValueMap;

import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;
import org.hibernate.Session;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import uk.ac.bbsrc.tgac.miso.core.data.Identifiable;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.core.service.UserService;

import static org.junit.Assert.*;
import java.util.List;
import java.util.Arrays;
import java.util.function.Function;

import java.util.ArrayList;
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

  private static final boolean DEBUG_MODE;

  static {
    DEBUG_MODE = Boolean.parseBoolean(System.getProperty("st.debug", "false"));
    // this allows debug mode to be turned on via command line args, i.e. -Dst.debug=true
  }

  private static Boolean constantsComplete = false;

  @Autowired
  protected WebApplicationContext wac;

  private MockMvc mockMvc;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private DataSource dataSource;


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
    String response = getMockMvc().perform(get(url)).andReturn().getResponse().getContentAsString();
    String status = JsonPath.read(response, "$.status");
    while (status.equals("running")) {
      response = getMockMvc().perform(get(url)).andReturn().getResponse().getContentAsString();
      status = JsonPath.read(response, "$.status");
      Thread.sleep(1000);
    }
    return response;
  }


  protected <T> List<T> baseTestBulkCreateAsync(String controllerBase, Class<T> createType, List<?> dtos)
      throws Exception {

    String response = pollingResponserHelper("post", dtos, controllerBase);
    List<T> objects = new ArrayList<T>();
    for (int i = 0; i < dtos.size(); i++) {
      Integer id = JsonPath.read(response, "$.data[" + i + "].id");
      T obj = currentSession().get(createType, id);
      assertNotNull(obj);
      objects.add(obj);
    }

    return objects;
  }


  protected <T> void testBulkCreateAsyncUnauthorized(String controllerBase, Class<T> createType, List<?> dtos)
      throws Exception {
    // tests failure for async create endpoints where admin permissions are required
    String response = pollingResponserHelper("post", dtos, controllerBase);
    if (DEBUG_MODE)
      System.out.println(response);
    assertEquals("An unexpected error has occurred", JsonPath.read(response, "$.detail"));
    // request should fail without admin permissions
  }

  /**
   * Sends a request to an async update endpoint and returns the resulting updated object. Note that
   * the DTOs must be provided in order of ID.
   */
  protected <T, D> List<T> baseTestBulkUpdateAsync(String controllerBase, Class<T> updateType, List<D> dtos,
      Function<D, Long> getId)
      throws Exception {
    String response = pollingResponserHelper("put", dtos, controllerBase);

    // check order of returned IDs
    List<Long> ids = dtos.stream().map(getId).toList();
    for (int i = 0; i < ids.size(); i++) {
      assertEquals(ids.get(i).longValue(), ((Integer) JsonPath.read(response, "$.data[" + i + "].id")).longValue());
      // the Integer cast is required, otherwise .longValue cannot be called
      // since the ids are always Integers as received from the JSON response, this is a safe cast
    }

    List<T> objects = new ArrayList<T>();
    for (Long id : ids) {
      T obj = currentSession().get(updateType, id);
      assertNotNull(obj);
      objects.add(obj);
    }
    return objects;
  }

  protected <T> void testBulkUpdateAsyncUnauthorized(String controllerBase, Class<T> createType, List<?> dtos)
      throws Exception {
    // tests failure for async update endpoints where admin permissions are required
    String response = pollingResponserHelper("put", dtos, controllerBase);
    if (DEBUG_MODE)
      System.out.println(response);
    assertEquals("An unexpected error has occurred", JsonPath.read(response, "$.detail"));
    // request should fail without admin permissions }
  }

  private String pollingResponserHelper(String requestType, List<?> dtos, String controllerBase) throws Exception {
    // helper method for async requests
    MvcResult mvcResult;
    ResultActions ac;
    switch (requestType) {
      case "post":
        ac = getMockMvc()
            .perform(post(controllerBase + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)));
        break;

      case "put":
        ac = getMockMvc()
            .perform(put(controllerBase + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)));
        break;

      default:
        throw new RuntimeException("invalid async method specified");
    }
    if (DEBUG_MODE)
      ac.andDo(print());

    mvcResult = ac.andExpect(status().isAccepted()).andReturn();

    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    String response = pollingResponse(controllerBase + "/bulk/" + id);
    return response;
  }


  protected <T> void testBulkDelete(Class<T> deleteType, int id, String controllerBase) throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(Long.valueOf(id)));

    assertNotNull(currentSession().get(deleteType, id)); // first check that it exists

    ResultActions ac = getMockMvc()
        .perform(post(controllerBase + "/bulk-delete").contentType(MediaType.APPLICATION_JSON).content(makeJson(ids)));

    if (DEBUG_MODE)
      ac.andDo(print());

    ac.andExpect(status().isNoContent());

    // now check that the lab was actually deleted
    assertNull(currentSession().get(deleteType, id));
  }

  protected <T> void testDeleteUnauthorized(Class<T> deleteType, int id, String controllerBase) throws Exception {
    List<Long> ids = new ArrayList<Long>(Arrays.asList(Long.valueOf(id)));

    assertNotNull(currentSession().get(deleteType, id)); // first check that it exists

    ResultActions ac = getMockMvc()
        .perform(post(controllerBase + "/bulk-delete").contentType(MediaType.APPLICATION_JSON).content(makeJson(ids)));

    if (DEBUG_MODE)
      ac.andDo(print());

    ac.andExpect(status().isUnauthorized());
    // this user doesn't have permissions to delete things
  }

  protected <T, D> T baseTestCreate(String controllerBase, D dto, Class<T> controllerClass, int status)
      throws Exception {

    ResultActions ac = getMockMvc()
        .perform(post(controllerBase).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)));

    if (DEBUG_MODE)
      ac.andDo(print());

    MvcResult result = ac.andExpect(status().is(status))
        .andExpect(jsonPath("$").exists())
        .andReturn();
    Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");

    T obj = currentSession().get(controllerClass, id);
    assertNotNull(obj);
    return obj;
  }

  protected <T, D> void testCreateUnauthorized(String controllerBase, D dto, Class<T> controllerClass)
      throws Exception {
    ResultActions ac = getMockMvc()
        .perform(post(controllerBase).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)));

    if (DEBUG_MODE)
      ac.andDo(print());
    ac.andExpect(status().isUnauthorized());
  }


  protected <T, D> T baseTestUpdate(String controllerBase, D dto, int id, Class<T> controllerClass)
      throws Exception {
    ResultActions ac = getMockMvc()
        .perform(put(controllerBase + "/" + id).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)));
    if (DEBUG_MODE)
      ac.andDo(print());

    ac.andExpect(status().isOk());

    T obj = currentSession().get(controllerClass, id);

    assertNotNull(obj);
    assertEquals(id, (int) ((Identifiable) obj).getId());

    return obj;
  }

  protected <T, D> void testUpdateUnauthorized(String controllerBase, D dto, int id, Class<T> updateType)
      throws Exception {
    ResultActions ac = getMockMvc()
        .perform(put(controllerBase + "/" + id).contentType(MediaType.APPLICATION_JSON).content(makeJson(dto)));
    if (DEBUG_MODE)
      ac.andDo(print());
    ac.andExpect(status().isUnauthorized());
  }

  private static MultiValueMap searchTerm(String term) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap();
    map.add("q", term);
    return map;
  }

  protected void baseSearchByTerm(String url, MultiValueMap params, List<Integer> ids)
      throws Exception {
    ResultActions ac = getMockMvc().perform(get(url).params(params).accept(MediaType.APPLICATION_JSON));
    if (DEBUG_MODE)
      ac.andDo(print());

    String response = ac.andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(ids.size())))
        .andReturn().getResponse().getContentAsString();

    checkIds(ids, false, response);
  }

  protected void baseSearchByTerm(String url, String searchTerm, List<Integer> ids) throws Exception {
    baseSearchByTerm(url, searchTerm(searchTerm), ids);
  }

  protected void testListAll(String url, List<Integer> ids) throws Exception {
    String response = getMockMvc().perform(get(url)).andReturn().getResponse().getContentAsString();
    checkIds(ids, false, response);
  }

  protected ResultActions testDtRequest(String url, int displayLength, String dataProp, int sortCol, List<Integer> ids)
      throws Exception {
    ResultActions ac = getMockMvc().perform(get(url).accept(MediaType.APPLICATION_JSON)
        .param("iDisplayStart", "0")
        .param("iDisplayLength", Integer.toString(displayLength))
        .param("mDataProp_0", dataProp)
        .param("sSortDir_0", "asc")
        .param("iSortCol_0", Integer.toString(sortCol))
        .param("sEcho", "1"));
    if (DEBUG_MODE)
      ac.andDo(print());

    ac.andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").exists())
        .andExpect(jsonPath("$.iTotalRecords").value(ids.size()));

    String response = ac.andReturn().getResponse().getContentAsString();
    checkIds(ids, true, response);

    return ac;
    // returns a result actions if more testing is desired
  }

  private void checkIds(List<Integer> expectedIds, boolean isDt, String response) throws Exception {
    List<Integer> returnedIds = new ArrayList<Integer>();

    String dtPath = "";
    if (isDt)
      dtPath = ".aaData";

    List<Integer> resultIds = JsonPath.read(response, "$" + dtPath + "[*].id");
    assertEquals(expectedIds.size(), resultIds.size());
    for (Integer expectedId : expectedIds) {
      assertTrue(resultIds.contains(expectedId));
    }

  }

  protected ResultActions testDtRequest(String url, List<Integer> ids)
      throws Exception {
    return testDtRequest(url, 25, "id", 3, ids);
  }

  protected ResultActions baseTestGetById(String controllerBase, int id) throws Exception {

    ResultActions result =
        getMockMvc().perform(get(controllerBase + "/" + Integer.toString(id)).accept(MediaType.APPLICATION_JSON));
    if (DEBUG_MODE)
      result.andDo(print());

    result.andExpect(status().isOk())
        .andExpect(jsonPath("$").exists())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(id));

    return result;
  }
}
