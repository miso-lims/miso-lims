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

public class BulkLibraryPage extends HeaderFooterPage {

  public static class Columns {
    public static final String NAME = "Library Name";
    public static final String ALIAS = "Library Alias";
    public static final String SAMPLE_ALIAS = "Sample Alias";
    public static final String ID_BARCODE = "Matrix Barcode";
    public static final String SAMPLE_LOCATION = "Sample Location";
    public static final String DESCRIPTION = "Description";
    public static final String RECEIVE_DATE = "Date of receipt";
    public static final String GROUP_ID = "Group ID";
    public static final String GROUP_DESC = "Group Desc.";
    public static final String DESIGN = "Design";
    public static final String CODE = "Code";
    public static final String PLATFORM = "Platform";
    public static final String LIBRARY_TYPE = "Type";
    public static final String SELECTION = "Selection";
    public static final String STRATEGY = "Strategy";
    public static final String INDEX_FAMILY = "Index Kit";
    public static final String INDEX_1 = "Index 1";
    public static final String INDEX_2 = "Index 2";
    public static final String KIT_DESCRIPTOR = "Kit";
    public static final String QC_PASSED = "QC Passed?";
    public static final String SIZE = "Size (bp)";
    public static final String VOLUME = "Vol. (µl)";
    public static final String CONCENTRATION = "Conc.";

    private Columns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private static final By EDIT_BUTTON_TEXT = By.linkText("Edit");
  private static final By PROPAGATE_BUTTON_TEXT = By.linkText("Make dilutions");
  private static final By SORT_BY_SAMPLE_LOCATION_ROWS = By.id("sortrows");
  private static final By SORT_BY_SAMPLE_LOCATION_COLS = By.id("sortcolumns");

  @FindBy(id = "bulkactions")
  private WebElement toolbar;

  private final HandsOnTable table;

  public BulkLibraryPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Create Libraries "), titleContains("Edit Libraries ")));
    table = new HandsOnTable(driver);
  }

  public static BulkLibraryPage getForEdit(WebDriver driver, String baseUrl, Collection<Long> libraryIds) {
    String ids = Joiner.on(',').join(libraryIds);
    String url = baseUrl + "miso/library/bulk/edit?ids=" + ids;
    driver.get(url);
    return new BulkLibraryPage(driver);
  }

  public static BulkLibraryPage getForPropagate(WebDriver driver, String baseUrl, Collection<Long> sampleIds, int replicates) {
    String ids = Joiner.on(',').join(sampleIds);
    String url = baseUrl + "miso/library/bulk/propagate?ids=" + ids + "&replicates=" + replicates;
    driver.get(url);
    return new BulkLibraryPage(driver);
  }

  public static BulkLibraryPage getForReceive(WebDriver driver, String baseUrl, int quantity, Integer projectId, int aliquotClassId) {
    String url = baseUrl + "miso/library/bulk/receive?quantity=" + quantity
        + "&projectId=" + (projectId == null ? "" : projectId)
        + "&sampleClassId=" + aliquotClassId;
    driver.get(url);
    return new BulkLibraryPage(driver);
  }

  public HandsOnTable getTable() {
    return table;
  }

  public BulkLibraryPage chainEdit() {
    WebElement html = getHtmlElement();
    toolbar.findElement(EDIT_BUTTON_TEXT).click();
    waitForPageRefresh(html);
    return new BulkLibraryPage(getDriver());
  }

  public BulkDilutionPage chainPropagateDilutions() {
    toolbar.findElement(PROPAGATE_BUTTON_TEXT).click();
    return new BulkDilutionPage(getDriver());
  }

  public void sortBySampleLocationRows() {
    toolbar.findElement(SORT_BY_SAMPLE_LOCATION_ROWS).click();
  }

  public void sortBySampleLocationColumns() {
    toolbar.findElement(SORT_BY_SAMPLE_LOCATION_COLS).click();
  }

}
