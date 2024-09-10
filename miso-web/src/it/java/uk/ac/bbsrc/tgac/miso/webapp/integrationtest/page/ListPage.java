package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class ListPage extends AbstractListPage {

  @FindBy(className = "dataTables_wrapper")
  private WebElement tableWrapper;
  @FindBy(className = "parsley-error")
  private WebElement errors;

  private final DataTable table;

  public ListPage(WebDriver driver) {
    super(driver, ListPage::new);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(visibilityOf(tableWrapper));
    table = new DataTable(driver, tableWrapper.getAttribute("id"));
  }

  public static ListPage getListPage(WebDriver driver, String baseUrl, String listTarget) {
    String url = String.format("%s%s", baseUrl, listTarget);
    driver.get(url);
    return new ListPage(driver);
  }

  public String clickButtonAndGetUrlWithConfirm(String linkText) {
    return clickLinkButtonAndGetUrl(linkText, null, true);
  }

  @Override
  public DataTable getTable() {
    return table;
  }

  @Override
  public WebElement getErrors() {
    return errors;
  }

}
