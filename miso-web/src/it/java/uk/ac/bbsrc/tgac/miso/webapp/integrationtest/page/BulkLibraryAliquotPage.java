package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.Collection;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.base.Joiner;

import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkLibraryAliquotPage extends BulkPage {

  public static class LibraryAliquotColumns {
    public static final String NAME = "Name";
    public static final String ALIAS = "Alias";
    public static final String PARENT_NAME = "Parent Name";
    public static final String PARENT_ALIAS = "Parent Alias";
    public static final String PARENT_LOCATION = "Parent Location";
    public static final String TISSUE_ORIGIN = "Tissue Origin";
    public static final String TISSUE_TYPE = "Tissue Type";
    public static final String DESCRIPTION = "Description";
    public static final String ID_BARCODE = "Matrix Barcode";
    public static final String BOX_SEARCH = "Box Search";
    public static final String BOX_ALIAS = "Box Alias";
    public static final String BOX_POSITION = "Position";
    public static final String DISCARDED = "Discarded";
    public static final String DISTRIBUTED = "Distributed";
    public static final String DISTRIBUTION_DATE = "Distribution Date";
    public static final String DISTRIBUTION_RECIPIENT = "Distribution Recipient";
    public static final String EFFECTIVE_GROUP_ID = "Effective Group ID";
    public static final String GROUP_ID = "Group ID";
    public static final String GROUP_DESCRIPTION = "Group Desc.";
    public static final String DESIGN_CODE = "Design Code";
    public static final String QC_STATUS = "QC Status";
    public static final String QC_NOTE = "QC Note";
    public static final String SIZE = "Size (bp)";
    public static final String CONCENTRATION = "Conc.";
    public static final String CONCENTRATION_UNITS = "Conc. Units";
    public static final String VOLUME = "Volume";
    public static final String VOLUME_UNITS = "Vol. Units";
    public static final String NG_USED = "Parent ng Used";
    public static final String VOLUME_USED = "Parent Vol. Used";
    public static final String CREATION_DATE = "Creation Date";
    public static final String KIT = "Kit";
    public static final String KIT_LOT = "Kit Lot";
    public static final String TARGETED_SEQUENCING = "Targeted Sequencing";

    private LibraryAliquotColumns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private static final By EDIT_BUTTON_TEXT = By.linkText("Edit");
  private static final By POOL_TOGETHER_BUTTON_TEXT = By.linkText("Pool Together");
  private static final By POOL_SEPARATELY_BUTTON_TEXT = By.linkText("Pool Separately");

  private WebElement toolbar;

  private HandsOnTable table;

  public BulkLibraryAliquotPage(WebDriver driver) {
    super(driver);
    waitWithTimeout()
        .until(or(titleContains("Create Library Aliquots from "), titleContains("Edit Library Aliquots ")));
    refreshElements();
  }

  @Override
  protected void refreshElements() {
    table = new HandsOnTable(getDriver());
    toolbar = getDriver().findElement(By.id("bulkactions"));
  }

  public static BulkLibraryAliquotPage getForEdit(WebDriver driver, String baseUrl, Collection<Long> aliquotIds) {
    String ids = Joiner.on(',').join(aliquotIds);
    String url = baseUrl + "libraryaliquot/bulk/edit";
    postData(driver, url, new MapBuilder<String, String>().put("ids", ids).build());
    return new BulkLibraryAliquotPage(driver);
  }

  public static BulkLibraryAliquotPage getForPropagate(WebDriver driver, String baseUrl, Collection<Long> libraryIds) {
    String ids = Joiner.on(',').join(libraryIds);
    String url = baseUrl + "libraryaliquot/bulk/propagate";
    postData(driver, url, new MapBuilder<String, String>().put("ids", ids).build());
    return new BulkLibraryAliquotPage(driver);
  }

  public static BulkLibraryAliquotPage getForRepropagate(WebDriver driver, String baseUrl,
      Collection<Long> aliquotIds) {
    String ids = Joiner.on(',').join(aliquotIds);
    String url = baseUrl + "libraryaliquot/bulk/repropagate";
    postData(driver, url, new MapBuilder<String, String>().put("ids", ids).build());
    return new BulkLibraryAliquotPage(driver);
  }

  @Override
  public HandsOnTable getTable() {
    return table;
  }

  public BulkLibraryAliquotPage chainEdit() {
    WebElement html = getHtmlElement();
    toolbar.findElement(EDIT_BUTTON_TEXT).click();
    waitForPageRefresh(html);
    return new BulkLibraryAliquotPage(getDriver());
  }

  public BulkPoolPage chainPoolTogether() {
    WebElement html = getHtmlElement();
    toolbar.findElement(POOL_TOGETHER_BUTTON_TEXT).click();
    waitForPageRefresh(html);
    return new BulkPoolPage(getDriver());
  }

  public BulkPoolPage chainPoolSeparately() {
    WebElement html = getHtmlElement();
    toolbar.findElement(POOL_SEPARATELY_BUTTON_TEXT).click();
    clickOk();
    waitForPageRefresh(html);
    return new BulkPoolPage(getDriver());
  }

}
