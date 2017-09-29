package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Represents a page with the header, footer, navigation tabs, and left navigation menu. Note that some of these
 * elements may be hidden if not logged in, such as on the login screen
 */
public abstract class HeaderFooterPage extends AbstractPage {

  @FindBy(css = "#footer p")
  private WebElement footerParagraph;

  public HeaderFooterPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
  }

  public String getFooterText() {
    return footerParagraph.getText();
  }

}
