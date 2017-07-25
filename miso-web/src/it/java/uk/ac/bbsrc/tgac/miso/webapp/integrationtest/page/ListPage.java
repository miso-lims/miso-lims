package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class ListPage extends HeaderFooterPage {

  public static class Columns {
    public static final String SORT = "";
    public static final String NAME = "Name";
    public static final String ALIAS = "Alias";
    public static final String SAMPLE_CLASS = "Sample Class";
    public static final String SAMPLE_TYPE = "Type";
    public static final String QC_PASSED = "QC Passed";
    public static final String LOCATION = "Location";
    public static final String LAST_MODIFIED = "Last Modified";
    public static final String SAMPLE_NAME = "Sample Name";
    public static final String SAMPLE_ALIAS = "Sample Alias";
    public static final String INDICES = "Index(es)";
    public static final String LIBRARY_NAME = "Library Name";
    public static final String LIBRARY_ALIAS = "Library Alias";
    public static final String CREATOR = "Creator";
    public static final String CREATION_DATE = "Creation Date";
    public static final String PLATFORM = "Platform";
    public static final String DIL_CONCENTRATION = "Concentration";
    public static final String DESCRIPTION = "Description";
    public static final String TYPE = "Type";
    public static final String PRINTER = "Printer";
    public static final String DRIVER = "Driver";
    public static final String BACKEND = "Backend";
    public static final String AVAILABLE = "Available";
  }

  @FindBy(id = "listingTable")
  private WebElement listingTable;
  @FindBy(className = "parsley-error")
  private WebElement errors;
  @FindBy(id = "listingTable_processing")
  private WebElement processing;

  private final DataTable table;

  public ListPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(ExpectedConditions.visibilityOf(listingTable)); // TODO: make this better
    table = new DataTable(listingTable, "listingTable");
  }

  public static ListPage getListPage(WebDriver driver, String baseUrl, String listTarget) {
    String url = String.format("%smiso/%s", baseUrl, listTarget);
    driver.get(url);
    return new ListPage(driver);
  }

  public DataTable getTable() {
    return table;
  }

  public WebElement getErrors() {
    return errors;
  }

  public void sortByColumn(String columnHeading) {
    table.clickToSort(columnHeading);
    waitWithTimeout().until(ExpectedConditions.invisibilityOf(processing));
  }

}
