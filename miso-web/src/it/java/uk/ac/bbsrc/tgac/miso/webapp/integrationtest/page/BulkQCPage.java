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

public class BulkQCPage extends HeaderFooterPage {

  public static class QcColumns {
    public static final String SAMPLE_ALIAS = "Sample Alias";
    public static final String LIBRARY_ALIAS = "Library Alias";
    public static final String POOL_ALIAS = "Pool Alias";
    public static final String DATE = "Date";
    public static final String TYPE = "Type";
    public static final String RESULT = "Result";
    public static final String UNITS = "Units";
    public static final String DESCRIPTION = "Description";

    private QcColumns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private static final By ADD_BUTTON_TEXT = By.linkText("Add QCs");
  private static final By EDIT_BUTTON_TEXT = By.linkText("Edit QCs");

  @FindBy(id = "bulkactions")
  private WebElement toolbar;

  private final HandsOnTable table;

  public BulkQCPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Add Sample QCs "), titleContains("Edit Sample QCs "), titleContains("Add Library QCs "),
        titleContains("Edit Library QCs "), titleContains("Add Pool QCs "), titleContains("Edit Pool QCs ")));
    table = new HandsOnTable(driver);
  }

  public static BulkQCPage getForEditSample(WebDriver driver, String baseUrl, Collection<Long> sampleIds) {
    String ids = Joiner.on(',').join(sampleIds);
    String url = baseUrl + "miso/qc/bulk/editFrom/Sample?entityIds=" + ids;
    driver.get(url);
    return new BulkQCPage(driver);
  }

  public static BulkQCPage getForAddSample(WebDriver driver, String baseUrl, Collection<Long> sampleIds, int copies) {
    String ids = Joiner.on(',').join(sampleIds);
    String url = baseUrl + "miso/qc/bulk/addFrom/Sample?entityIds=" + ids + "&copies=" + copies;
    driver.get(url);
    return new BulkQCPage(driver);
  }

  public static BulkQCPage getForEditLibrary(WebDriver driver, String baseUrl, Collection<Long> libraryIds) {
    String ids = Joiner.on(',').join(libraryIds);
    String url = baseUrl + "miso/qc/bulk/editFrom/Library?entityIds=" + ids;
    driver.get(url);
    return new BulkQCPage(driver);
  }

  public static BulkQCPage getForAddLibrary(WebDriver driver, String baseUrl, Collection<Long> libraryIds, int copies) {
    String ids = Joiner.on(',').join(libraryIds);
    String url = baseUrl + "miso/qc/bulk/addFrom/Library?entityIds=" + ids + "&copies=" + copies;
    driver.get(url);
    return new BulkQCPage(driver);
  }

  public static BulkQCPage getForEditPool(WebDriver driver, String baseUrl, Collection<Long> poolIds) {
    String ids = Joiner.on(',').join(poolIds);
    String url = baseUrl + "miso/qc/bulk/editFrom/Pool?entityIds=" + ids;
    driver.get(url);
    return new BulkQCPage(driver);
  }

  public static BulkQCPage getForAddPool(WebDriver driver, String baseUrl, Collection<Long> poolIds, int copies) {
    String ids = Joiner.on(',').join(poolIds);
    String url = baseUrl + "miso/qc/bulk/addFrom/Pool?entityIds=" + ids + "&copies=" + copies;
    driver.get(url);
    return new BulkQCPage(driver);
  }

  public HandsOnTable getTable() {
    return table;
  }

}
