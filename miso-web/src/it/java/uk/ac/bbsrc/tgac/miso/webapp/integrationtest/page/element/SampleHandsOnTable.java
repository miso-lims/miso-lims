package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.MoreExpectedConditions.textDoesNotContain;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;

public class SampleHandsOnTable extends HandsOnTable {

  private static final String LOOKUP_TEXT = "(...searching...)";

  public SampleHandsOnTable(WebDriver driver) {
    super(driver);
  }

  public void waitForIdentityLookup(int rowNum) {
    WebElement lookupField = getCell(SamColumns.IDENTITY_ALIAS, rowNum);
    waitUntil(textDoesNotContain(lookupField, LOOKUP_TEXT));
  }

}
