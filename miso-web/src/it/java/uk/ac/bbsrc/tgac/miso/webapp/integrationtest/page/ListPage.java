package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class ListPage extends HeaderFooterPage implements AbstractListPage {

  @FindBy(className = "dataTables_wrapper")
  private WebElement tableWrapper;
  @FindBy(className = "parsley-error")
  private WebElement errors;

  private WebElement addButton = null;

  private final DataTable table;

  public ListPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(visibilityOf(tableWrapper));
    table = new DataTable(driver, tableWrapper.getAttribute("id"));
    if (!driver.findElements(By.linkText("Add")).isEmpty()) {
      addButton = driver.findElement(By.linkText("Add"));
    }
  }

  public static ListPage getListPage(WebDriver driver, String baseUrl, String listTarget) {
    String url = String.format("%smiso/%s", baseUrl, listTarget);
    driver.get(url);
    return new ListPage(driver);
  }

  @Override
  public DataTable getTable() {
    return table;
  }

  @Override
  public WebElement getErrors() {
    return errors;
  }

  public void clickAddButton(boolean expectAddDialog) {
    if (addButton == null) throw new IllegalArgumentException("Add button is not present on page");
    addButton.click();
    if (expectAddDialog) {
      // add the dialog
    } else {
      WebElement html = getHtmlElement();
      waitForPageRefresh(html);
    }
  }
}
