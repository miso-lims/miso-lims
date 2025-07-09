package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.File;
import java.io.IOException;

import javax.sql.DataSource;

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

  @Before
  public final void setupAbstractTest() throws IOException {

    // reset test data for each test
    Resource clearData = new FileSystemResource(getScript(CLEAR_DATA_SCRIPT));
    Resource testData = new FileSystemResource(getScript(DETAILED_SCRIPT));

    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(clearData, testData);
    populator.execute(dataSource);

    this.mockMvc = webAppContextSetup(this.wac).build();
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
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
    String requestJson = ow.writeValueAsString(obj);
    return requestJson;
  }

  public String makeJson(List<?> obj) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
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


    MvcResult mvcResult = getMockMvc()
        .perform(post(controllerBase + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
        .andExpect(status().isAccepted())
        .andReturn();

    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    String response = pollingResponse(controllerBase + "/bulk/" + id);

    Integer id1 = JsonPath.read(response, "$.data[0].id");
    Integer id2 = JsonPath.read(response, "$.data[1].id");

    assertNotNull(currentSession().get(createType, id1));
    assertNotNull(currentSession().get(createType, id2));

  }

  protected <T> void abstractBulkCreateAsyncFail(String controllerBase, Class<T> createType, List<?> dtos)
      throws Exception {
    // tests failure for async create endpoints where admin permissions are required
    
    MvcResult mvcResult = getMockMvc()
          .perform(post(controllerBase + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
          .andExpect(status().isAccepted())
          .andDo(print())
          .andReturn();
    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    String response = pollingResponse(controllerBase + "/bulk/" + id);
    String status = JsonPath.read(response, "$.status");
    assertEquals("failed", status); // request should fail without admin permissions
}

  protected <T> List<T> abstractTestBulkUpdateAsync(String controllerBase, Class<T> updateType, List<?> dtos,
      int[] ids)
      throws Exception {

    MvcResult mvcResult = getMockMvc()
        .perform(put(controllerBase + "/bulk").contentType(MediaType.APPLICATION_JSON).content(makeJson(dtos)))
        .andExpect(status().isAccepted())
        .andReturn();

    String status = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.status");

    String id = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$.operationId");
    pollingResponse(controllerBase + "/bulk/" + id);


    // now check if the updates went through
    T obj1 = currentSession().get(updateType, ids[0]);
    T obj2 = currentSession().get(updateType, ids[1]);

    assertNotNull(obj1);
    assertNotNull(obj2);

    return Arrays.asList(obj1, obj2);

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



}
