package uk.ac.bbsrc.tgac.miso.webapp.controllertest;

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


@RunWith(SpringRunner.class)
@ContextConfiguration("/it-context.xml")
@PropertySource("classpath:/miso-web/src/main/resources/miso.properties")
public abstract class AbstractCT {
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


}
