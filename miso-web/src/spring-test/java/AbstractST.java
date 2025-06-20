package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.File;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RunWith(SpringRunner.class)
@ContextConfiguration("/st-context.xml")
@WebAppConfiguration
@PropertySource("/tomcat-config/miso.it.properties")
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

  @Before
  public final void setupAbstractTest() {

    // reset test data for each test
    Resource clearData = new FileSystemResource(getScript(CLEAR_DATA_SCRIPT));
    Resource testData = new FileSystemResource(getScript(DETAILED_SCRIPT));
>>>>>>> 20b2e78ba (assume always detailed sample mode)
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(clearData, testData);
    populator.execute(dataSource);
  }

  <<<<<<<HEAD<<<<<<<HEAD=======

  protected boolean isDetailedSampleMode() {
    return true;
  }

  >>>>>>>

  e36728ea0 (tentative changes)
=======
>>>>>>> 20b2e78ba (assume always detailed sample mode)

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
