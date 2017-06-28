package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.HomePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LoginPage;

import io.github.bonigarcia.wdm.PhantomJsDriverManager;

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
    LoginPage loginPage = new LoginPage(getDriver(), getBaseUrl());
    HomePage homePage = loginPage.login("admin", "admin");
    assertNotNull(homePage);
  }

}
