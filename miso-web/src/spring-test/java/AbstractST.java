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
import org.springframework.web.context.support.XmlWebApplicationContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.ServletContextEvent;
import uk.ac.bbsrc.tgac.miso.core.security.AuthorizationManager;
import uk.ac.bbsrc.tgac.miso.webapp.context.MisoAppListener;


// loader = org.springframework.test.context.support.GenericXmlContextLoader.class)
// @WebAppConfiguration
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {STConfig.class})
public abstract class AbstractST {
  private static final Logger log = LoggerFactory.getLogger(AbstractST.class);

  private static final String SCRIPT_DIR = System.getProperty("basedir") + "/src/it/resources/db/migration/";

  private static final String CLEAR_DATA_SCRIPT = "clear_test_data.sql";
  private static final String DETAILED_SCRIPT = "integration_test_data.sql";

  private static Boolean constantsComplete = false;

  @Autowired
  private MockServletContext servletContext;

  @Autowired
  protected XmlWebApplicationContext wac;

  private MockMvc mockMvc;


  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private DataSource dataSource;

  @Mock
  @Autowired
  private AuthorizationManager authorizationManager;


  // @Autowired
  // private UserService userService;

  @Before
  public final void setupAbstractTest() throws IOException {
    // MockitoAnnotations.initMocks(this);

    // MockServletContext mockServletContext = new MockServletContext();
    // wac.setServletContext(mockServletContext);


    servletContext.setAttribute(
        WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);


    new MisoAppListener().contextInitialized(
        new ServletContextEvent(servletContext));


    // reset test data for each test
    Resource clearData = new FileSystemResource(getScript(CLEAR_DATA_SCRIPT));
    Resource testData = new FileSystemResource(getScript(DETAILED_SCRIPT));
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(clearData, testData);
    populator.execute(dataSource);
  }

  // private void setUser(boolean isAdmin, Group... groups) throws IOException {
  // User user = userService.get(1L); // getting a real user may fix the issue

  // Mockito.when(authorizationManager.getCurrentUser()).thenReturn(user);
  // Mockito.when(authorizationManager.isAdminUser()).thenReturn(isAdmin);
  // // Mockito.when(user.isAdmin()).thenReturn(isAdmin); // this might fix the issue
  // Mockito.when(authorizationManager.isGroupMember(Mockito.any())).thenReturn(false);
  // if (groups.length > 0) {
  // for (Group group : groups) {
  // Mockito.when(authorizationManager.isGroupMember(group)).thenReturn(true);
  // }
  // }
  // }

  private File getScript(String filename) {
    File script = new File(SCRIPT_DIR + filename);
    if (!script.exists()) {
      throw new IllegalStateException("Script not found: " + filename);
    }
    return script;
  }

  @Before
  public void setup() {
    this.mockMvc = webAppContextSetup(this.wac).build();
  }

  @Test
  public void initialization() {
    assertNotNull(wac);
  }

  protected MockMvc getMockMvc() {
    return mockMvc;
  }

}
