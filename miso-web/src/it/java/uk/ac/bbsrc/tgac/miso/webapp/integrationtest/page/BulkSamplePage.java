package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

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
    public static final String DONOR_SEX = "Donor Sex";
    public static final String SAMPLE_CLASS = "Sample Class";
    public static final String GROUP_ID = "Group ID";
    public static final String GROUP_DESCRIPTION = "Group Desc.";
    public static final String QC_STATUS = "QC Status";
    public static final String QC_NOTE = "QC Note";
  };

  private static final String CREATE_URL_FORMAT = "%smiso/sample/bulk/new?quantity=%d&projectId=%s&sampleClassId=%d";
  private static final String EDIT_URL_FORMAT = "%smiso/sample/bulk/edit?ids=%s";

  @FindBy(id = "hotContainer")
  private WebElement hotContainer;

  private final HandsOnTable table;

  public BulkSamplePage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Create Samples "), titleContains("Edit Samples ")));
    table = new HandsOnTable(hotContainer);
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

}
