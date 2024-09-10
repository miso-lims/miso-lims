package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import org.openqa.selenium.WebDriver;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class IdentitySearchPage extends HeaderFooterPage {

  private final DataTable samplesTable;

  public IdentitySearchPage(WebDriver driver) {
    super(driver);
    samplesTable = new DataTable(driver, "list_samples_wrapper");
  }

  public static IdentitySearchPage get(WebDriver driver, String baseUrl) {
    driver.get(baseUrl + "tools/identitysearch");
    return new IdentitySearchPage(driver);
  }

  public DataTable getSamplesTable() {
    return samplesTable;
  }

}
