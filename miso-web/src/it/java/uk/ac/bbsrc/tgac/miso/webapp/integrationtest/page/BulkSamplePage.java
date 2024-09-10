package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import org.openqa.selenium.WebDriver;

import com.google.common.base.Joiner;

import uk.ac.bbsrc.tgac.miso.core.util.MapBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkSamplePage extends BulkPage {

  public static class SamColumns {
    public static final String NAME = "Name";
    public static final String ALIAS = "Alias";
    public static final String DESCRIPTION = "Description";
    public static final String RECEIVE_DATE = "Date of Receipt";
    public static final String RECEIVE_TIME = "Time of Receipt";
    public static final String RECEIVED_FROM = "Received From";
    public static final String RECEIVED_BY = "Received By";
    public static final String RECEIPT_CONFIRMED = "Receipt Confirmed";
    public static final String RECEIPT_QC_PASSED = "Receipt QC Passed";
    public static final String RECEIPT_QC_NOTE = "Receipt QC Note";
    public static final String REQUISITION_ALIAS = "Requisition Alias";
    public static final String REQUISITION = "Requisition";
    public static final String REQUISITION_ASSAY = "Assay";
    public static final String ID_BARCODE = "Matrix Barcode";
    public static final String BOX_SEARCH = "Box Search";
    public static final String BOX_ALIAS = "Box Alias";
    public static final String BOX_POSITION = "Position";
    public static final String DISCARDED = "Discarded";
    public static final String SAMPLE_TYPE = "Sample Type";
    public static final String SCIENTIFIC_NAME = "Sci. Name";
    public static final String PROJECT = "Project";
    public static final String EXTERNAL_NAME = "External Name";
    public static final String IDENTITY_ALIAS = "Identity Alias";
    public static final String DONOR_SEX = "Donor Sex";
    public static final String CONSENT = "Consent";
    public static final String SUBPROJECT = "Subproject";
    public static final String SAMPLE_CLASS = "Sample Class";
    public static final String EFFECTIVE_GROUP_ID = "Effective Group ID";
    public static final String GROUP_ID = "Group ID";
    public static final String GROUP_DESCRIPTION = "Group Desc.";
    public static final String CREATION_DATE = "Creation Date";
    public static final String TISSUE_ORIGIN = "Tissue Origin";
    public static final String TISSUE_TYPE = "Tissue Type";
    public static final String PASSAGE_NUMBER = "Passage #";
    public static final String TIMES_RECEIVED = "Times Received";
    public static final String TUBE_NUMBER = "Tube Number";
    public static final String LAB = "Lab";
    public static final String SECONDARY_ID = "Secondary ID";
    public static final String TISSUE_MATERIAL = "Material";
    public static final String REGION = "Region";
    public static final String TIMEPOINT = "Timepoint";
    public static final String INITIAL_SLIDES = "Initial Slides";
    public static final String SLIDES = "Slides";
    public static final String THICKNESS = "Thickness";
    public static final String STAIN = "Stain";
    public static final String PERCENT_TUMOUR = "% Tumour";
    public static final String PERCENT_NECROSIS = "% Necrosis";
    public static final String MARKED_AREA = "Marked Area (mm²)";
    public static final String MARKED_AREA_PERCENT_TUMOUR = "Marked Area % Tumour";
    public static final String REFERENCE_SLIDE = "Reference Slide";
    public static final String PIECE_TYPE = "Piece Type";
    public static final String SLIDES_CONSUMED = "Slides Consumed";
    public static final String STR_STATUS = "STR Status";
    public static final String INITIAL_VOLUME = "Initial Volume";
    public static final String VOLUME = "Volume";
    public static final String VOLUME_UNITS = "Vol. Units";
    public static final String PARENT_NG_USED = "Parent ng Used";
    public static final String PARENT_VOLUME_USED = "Parent Vol. Used";
    public static final String CONCENTRATION = "Conc.";
    public static final String CONCENTRATION_UNITS = "Conc. Units";
    public static final String DNASE_TREATED = "DNAse";
    public static final String QC_STATUS = "QC Status";
    public static final String QC_NOTE = "QC Note";
    public static final String PURPOSE = "Purpose";
    public static final String PARENT_ALIAS = "Parent Alias";
    public static final String PARENT_LOCATION = "Parent Location";
    public static final String PARENT_NAME = "Parent Name";
    public static final String PARENT_SAMPLE_CLASS = "Parent Sample Class";
    public static final String INITIAL_CELL_CONC = "Initial Cell Conc.";
    public static final String DIGESTION = "Digestion";
    public static final String TARGET_CELL_RECOVERY = "Target Cell Recovery";
    public static final String CELL_VIABILITY = "Cell Viability";
    public static final String LOADING_CELL_CONC = "Loading Cell Conc.";
    public static final String INPUT_INTO_LIBRARY = "Input into Library";
    public static final String SOP = "SOP";

    private SamColumns() {
      throw new IllegalStateException("Util class not intended for instantiation");
    }
  };

  private HandsOnTable table;

  public BulkSamplePage(WebDriver driver) {
    super(driver);
    waitWithTimeout().until(or(titleContains("Create Samples "), titleContains("Edit Samples ")));
    refreshElements();
  }

  @Override
  protected void refreshElements() {
    table = new HandsOnTable(getDriver());
  }

  public static BulkSamplePage getForCreate(WebDriver driver, String baseUrl, Integer quantity, Long projectId,
      String sampleCategory) {
    String url = baseUrl + "sample/bulk/new";
    MapBuilder<String, String> params = new MapBuilder<String, String>()
        .put("quantity", quantity.toString());
    if (projectId != null) {
      params.put("projectId", projectId.toString());
    }
    if (sampleCategory != null) {
      params.put("targetCategory", sampleCategory);
    }
    postData(driver, url, params.build());
    return new BulkSamplePage(driver);
  }

  public static BulkSamplePage getForEdit(WebDriver driver, String baseUrl, Collection<Long> sampleIds) {
    String ids = Joiner.on(',').join(sampleIds);
    String url = baseUrl + "sample/bulk/edit";
    postData(driver, url, new MapBuilder<String, String>().put("ids", ids).build());
    return new BulkSamplePage(driver);
  }

  public static BulkSamplePage getForPropagate(WebDriver driver, String baseUrl, List<Long> parentIds,
      List<Integer> replicates,
      String sampleCategory) {
    String ids = Joiner.on(',').join(parentIds);
    String replicatesString = Joiner.on(',').join(replicates);
    String url = baseUrl + "sample/bulk/propagate";
    MapBuilder<String, String> params = new MapBuilder<String, String>()
        .put("parentIds", ids)
        .put("replicates", replicatesString)
        .put("targetCategory", sampleCategory);
    postData(driver, url, params.build());
    return new BulkSamplePage(driver);
  }

  @Override
  public HandsOnTable getTable() {
    return table;
  }

  public String getSensibleDate(String date) {
    SimpleDateFormat fromUI = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sensible = new SimpleDateFormat("yyyy-MM-dd");
    try {
      return sensible.format(fromUI.parse(date));
    } catch (ParseException e) {
      throw new IllegalArgumentException("Very bad date format", e);
    }
  }
}
