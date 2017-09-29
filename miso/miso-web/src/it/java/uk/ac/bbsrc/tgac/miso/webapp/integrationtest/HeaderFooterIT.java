package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import org.junit.Test;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.HeaderFooterPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LoginPage;

public class HeaderFooterIT extends AbstractIT {

  @Test
  public void testVersion() {
    HeaderFooterPage page = LoginPage.get(getDriver(), getBaseUrl());
    String text = page.getFooterText();
    assertNotNull(text);
    assertTrue("Version number in footer", text.matches(".*Version: .*\\d+\\.\\d+\\.\\d+.*"));
  }

}
