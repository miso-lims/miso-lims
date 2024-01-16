package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.Collection;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.common.base.Joiner;

import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkLibraryPage extends BulkPage {

  public static class LibColumns {
    public static final String NAME = "Name";
    public static final String ALIAS = "Alias";
    public static final String SAMPLE_ALIAS = "Sample Alias";
    public static final String SAMPLE_NAME = "Sample Name";
    public static final String TISSUE_ORIGIN = "Tissue Origin";
    public static final String TISSUE_TYPE = "Tissue Type";
    public static final String ID_BARCODE = "Matrix Barcode";
    public static final String BOX_SEARCH = "Box Search";
    public static final String BOX_ALIAS = "Box Alias";
    public static final String BOX_POSITION = "Position";
    public static final String DISCARDED = "Discarded";
    public static final String SAMPLE_LOCATION = "Sample Location";
    public static final String DESCRIPTION = "Description";
    public static final String CREATION_DATE = "Creation Date";
    public static final String WORKSTATION = "Workstation";
    public static final String THERMAL_CYCLER = "Thermal Cycler";
    public static final String RECEIVE_DATE = "Date of Receipt";
    public static final String RECEIVE_TIME = "Time of Receipt";
    public static final String RECEIVED_FROM = "Received From";
    public static final String RECEIVED_BY = "Received By";
    public static final String RECEIPT_CONFIRMED = "Receipt Confirmed";
    public static final String RECEIPT_QC_PASSED = "Receipt QC Passed";
    public static final String RECEIPT_QC_NOTE = "Receipt QC Note";
    public static final String REQUISITION_ALIAS = "Requisition Alias";
    public static final String REQUISITION = "Requisition";
    public static final String EFFECTIVE_GROUP_ID = "Effective Group ID";
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
    public static final String UMIS = "Has UMIs";
    public static final String KIT_DESCRIPTOR = "Kit";
    public static final String KIT_LOT = "Kit Lot";
    public static final String QC_STATUS = "QC Status";
    public static final String QC_NOTE = "QC Note";
    public static final String SIZE = "Size (bp)";
    public static final String INITIAL_VOLUME = "Initial Volume";
    public static final String VOLUME = "Volume";
    public static final String VOLUME_UNITS = "Vol. Units";
    public static final String PARENT_NG_USED = "Parent ng Used";
    public static final String PARENT_VOLUME_USED = "Parent Vol. Used";
    public static final String CONCENTRATION = "Conc.";
    public static final String CONCENTRATION_UNITS = "Conc. Units";
    public static final String SPIKE_IN = "Spike-In";
    public static final String SPIKE_IN_DILUTION = "Spike-In Dilution Factor";
    public static final String SPIKE_IN_VOL = "Spike-In Volume";
    public static final String TEMPLATE = "Template";
    public static final String SOP = "SOP";

    private LibColumns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  public static final String SORT_SAMPLE_LOCATION_ROWS = "Sample Location (by rows)";
  public static final String SORT_SAMPLE_LOCATION_COLS = "Sample Location (by columns)";

  private static final By EDIT_BUTTON_TEXT = By.linkText("Edit");
  private static final By PROPAGATE_BUTTON_TEXT = By.linkText("Make aliquots");

  private WebElement toolbar;

  private HandsOnTable table;

  public BulkLibraryPage(WebDriver driver) {
    super(driver);
    waitWithTimeout().until(or(titleContains("Create Libraries "), titleContains("Edit Libraries ")));
    refreshElements();
  }

  @Override
  protected void refreshElements() {
    table = new HandsOnTable(getDriver());
    toolbar = getDriver().findElement(By.id("bulkactions"));
  }

  public static BulkLibraryPage getForEdit(WebDriver driver, String baseUrl, Collection<Long> libraryIds) {
    String ids = Joiner.on(',').join(libraryIds);
    String url = baseUrl + "miso/library/bulk/edit";
    postData(driver, url, new MapBuilder<String, String>().put("ids", ids).build());
    return new BulkLibraryPage(driver);
  }

  public static BulkLibraryPage getForPropagate(WebDriver driver, String baseUrl, List<Long> sampleIds,
      List<Integer> replicates) {
    String ids = Joiner.on(',').join(sampleIds);
    String replicatesString = Joiner.on(',').join(replicates);
    String url = baseUrl + "miso/library/bulk/propagate";
    postData(driver, url, new MapBuilder<String, String>()
        .put("ids", ids)
        .put("replicates", replicatesString)
        .build());
    return new BulkLibraryPage(driver);
  }

  public static BulkLibraryPage getForReceive(WebDriver driver, String baseUrl, int quantity, Long projectId,
      Long aliquotClassId) {
    String url = baseUrl + "miso/library/bulk/receive";
    MapBuilder<String, String> params = new MapBuilder<String, String>()
        .put("quantity", String.valueOf(quantity));
    if (projectId != null) {
      params.put("projectId", projectId.toString());
    }
    if (aliquotClassId != null) {
      params.put("sampleClassId", aliquotClassId.toString());
    }
    postData(driver, url, params.build());
    return new BulkLibraryPage(driver);
  }

  @Override
  public HandsOnTable getTable() {
    return table;
  }

  public BulkLibraryPage chainEdit() {
    WebElement html = getHtmlElement();
    toolbar.findElement(EDIT_BUTTON_TEXT).click();
    waitForPageRefresh(html);
    return new BulkLibraryPage(getDriver());
  }

  public BulkLibraryAliquotPage chainPropagateLibraryAliquots() {
    toolbar.findElement(PROPAGATE_BUTTON_TEXT).click();
    clickOk();
    return new BulkLibraryAliquotPage(getDriver());
  }

}
