package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class ListTabbedPage extends HeaderFooterPage {

  public static class TabbedColumns {
    public static final String SORT = "";
    public static final String NAME = "Name";
    public static final String ALIAS = "Alias";
    public static final String LOCATION = "Location";
    public static final String LAST_MODIFIED = "Last Modified";
    public static final String PLATFORM = "Platform";
    public static final String DESCRIPTION = "Description";
    public static final String DATE_CREATED = "Date Created";
    public static final String POOL_CONCENTRATION = "Conc. (nM)";
    public static final String LONGEST_INDEX = "Longest Index";
    public static final String SEQUENCING_PARAMETERS = "Sequencing Parameters";
    public static final String SERIAL_NUMBER = "Serial Number";
    public static final String LAST_RUN_NAME = "Last Run Name";
    public static final String LAST_RUN_ALIAS = "Last Run Alias";
    public static final String LAST_SEQUENCER = "Last Sequencer Used";
    public static final String STATUS = "Status";
    public static final String START_DATE = "Start Date";
    public static final String END_DATE = "End Date";
    public static final String ITEMS_CAPACITY = "Items/Capacity";
    public static final String SIZE = "Size";
    public static final String SEQUENCER_NAME = "Sequencer Name";
    public static final String MODEL = "Model";
    public static final String LAST_SERVICED = "Last Serviced";
    public static final String VERSION = "Version";
    public static final String MANUFACTURER = "Manufacturer";
    public static final String PART_NUMBER = "Part Number";
    public static final String STOCK_LEVEL = "Stock Level";
    public static final String FAMILY = "Family";
    public static final String SEQUENCE = "Sequence";
  }

  @FindBy(className = "dataTable")
  private List<WebElement> tablesElements;
  @FindBy(className = "ui-tabs-anchor")
  private List<WebElement> tabs;
  @FindBy(className = "parsley-error")
  private WebElement errors;
  @FindBy(id = "list1_processing")
  private WebElement processing1;

  private final List<DataTable> tables = new ArrayList<>();

  @FindBy(className = "ui-tabs-active")
  private WebElement selectedTab;
  private DataTable selectedTable;

  public ListTabbedPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(ExpectedConditions.visibilityOf(selectedTab)); // TODO: make this better
    tablesElements.forEach(table -> {
      tables.add(new DataTable(table, table.getAttribute("id")));
    });
    setSelectedTable();
  }

  public static ListTabbedPage getTabbedListPage(WebDriver driver, String baseUrl, String listTarget) {
    String url = String.format("%smiso/%s", baseUrl, listTarget);
    driver.get(url);
    return new ListTabbedPage(driver);
  }

  public List<DataTable> getTables() {
    return tables;
  }

  public DataTable getSelectedTable() {
    return selectedTable;
  }

  private void setSelectedTable() {
    String tabId = selectedTab.findElement(By.tagName("a")).getAttribute("id");
    String tabNumber = tabId.substring(tabId.lastIndexOf("-") + 1);
    String tableId = "list" + tabNumber;
    selectedTable = tables.stream()
        .filter(table -> table.getId().equals(tableId)).findAny().orElse(null);
    if (selectedTable == null) throw new IllegalArgumentException("Cannot find table with id " + tableId);
  }

  public WebElement getErrors() {
    return errors;
  }

  public void sortByColumn(String columnHeading) {
    selectedTable.clickToSort(columnHeading);
    waitWithTimeout().until(ExpectedConditions.invisibilityOf(processing1));
  }

  public void clickTab(String tabHeading) {
    selectedTab = tabs.stream()
        .filter(tab -> tab.getText().equals(tabHeading))
        .findAny().orElse(null);
    if (selectedTab == null) throw new IllegalArgumentException("Cannot find tab " + tabHeading);
    selectedTab.click();
    setSelectedTable();
  }

  public List<String> getTabHeadings() {
    List<WebElement> tabs = getDriver().findElements(By.className("ui-tabs-anchor"));
    if (tabs.size() > 0) {
      return tabs.stream()
          .map(tab -> tab.getText().trim())
          .collect(Collectors.toList());
    } else {
      throw new IllegalArgumentException("This page is not tabbed");
    }
  }

}
