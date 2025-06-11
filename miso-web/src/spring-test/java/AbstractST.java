package uk.ac.bbsrc.tgac.miso.webapp.springtest;

<<<<<<< HEAD
<<<<<<< HEAD
=======
>>>>>>> dd34ddc1f (added maven dependencies plugin to spring test profile)
import java.io.File;

import javax.sql.DataSource;

import org.junit.Before;
<<<<<<< HEAD
=======
import javax.sql.DataSource;

>>>>>>> e36728ea0 (tentative changes)
=======
>>>>>>> dd34ddc1f (added maven dependencies plugin to spring test profile)
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
<<<<<<< HEAD
<<<<<<< HEAD
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
=======
>>>>>>> e36728ea0 (tentative changes)
=======
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
>>>>>>> dd34ddc1f (added maven dependencies plugin to spring test profile)
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

<<<<<<< HEAD
<<<<<<< HEAD
=======
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
=======
>>>>>>> dd34ddc1f (added maven dependencies plugin to spring test profile)

>>>>>>> e36728ea0 (tentative changes)
@RunWith(SpringRunner.class)
@ContextConfiguration("/it-context.xml")
@PropertySource("/tomcat-config/miso.it.properties")
public abstract class AbstractST {
<<<<<<< HEAD
<<<<<<< HEAD
  private static final Logger log = LoggerFactory.getLogger(AbstractST.class);

  private static final String SCRIPT_DIR = System.getProperty("basedir") + "/src/it/resources/db/migration/";

  private static final String CLEAR_DATA_SCRIPT = "clear_test_data.sql";
  private static final String DETAILED_SCRIPT = "integration_test_data.sql";

=======
  private static final Logger log = LoggerFactory.getLogger(AbstractCT.class);
=======
  private static final Logger log = LoggerFactory.getLogger(AbstractST.class);
>>>>>>> dd34ddc1f (added maven dependencies plugin to spring test profile)

  private static final String SCRIPT_DIR = System.getProperty("basedir") + "/src/it/resources/db/migration/";


  private static final String CLEAR_DATA_SCRIPT = "clear_test_data.sql";
  private static final String PLAIN_SCRIPT = "plainSample_integration_test_data.sql";
  private static final String DETAILED_SCRIPT = "integration_test_data.sql";


>>>>>>> e36728ea0 (tentative changes)
  private static Boolean constantsComplete = false;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private DataSource dataSource;

<<<<<<< HEAD
=======

>>>>>>> e36728ea0 (tentative changes)
  @Before
  public final void setupAbstractTest() {

    // reset test data for each test
    Resource clearData = new FileSystemResource(getScript(CLEAR_DATA_SCRIPT));
<<<<<<< HEAD
    Resource testData = new FileSystemResource(getScript(DETAILED_SCRIPT));
=======
    Resource testData = new FileSystemResource(getScript(isDetailedSampleMode() ? DETAILED_SCRIPT : PLAIN_SCRIPT));
>>>>>>> e36728ea0 (tentative changes)
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(clearData, testData);
    populator.execute(dataSource);
  }

<<<<<<< HEAD
=======

  protected boolean isDetailedSampleMode() {
    return true;
  }

>>>>>>> e36728ea0 (tentative changes)
  private File getScript(String filename) {
    File script = new File(SCRIPT_DIR + filename);
    if (!script.exists()) {
      throw new IllegalStateException("Script not found: " + filename);
    }
    return script;
  }

<<<<<<< HEAD
=======

>>>>>>> e36728ea0 (tentative changes)
}
