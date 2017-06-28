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
    page = new LoginPage(getDriver(), getBaseUrl());
    assertNotNull(page);
  }

  @Test
  public void testLogin() {
    HomePage homepage = page.login("admin", "admin");
    assertNotNull(homepage);
  }

  @Test
  public void testVersion() {
    String text = page.getFooterParagraph();
    assertNotNull(text);
    assertTrue(text.matches(".*Version: .*\\d+\\.\\d+\\.\\d+.*"));
  }

}
