package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.Collection;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.google.common.base.Joiner;

import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkQCPage extends BulkPage {

  public static class QcColumns {
    public static final String SAMPLE_ALIAS = "Sample Alias";
    public static final String LIBRARY_ALIAS = "Library Alias";
    public static final String POOL_ALIAS = "Pool Alias";
    public static final String LIBRARY_ALIQUOT_ALIAS = "LibraryAliquot Alias";
    public static final String DATE = "Date";
    public static final String TYPE = "Type";
    public static final String INSTRUMENT = "Instrument";
    public static final String KIT = "Kit";
    public static final String KIT_LOT = "Kit Lot";
    public static final String RESULT = "Result";
    public static final String UNITS = "Units";
    public static final String DESCRIPTION = "Description";

    private QcColumns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private HandsOnTable table;

  public BulkQCPage(WebDriver driver) {
    super(driver);
    waitWithTimeout().until(
        or(titleContains("Add Sample QCs "), titleContains("Edit Sample QCs "), titleContains("Add Library QCs "),
            titleContains("Edit Library QCs "), titleContains("Add Pool QCs "), titleContains("Edit Pool QCs "),
                titleContains("Add LibraryAliquot QCs"), titleContains("Edit LibraryAliquot QCs")));
    refreshElements();
  }

  @Override
  protected void refreshElements() {
    table = new HandsOnTable(getDriver());
  }

  public static BulkQCPage getForEditSample(WebDriver driver, String baseUrl, Collection<Long> sampleIds,
      int addControls) {
    Map<String, String> params = new MapBuilder<String, String>()
        .put("entityIds", Joiner.on(',').join(sampleIds))
        .put("addControls", Integer.toString(addControls))
        .build();
    postData(driver, baseUrl + "qc/bulk/editFrom/Sample", params);
    return new BulkQCPage(driver);
  }

  public static BulkQCPage getForAddSample(WebDriver driver, String baseUrl, Collection<Long> sampleIds, int copies,
      int controls) {
    Map<String, String> params = new MapBuilder<String, String>()
        .put("entityIds", Joiner.on(',').join(sampleIds))
        .put("copies", Integer.toString(copies))
        .put("controls", Integer.toString(controls))
        .build();
    postData(driver, baseUrl + "qc/bulk/addFrom/Sample", params);
    return new BulkQCPage(driver);
  }

  public static BulkQCPage getForEditLibrary(WebDriver driver, String baseUrl, Collection<Long> libraryIds,
      int addControls) {
    Map<String, String> params = new MapBuilder<String, String>()
        .put("entityIds", Joiner.on(',').join(libraryIds))
        .put("addControls", Integer.toString(addControls))
        .build();
    postData(driver, baseUrl + "qc/bulk/editFrom/Library", params);
    return new BulkQCPage(driver);
  }

  public static BulkQCPage getForAddLibrary(WebDriver driver, String baseUrl, Collection<Long> libraryIds, int copies,
      int controls) {
    Map<String, String> params = new MapBuilder<String, String>()
        .put("entityIds", Joiner.on(',').join(libraryIds))
        .put("copies", Integer.toString(copies))
        .put("controls", Integer.toString(controls))
        .build();
    postData(driver, baseUrl + "qc/bulk/addFrom/Library", params);
    return new BulkQCPage(driver);
  }

  public static BulkQCPage getForEditPool(WebDriver driver, String baseUrl, Collection<Long> poolIds, int addControls) {
    Map<String, String> params = new MapBuilder<String, String>()
        .put("entityIds", Joiner.on(',').join(poolIds))
        .put("addControls", Integer.toString(addControls))
        .build();
    postData(driver, baseUrl + "qc/bulk/editFrom/Pool", params);
    return new BulkQCPage(driver);
  }

  public static BulkQCPage getForAddPool(WebDriver driver, String baseUrl, Collection<Long> poolIds, int copies,
      int controls) {
    Map<String, String> params = new MapBuilder<String, String>()
        .put("entityIds", Joiner.on(',').join(poolIds))
        .put("copies", Integer.toString(copies))
        .put("controls", Integer.toString(controls))
        .build();
    postData(driver, baseUrl + "qc/bulk/addFrom/Pool", params);
    return new BulkQCPage(driver);
  }
  public static BulkQCPage getForEditLibraryAliquot(WebDriver driver, String baseUrl, Collection<Long> aliquotIds, int addControls) {
        Map<String, String> params = new MapBuilder<String, String>()
                .put("entityIds", Joiner.on(',').join(aliquotIds))
                .put("addControls", Integer.toString(addControls))
                .build();
        postData(driver, baseUrl + "qc/bulk/editFrom/LibraryAliquot", params);
        return new BulkQCPage(driver);
  }

  public static BulkQCPage getForAddLibraryAliquot(WebDriver driver, String baseUrl, Collection<Long> aliquotIds, int copies,
                                           int controls) {
        Map<String, String> params = new MapBuilder<String, String>()
                .put("entityIds", Joiner.on(',').join(aliquotIds))
                .put("copies", Integer.toString(copies))
                .put("controls", Integer.toString(controls))
                .build();
        postData(driver, baseUrl + "qc/bulk/addFrom/LibraryAliquot", params);
        return new BulkQCPage(driver);
  }

  @Override
  public HandsOnTable getTable() {
    return table;
  }

}
