package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class ListTabbedPage extends HeaderFooterPage implements AbstractListPage {

  public static class Tabs {
    public static final String ILLUMINA = "Illumina";
    public static final String LS454 = "LS454";
    public static final String PACBIO = "PacBio";
    public static final String SOLID = "Solid";
    public static final String OXFORD_NANOPORE = "OxfordNanopore";
    public static final String ACTIVE = "Active";
    public static final String ALL = "All";
    public static final String DNA = "DNA";
    public static final String RNA = "RNA";
    public static final String LIBRARIES = "Libraries";
    public static final String STORAGE = "Storage";
    public static final String TISSUE = "Tissue";
    public static final String CLUSTERING = "Clustering";
    public static final String EXTRACTION = "Extraction";
    public static final String LIBRARY = "Library";
    public static final String MULTIPLEXING = "Multiplexing";
    public static final String SEQUENCING = "Sequencing";
  }

  @FindBy(className = "dataTable")
  private List<WebElement> tablesElements;
  @FindBy(className = "parsley-error")
  private WebElement errors;
  @FindBy(className = "dataTables_processing")
  private List<WebElement> processingList;
  
  private final List<WebElement> tabs;

  private final List<DataTable> tables = new ArrayList<>();

  @FindBy(className = "ui-tabs-active")
  private WebElement selectedTab;
  private DataTable selectedTable;
  private WebElement selectedProcessing;

  public ListTabbedPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    tabs = driver.findElements(By.xpath("//li[@role='tab']"));
    waitWithTimeout().until(ExpectedConditions.visibilityOf(selectedTab));
    tablesElements.forEach(table -> {
      tables.add(new DataTable(table));
    });
    setSelectedTable(getTabNumber());
  }

  public static ListTabbedPage getTabbedListPage(WebDriver driver, String baseUrl, String listTarget) {
    String url = String.format("%smiso/%s", baseUrl, listTarget);
    driver.get(url);
    return new ListTabbedPage(driver);
  }

  public List<DataTable> getTables() {
    return tables;
  }

  @Override
  public DataTable getTable() {
    return selectedTable;
  }

  private void setSelectedTable(String tabNumber) {
    String tableId = "list" + tabNumber;
    selectedTable = tables.stream()
        .filter(table -> tableId.equals(table.getId())).findAny().orElse(null);
    if (selectedTable == null) throw new IllegalArgumentException("Cannot find table with id " + tableId);
    setSelectedProcessing(tabNumber);
  }

  @Override
  public WebElement getErrors() {
    return errors;
  }

  @Override
  public void sortByColumn(String columnHeading) {
    selectedTable.clickToSort(columnHeading);
    waitWithTimeout().until(ExpectedConditions.invisibilityOf(selectedProcessing));
  }

  public void clickTab(String tabHeading) {
    selectedTab = tabs.stream()
        .filter(tab -> tab.getText().trim().equals(tabHeading))
        .findAny().orElse(null);
    if (selectedTab == null) throw new IllegalArgumentException("Cannot find tab " + tabHeading);

    setSelectedTable(getTabNumber());
    selectedTab.click();
    waitWithTimeout().until(ExpectedConditions.invisibilityOf(selectedProcessing));
  }

  private void setSelectedProcessing(String tabNumber) {
    String selectedProcessingId = "list" + getTabNumber() + "_processing";
    
    selectedProcessing = processingList.stream()
        .filter(proc -> selectedProcessingId.equals(proc.getAttribute("id")))
        .findAny().orElse(null);
    if (selectedProcessing == null) throw new IllegalArgumentException("Cannot find processing with ID " + selectedProcessingId);
  }

  public Set<String> getTabHeadings() {
    if (tabs.size() > 0) {
      return tabs.stream()
          .map(tab -> tab.getText().trim())
          .collect(Collectors.toSet());
    } else {
      throw new IllegalArgumentException("This page is not tabbed");
    }
  }

  private String getTabNumber() {
    String tabId = selectedTab.findElement(By.tagName("a")).getAttribute("id");
    return tabId.substring(tabId.lastIndexOf("-") + 1);
  }
}
