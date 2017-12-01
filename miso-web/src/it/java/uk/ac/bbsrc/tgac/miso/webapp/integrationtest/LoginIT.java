package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.HomePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LoginPage;

public class LoginIT extends AbstractIT {

  private LoginPage page;

  @Before
  public void setup() {
    page = LoginPage.get(getDriver(), getBaseUrl());
    assertNotNull(page);
  }

  @Test
  public void testLogin() {
    HomePage homepage = page.loginValidUser("admin", "admin");
    assertNotNull(homepage);
  }

  @Test
  public void testBadLogin() {
    assertNull(page.getErrorMessage());
    LoginPage failed = page.loginInvalidUser("notauser", "notapassword");
    assertNotNull(failed);
    String error = failed.getErrorMessage();
    assertNotNull(error);
    assertTrue(error.contains("Access denied"));
  }

}
