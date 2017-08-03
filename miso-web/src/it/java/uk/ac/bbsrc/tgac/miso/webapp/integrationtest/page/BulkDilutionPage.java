package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.Collection;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.base.Joiner;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkDilutionPage extends AbstractPage {

  public static class Columns {
    // TODO: add column constants

    private Columns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private static final By EDIT_BUTTON_TEXT = By.linkText("Edit");

  @FindBy(id = "bulkactions")
  private WebElement toolbar;

  private final HandsOnTable table;

  public BulkDilutionPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Create Dilutions from Libraries "), titleContains("Edit Dilutions ")));
    table = new HandsOnTable(driver);
  }

  public static BulkDilutionPage getForEdit(WebDriver driver, String baseUrl, Collection<Long> dilutionIds) {
    String ids = Joiner.on(',').join(dilutionIds);
    String url = baseUrl + "miso/library/dilution/bulk/edit?ids=" + ids;
    driver.get(url);
    return new BulkDilutionPage(driver);
  }

  public static BulkDilutionPage getForPropagate(WebDriver driver, String baseUrl, Collection<Long> libraryIds) {
    String ids = Joiner.on(',').join(libraryIds);
    String url = baseUrl + "miso/library/dilutions/bulk/propagate?ids=" + ids;
    driver.get(url);
    return new BulkDilutionPage(driver);
  }

  public HandsOnTable getTable() {
    return table;
  }

  public BulkDilutionPage chainEdit() {
    toolbar.findElement(EDIT_BUTTON_TEXT).click();
    waitForPageRefresh();
    return new BulkDilutionPage(getDriver());
  }

}
