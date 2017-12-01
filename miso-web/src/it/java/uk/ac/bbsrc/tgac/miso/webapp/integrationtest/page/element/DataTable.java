package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.Columns;

public class DataTable extends AbstractElement {

  private static final By tableSelector = By.tagName("table");
  private static final By searchBarSelector = By.xpath(".//div[@class='dataTables_filter']/descendant::label/descendant::input");
  private static final By searchBarDivSelector = By.xpath(".//div[@class='dataTables_filter']");
  private static final By columnHeadingsSelector = By.tagName("th");
  private static final By rowSelector = By.cssSelector("tbody > tr");
  private static final By cellSelector = By.tagName("td");
  private static final By emptyTableSelector = By.className("dataTables_empty");
  private static final By sortableColumnSelector = By.xpath(".//div/span[contains(@class, 'ui-icon-carat')]");
  private static final By selectedSortableColumnSelector = By.xpath(".//div/span[contains(@class, 'ui-icon-triangle')]");
  private static final By processingSelector = By.className("dataTables_processing");

  private final WebElement table;
  private final List<WebElement> columnHeaders;
  private final List<String> columnHeadings;
  private final WebElement searchBar;
  private final WebElement searchBarDiv;
  private final WebElement processing;

  public DataTable(WebDriver driver, String tableWrapperId) {
    super(driver);
    PageFactory.initElements(driver, this);
    WebElement tableWrapper = getDriver().findElement(By.id(tableWrapperId));
    this.table = tableWrapper.findElement(tableSelector);
    this.columnHeaders = tableWrapper.findElements(columnHeadingsSelector).stream()
        .collect(Collectors.toList());
    this.columnHeadings = columnHeaders.stream()
        .map(element -> element.getText().trim())
        .collect(Collectors.toList());
    this.searchBar = tableWrapper.findElement(searchBarSelector);
    this.searchBarDiv = tableWrapper.findElement(searchBarDivSelector);
    this.processing = tableWrapper.findElement(processingSelector);
  }

  public List<WebElement> getColumnHeaders() {
    return Lists.newArrayList(columnHeaders);
  }

  public List<String> getColumnHeadings() {
    return Lists.newArrayList(columnHeadings);
  }

  public List<String> getSortableColumnHeadings() {
    List<WebElement> sortableColumns = columnHeaders.stream()
        .filter(th -> th.findElements(sortableColumnSelector).size() != 0)
        .collect(Collectors.toList());
    WebElement selectedSorted = columnHeaders.stream()
        .filter(th -> th.findElements(selectedSortableColumnSelector).size() != 0)
        .findAny().orElse(null);
    if (selectedSorted != null) sortableColumns.add(selectedSorted);
    return sortableColumns.stream()
        .map(col -> col.getText().trim())
        .collect(Collectors.toList());
  }

  public void sortByColumn(String columnHeading) {
    clickToSort(columnHeading);
    waitWithTimeout().until(invisibilityOf(processing));
  }

  public String getId() {
    return table.getAttribute("id");
  }

  public WebElement getProcessing() {
    return processing;
  }

  /**
   * Returns '0' for a table with no data
   * 
   * @return
   */
  public int countRows() {
    List<WebElement> allRows = table.findElements(rowSelector);
    if (allRows.size() == 1) {
      // empty table has one tr containing one td with text "No data available in table"
      if (allRows.get(0).findElements(By.tagName("td")).get(0).getText().equals("No data available in table")) {
        return 0;
      }
    }
    return table.findElements(rowSelector).size();
  }

  public void clickToSort(String columnHeading) {
    WebElement sortTarget = getHeader(columnHeading);
    sortTarget.click();
  }

  private WebElement getHeader(String columnHeading) {
    int colNum = columnHeadings.indexOf(columnHeading);
    if (colNum == -1) {
      throw new IllegalArgumentException("Column " + columnHeading + " doesn't exist");
    }
    return columnHeaders.get(colNum);
  }

  public String getTextAtCell(String columnHeading, int rowNum) {
    WebElement cell = getCell(columnHeading, rowNum);
    return cell.getText();
  }

  public void checkBoxForRow(int rowNum) {
    List<WebElement> checkbox = getCell(Columns.SORT, rowNum).findElements(By.tagName("input"));
    if (checkbox.isEmpty()) {
      throw new IllegalArgumentException("Row " + rowNum + " does not have a checkbox to click.");
    }
    checkbox.get(0).click();
  }

  private WebElement getCell(String columnHeading, int rowNum) {
    int colNum = columnHeadings.indexOf(columnHeading);
    if (colNum == -1) {
      throw new IllegalArgumentException("Column " + columnHeading + " doesn't exist");
    }
    List<WebElement> rows = table.findElements(rowSelector);
    if (rowNum >= rows.size()) {
      throw new IllegalArgumentException("Requested row " + rowNum + " which is larger than the available " + rows.size());
    }
    WebElement row = rows.get(rowNum);
    List<WebElement> cells = row.findElements(cellSelector);
    return cells.get(colNum);
  }

  public boolean isTableEmpty() {
    return table.findElements(emptyTableSelector).size() == 1;
  }

  public List<String> getColumnValues(String columnHeading) {
    int colNum = columnHeadings.indexOf(columnHeading);
    if (table.findElements(rowSelector).isEmpty()) {
      return new ArrayList<>();
    }
    return table.findElements(rowSelector).stream()
        .map(row -> {
          if (row == null) return null;
          List<WebElement> cells = row.findElements(cellSelector);
          if (cells.size() < 2) return null; // empty table has one cell saying "No data available in table"
          return cells.get(colNum).getText();
        })
        .collect(Collectors.toList());
  }

  public boolean doesColumnContain(String columnHeading, String target) {
    return getColumnValues(columnHeading).contains(target);
  }

  public void searchFor(String searchTerm) {
    waitUntil(elementToBeClickable(searchBar));
    searchBar.clear();
    searchBar.sendKeys(searchTerm);
    searchBar.sendKeys(Keys.ENTER);
    waitUntil(elementToBeClickable(searchBar));
  }

  public String getSearchDivId() {
    return searchBarDiv.getAttribute("id");
  }

}
