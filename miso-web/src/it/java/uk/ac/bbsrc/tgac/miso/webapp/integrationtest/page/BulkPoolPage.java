package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.Collection;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.base.Joiner;

import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkPoolPage extends BulkPage {

  public static class Columns {
    public static final String NAME = "Name";
    public static final String ALIAS = "Alias";
    public static final String DESCRIPTION = "Description";
    public static final String BARCODE = "Matrix Barcode";
    public static final String BOX_SEARCH = "Box Search";
    public static final String BOX_ALIAS = "Box Alias";
    public static final String BOX_POSITION = "Position";
    public static final String DISCARDED = "Discarded";
    public static final String CREATE_DATE = "Creation Date";
    public static final String SIZE = "Size (bp)";
    public static final String CONCENTRATION = "Conc.";
    public static final String CONCENTRATION_UNITS = "Conc. Units";
    public static final String VOLUME = "Volume";
    public static final String VOLUME_UNITS = "Vol. Units";
    public static final String QC_STATUS = "QC Status";
    public static final String READY_TO_RUN = "Ready to Run?";

    public static final String LIBRARY_ALIQUOT_NAME = "Library Aliquot Name";
    public static final String LIBRARY_SIZE = "Size (bp)";
    public static final String POOL = "Pool";

    private Columns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private static final By EDIT_BUTTON_TEXT = By.linkText("Edit");
  private static final String POOL_SEPARATELY_URL_FRAGMENT = "libraryaliquot/bulk/pool-separate";
  private static final String POOL_TOGETHER_URL_FRAGMENT = "libraryaliquot/bulk/merge";
  private static final String MERGE_URL_FRAGMENT = "pool/bulk/merge";

  @FindBy(id = "bulkactions")
  private WebElement toolbar;

  private HandsOnTable table;

  public BulkPoolPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout()
        .until(
            or(titleContains("Create Pools from Library Aliquots "), titleContains("Edit Pools "),
                titleContains("Create Pool from Pools "),
                titleContains("Merge Pools ")));
    refreshElements();
  }

  @Override
  protected void refreshElements() {
    table = new HandsOnTable(getDriver());
  }

  public static BulkPoolPage getForEdit(WebDriver driver, String baseUrl, Collection<Long> poolIds) {
    String ids = Joiner.on(',').join(poolIds);
    String url = baseUrl + "pool/bulk/edit";
    MapBuilder<String, String> params = new MapBuilder<String, String>()
        .put("ids", ids);
    postData(driver, url, params.build());
    return new BulkPoolPage(driver);
  }

  public static BulkPoolPage getForMerge(WebDriver driver, String baseUrl, List<Long> poolIds,
      List<Integer> proportions) {
    String ids = Joiner.on(',').join(poolIds);
    String proportionsString = Joiner.on(',').join(proportions);
    String url = baseUrl + MERGE_URL_FRAGMENT;
    MapBuilder<String, String> params = new MapBuilder<String, String>()
        .put("ids", ids)
        .put("proportions", proportionsString);
    postData(driver, url, params.build());
    return new BulkPoolPage(driver);
  }

  public static BulkPoolPage getForPoolSeparately(WebDriver driver, String baseUrl, Collection<Long> aliquotIds) {
    String ids = Joiner.on(",").join(aliquotIds);
    String url = baseUrl + POOL_SEPARATELY_URL_FRAGMENT;
    MapBuilder<String, String> params = new MapBuilder<String, String>()
        .put("ids", ids);
    postData(driver, url, params.build());
    return new BulkPoolPage(driver);
  }

  public static BulkPoolPage getForPoolTogether(WebDriver driver, String baseUrl, Collection<Long> aliquotIds) {
    String ids = Joiner.on(",").join(aliquotIds);
    String url = baseUrl + POOL_TOGETHER_URL_FRAGMENT;
    MapBuilder<String, String> params = new MapBuilder<String, String>()
        .put("ids", ids);
    postData(driver, url, params.build());
    return new BulkPoolPage(driver);
  }

  protected WebElement getToolbar() {
    return toolbar;
  }

  @Override
  public HandsOnTable getTable() {
    return table;
  }

  public BulkPoolPage chainEdit() {
    WebElement html = getHtmlElement();
    getToolbar().findElement(EDIT_BUTTON_TEXT).click();
    waitForPageRefresh(html);
    return new BulkPoolPage(getDriver());
  }

}
