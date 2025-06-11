package uk.ac.bbsrc.tgac.miso.webapp.springtest;

import javax.sql.DataSource;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

@RunWith(SpringRunner.class)
@ContextConfiguration("/it-context.xml")
@PropertySource("/tomcat-config/miso.it.properties")
public abstract class AbstractST {
  private static final Logger log = LoggerFactory.getLogger(AbstractCT.class);

  private static final String SCRIPT_DIR = System.getProperty("basedir") + "/src/it/resources/db/migration/";


  private static final String CLEAR_DATA_SCRIPT = "clear_test_data.sql";
  private static final String PLAIN_SCRIPT = "plainSample_integration_test_data.sql";
  private static final String DETAILED_SCRIPT = "integration_test_data.sql";


  private static Boolean constantsComplete = false;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private DataSource dataSource;


  @Before
  public final void setupAbstractTest() {

    // reset test data for each test
    Resource clearData = new FileSystemResource(getScript(CLEAR_DATA_SCRIPT));
    Resource testData = new FileSystemResource(getScript(isDetailedSampleMode() ? DETAILED_SCRIPT : PLAIN_SCRIPT));
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(clearData, testData);
    populator.execute(dataSource);
  }


  protected boolean isDetailedSampleMode() {
    return true;
  }

  private File getScript(String filename) {
    File script = new File(SCRIPT_DIR + filename);
    if (!script.exists()) {
      throw new IllegalStateException("Script not found: " + filename);
    }
    return script;
  }


}
