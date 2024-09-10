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

public class ListTabbedPage extends AbstractListPage {

  public static class Tabs {
    public static final String ALL = "All";
    public static final String ARRAY_SCANNER = "Array Scanner";
    public static final String CLUSTERING = "Clustering";
    public static final String DISTRIBUTION = "Distribution";
    public static final String DNA = "DNA";
    public static final String DRAFT = "Draft";
    public static final String EXTRACTION = "Extraction";
    public static final String FREEZERS = "Freezers";
    public static final String FULFILLED = "Fulfilled";
    public static final String ILLUMINA = "Illumina";
    public static final String INTERNAL = "Internal";
    public static final String LIBRARIES = "Libraries";
    public static final String LIBRARY = "Library";
    public static final String LS454 = "LS454";
    public static final String MINE = "Mine";
    public static final String MULTIPLEXING = "Multiplexing";
    public static final String OTHER = "Other";
    public static final String OUTSTANDING = "Outstanding";
    public static final String OXFORD_NANOPORE = "OxfordNanopore";
    public static final String PACBIO = "PacBio";
    public static final String PENDING = "Pending";
    public static final String RECEIPT = "Receipt";
    public static final String RNA = "RNA";
    public static final String ROOMS = "Rooms";
    public static final String RUN = "Run";
    public static final String SAMPLE = "Sample";
    public static final String SEQUENCER = "Sequencer";
    public static final String SEQUENCING = "Sequencing";
    public static final String SOLID = "Solid";
    public static final String STORAGE = "Storage";
    public static final String TISSUE = "Tissue";
  }

  @FindBy(className = "dataTables_wrapper")
  private List<WebElement> tableWrapperElements;
  @FindBy(className = "parsley-error")
  private WebElement errors;

  private final List<WebElement> tabs;

  private final List<DataTable> tables = new ArrayList<>();

  @FindBy(className = "ui-tabs-active")
  private WebElement selectedTab;
  private DataTable selectedTable;
  @FindBy(id = "dialog")
  private WebElement dialog;

  public ListTabbedPage(WebDriver driver) {
    super(driver, ListTabbedPage::new);
    PageFactory.initElements(driver, this);
    tabs = driver.findElements(By.xpath("//li[@role='tab']"));
    waitWithTimeout().until(ExpectedConditions.visibilityOf(selectedTab));
    tableWrapperElements.forEach(wrapper -> {
      tables.add(new DataTable(driver, wrapper.getAttribute("id")));
    });
    setSelectedTable(getTabNumber());
  }

  public static ListTabbedPage getTabbedListPage(WebDriver driver, String baseUrl, String listTarget) {
    String url = String.format("%s%s", baseUrl, listTarget);
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
    if (selectedTable == null)
      throw new IllegalArgumentException("Cannot find table with id " + tableId);
  }

  @Override
  public WebElement getErrors() {
    return errors;
  }

  public void clickTab(String tabHeading) {
    selectedTab = tabs.stream()
        .filter(tab -> tab.getText().trim().equals(tabHeading))
        .findAny().orElse(null);
    if (selectedTab == null)
      throw new IllegalArgumentException("Cannot find tab " + tabHeading);

    setSelectedTable(getTabNumber());
    selectedTab.click();
    waitWithTimeout().until(ExpectedConditions.invisibilityOf(getTable().getProcessing()));
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

  public String clickButtonAndGetUrl(String linkText, List<String> selections) {
    return clickLinkButtonAndGetUrl(linkText, selections, false);
  }
}
