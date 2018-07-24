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

public class BulkSampleQCPage extends HeaderFooterPage {

  public static class SamQcColumns {
    public static final String ALIAS = "Sample Alias";
    public static final String DATE = "Date";
    public static final String TYPE = "Type";
    public static final String RESULT = "Result";
    public static final String UNITS = "Units";

    private SamQcColumns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private static final By ADD_BUTTON_TEXT = By.linkText("Add QCs");
  private static final By EDIT_BUTTON_TEXT = By.linkText("Edit QCs");

  @FindBy(id = "bulkactions")
  private WebElement toolbar;

  private final HandsOnTable table;

  public BulkSampleQCPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Add Sample QCs "), titleContains("Edit Sample QCs ")));
    table = new HandsOnTable(driver);
  }

  public static BulkSampleQCPage getForEdit(WebDriver driver, String baseUrl, Collection<Long> sampleIds) {
    String ids = Joiner.on(',').join(sampleIds);
    String url = baseUrl + "miso/qc/bulk/editFrom/Sample?entityIds=" + ids;
    driver.get(url);
    return new BulkSampleQCPage(driver);
  }

  public static BulkSampleQCPage getForAdd(WebDriver driver, String baseUrl, Collection<Long> libraryIds, int copies) {
    String ids = Joiner.on(',').join(libraryIds);
    String url = baseUrl + "miso/qc/bulk/addFrom/Sample?entityIds=" + ids + "&copies=" + copies;
    driver.get(url);
    return new BulkSampleQCPage(driver);
  }

  public HandsOnTable getTable() {
    return table;
  }

}
