package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.HomePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LoginPage;

import io.github.bonigarcia.wdm.WebDriverManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/it-context.xml")
public abstract class AbstractIT {

  private static final String SCRIPT_DIR = System.getProperty("basedir") + "/src/it/resources/db/migration/";
  private static final String CLEAR_DATA_SCRIPT = "clear_test_data.sql";
  private static final String PLAIN_SCRIPT = "plainSample_integration_test_data.sql";
  private static final String DETAILED_SCRIPT = "integration_test_data.sql";

  @Autowired
  private SessionFactory sessionFactory;

  @Autowired
  private DataSource dataSource;

  private WebDriver driver;
  private static final String baseUrl = System.getProperty("miso.it.baseUrl");

  @BeforeClass
  public static final void setupAbstractClass() {
    WebDriverManager.chromedriver().setup();
  }

  @Before
  public final void setupAbstractTest() {
    ChromeOptions opts = new ChromeOptions();
    opts.setHeadless(true);
    // large width is important so that all columns of handsontables get rendered
    opts.addArguments("--disable-gpu", "--window-size=4000x1440");
    driver = new ChromeDriver(opts);

    // don't allow page load or script execution to take longer than 10 seconds
    driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
    driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);

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

  protected final void loginAdmin() {
    LoginPage loginPage = LoginPage.get(getDriver(), getBaseUrl());
    HomePage homePage = loginPage.loginValidUser("admin", "admin");
    assertNotNull(homePage);
  }

}
