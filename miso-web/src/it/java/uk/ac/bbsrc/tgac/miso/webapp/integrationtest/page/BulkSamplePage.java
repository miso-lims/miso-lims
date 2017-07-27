package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkSamplePage extends HeaderFooterPage {

  public static class Columns {
    public static final String NAME = "Sample Name";
    public static final String ALIAS = "Sample Alias";
    public static final String DESCRIPTION = "Description";
    public static final String RECEIVE_DATE = "Date of receipt";
    public static final String ID_BARCODE = "Matrix Barcode";
    public static final String SAMPLE_TYPE = "Sample Type";
    public static final String SCIENTIFIC_NAME = "Sci. Name";
    public static final String PROJECT = "Project";
    public static final String EXTERNAL_NAME = "External Name";
    public static final String IDENTITY_ALIAS = "Identity Alias";
    public static final String DONOR_SEX = "Donor Sex";
    public static final String SAMPLE_CLASS = "Sample Class";
    public static final String GROUP_ID = "Group ID";
    public static final String GROUP_DESCRIPTION = "Group Desc.";
    public static final String TISSUE_ORIGIN = "Tissue Origin";
    public static final String TISSUE_TYPE = "Tissue Type";
    public static final String PASSAGE_NUMBER = "Passage #";
    public static final String TIMES_RECEIVED = "Times Received";
    public static final String TUBE_NUMBER = "Tube Number";
    public static final String LAB = "Lab";
    public static final String EXT_INST_ID = "Ext. Inst. Identifier";
    public static final String TISSUE_MATERIAL = "Material";
    public static final String REGION = "Region";
    public static final String SLIDES = "Slides";
    public static final String DISCARDS = "Discards";
    public static final String THICKNESS = "Thickness";
    public static final String STAIN = "Stain";
    public static final String SLIDES_CONSUMED = "Slides Consumed";
    public static final String STR_STATUS = "STR Status";
    public static final String VOLUME = "Vol. (µl)";
    public static final String CONCENTRATION = "Conc. (ng/µl)";
    public static final String QC_STATUS = "QC Status";
    public static final String DNASE_TREATED = "DNAse";
    public static final String NEW_RIN = "New RIN";
    public static final String NEW_DV200 = "New DV200";
    public static final String QC_NOTE = "QC Note";
    public static final String PURPOSE = "Purpose";
  };

  private static final String CREATE_URL_FORMAT = "%smiso/sample/bulk/new?quantity=%d&projectId=%s&sampleClassId=%d";
  private static final String EDIT_URL_FORMAT = "%smiso/sample/bulk/edit?ids=%s";

  @FindBy(id = "hotContainer")
  private WebElement hotContainer;
  @FindBy(id = "save")
  private WebElement saveButton;
  @FindBy(id = "successMessages")
  private WebElement successMessages;
  @FindBy(id = "errors")
  private WebElement errors;
  @FindBy(id = "ajaxLoader")
  private WebElement ajaxSpinner;

  private final HandsOnTable table;

  public BulkSamplePage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Create Samples "), titleContains("Edit Samples ")));
    table = new HandsOnTable(driver, hotContainer);
  }

  public static BulkSamplePage getForCreate(WebDriver driver, String baseUrl, Integer quantity, Long projectId, Long sampleClassId) {
    String project = projectId == null ? "" : projectId.toString();
    String url = String.format(CREATE_URL_FORMAT, baseUrl, quantity, project, sampleClassId);
    driver.get(url);
    return new BulkSamplePage(driver);
  }

  public static BulkSamplePage getForEdit(WebDriver driver, String baseUrl, List<Long> sampleIds) {
    String ids = makeCommaSeparatedString(sampleIds);
    String url = String.format(EDIT_URL_FORMAT, baseUrl, ids);
    driver.get(url);
    return new BulkSamplePage(driver);
  }

  private static String makeCommaSeparatedString(Collection<Long> longs) {
    StringBuilder sb = new StringBuilder();
    for (Long l : longs) {
      sb.append(l).append(",");
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  public HandsOnTable getTable() {
    return table;
  }

  public void clickSaveButton() {
    saveButton.click();
    // give time for spinner to appear, then wait for it to be hidden again
    waitExplicitly(1000);
    waitWithTimeout().until(invisibilityOf(ajaxSpinner));
  }

  public String getSuccessMessages() {
    return successMessages.getText();
  }

  public String getErrorMessages() {
    return errors.getText();
  }

  public boolean areErrorsHidden() {
    return errors.getAttribute("class").contains("hidden");
  }

  public void waitForIdentityLookup() {
    waitExplicitly(3000);
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
