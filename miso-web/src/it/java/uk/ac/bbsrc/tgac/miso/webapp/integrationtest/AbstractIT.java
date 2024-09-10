package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.io.File;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;

import io.github.bonigarcia.wdm.WebDriverManager;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.HomePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LoginPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.TestRunner;

@RunWith(TestRunner.class)
@ContextConfiguration("/it-context.xml")
public abstract class AbstractIT {

  private static final Logger log = LoggerFactory.getLogger(AbstractIT.class);

  public static final TimeZone EASTERN_TIME_ZONE = TimeZone.getTimeZone("Canada/Eastern");

  private static final String SCRIPT_DIR = System.getProperty("basedir") + "/src/it/resources/db/migration/";
  private static final String CLEAR_DATA_SCRIPT = "clear_test_data.sql";
  private static final String PLAIN_SCRIPT = "plainSample_integration_test_data.sql";
  private static final String DETAILED_SCRIPT = "integration_test_data.sql";

  private static Boolean constantsComplete = false;

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private DataSource dataSource;

  private WebDriver driver;
  private static final String baseUrl = System.getProperty("miso.it.baseUrl");

  @BeforeClass
  public static final void setupAbstractClass() {
    TimeZone.setDefault(EASTERN_TIME_ZONE);
    WebDriverManager.chromedriver().setup();
  }

  @Before
  public final void setupAbstractTest() {
    ChromeOptions opts = new ChromeOptions();
    opts.setHeadless(true);
    opts.addArguments("--disable-gpu");
    LoggingPreferences loggingPrefs = new LoggingPreferences();
    loggingPrefs.enable(LogType.BROWSER, Level.ALL);
    opts.setCapability(CapabilityType.LOGGING_PREFS, loggingPrefs);
    driver = new ChromeDriver(opts);

    // don't allow page load or script execution to take longer than 10 seconds
    driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
    driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);
    // large width is important so that all columns of handsontables get rendered
    driver.manage().window().setSize(new Dimension(6000, 4000));

    // reset test data for each test
    Resource clearData = new FileSystemResource(getScript(CLEAR_DATA_SCRIPT));
    Resource testData = new FileSystemResource(getScript(isDetailedSampleMode() ? DETAILED_SCRIPT : PLAIN_SCRIPT));
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator(clearData, testData);
    populator.execute(dataSource);
  }

  private File getScript(String filename) {
    File script = new File(SCRIPT_DIR + filename);
    if (!script.exists()) {
      throw new IllegalStateException("Script not found: " + filename);
    }
    return script;
  }

  protected boolean isDetailedSampleMode() {
    return true;
  }

  @After
  public final void teardownAbstractTest() {
    if (driver != null) {
      driver.quit();
    }
  }

  protected final WebDriver getDriver() {
    return driver;
  }

  protected final String getBaseUrl() {
    return baseUrl;
  }

  protected final Session getSession() {
    return sessionFactory.openSession();
  }

  protected final HomePage login() {
    LoginPage loginPage = LoginPage.get(getDriver(), getBaseUrl());
    HomePage homePage = loginPage.loginValidUser("user", "user");
    assertNotNull(homePage);
    verifyConstants();
    return homePage;
  }

  protected final void loginAdmin() {
    LoginPage loginPage = LoginPage.get(getDriver(), getBaseUrl());
    HomePage homePage = loginPage.loginValidUser("admin", "admin");
    assertNotNull(homePage);
    verifyConstants();
  }

  private void verifyConstants() {
    if (!constantsComplete()) {
      log.warn("Missing data in constants.js - refreshing");
      ((JavascriptExecutor) getDriver())
          .executeScript("jQuery.ajax({type: 'POST', url: '/rest/admin/constants/refresh'})");
      for (int attempt = 0; attempt < 5; attempt++) {
        try {
          Thread.sleep(30000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        getDriver().navigate().refresh();
        if (constantsComplete()) {
          break;
        }
      }
      assertTrue("Attempts to refresh constants failed", constantsComplete());
    }
  }

  private Boolean constantsComplete() {
    if (!constantsComplete) {
      constantsComplete = (Boolean) ((JavascriptExecutor) getDriver())
          .executeScript("return Constants.attachmentCategories.some(cat => cat.alias === 'last entry');");
    }
    return constantsComplete;
  }

}
