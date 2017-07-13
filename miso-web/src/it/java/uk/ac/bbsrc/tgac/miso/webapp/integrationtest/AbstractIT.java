package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.assertNotNull;
import io.github.bonigarcia.wdm.PhantomJsDriverManager;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.HomePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LoginPage;

public class AbstractIT {

  private WebDriver driver;
  private static final String baseUrl = System.getProperty("miso.it.baseUrl");

  @BeforeClass
  public static final void setupAbstractClass() {
    PhantomJsDriverManager.getInstance().setup();
  }

  @Before
  public final void setupAbstractTest() {
    driver = new PhantomJSDriver();
    // don't allow page load or script execution to take longer than 10 seconds
    driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
    driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
    // large width is important so that all columns of handsontables get rendered
    driver.manage().window().setSize(new Dimension(2560, 1440));
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

  protected final void loginAdmin() {
    LoginPage loginPage = LoginPage.get(getDriver(), getBaseUrl());
    HomePage homePage = loginPage.loginValidUser("admin", "admin");
    assertNotNull(homePage);
  }

}
