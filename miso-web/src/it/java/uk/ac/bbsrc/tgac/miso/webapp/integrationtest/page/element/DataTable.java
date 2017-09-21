package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.Columns;

public class DataTable {

  private static final By columnHeadingsSelector = By.tagName("th");
  private static final By rowSelector = By.cssSelector("tbody > tr");
  private static final By cellSelector = By.tagName("td");
  private static final By emptyTableSelector = By.className("dataTables_empty");
  private static final By sortableColumnSelector = By.xpath(".//div/span[contains(@class, 'ui-icon-carat')]");
  private static final By selectedSortableColumnSelector = By.xpath(".//div/span[contains(@class, 'ui-icon-triangle')]");

  private final WebElement listTable;
  private final List<WebElement> columnHeaders;
  private final List<String> columnHeadings;

  public DataTable(WebElement listTable) {
    this.listTable = listTable;
    this.columnHeaders = listTable.findElements(columnHeadingsSelector).stream()
        .collect(Collectors.toList());
    this.columnHeadings = columnHeaders.stream()
        .map(element -> element.getText().trim())
        .collect(Collectors.toList());
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

  public String getId() {
    return listTable.getAttribute("id");
  }

  public int countRows() {
    return listTable.findElements(rowSelector).size();
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
    List<WebElement> rows = listTable.findElements(rowSelector);
    if (rowNum >= rows.size()) {
      throw new IllegalArgumentException("Requested row " + rowNum + " which is larger than the available " + rows.size());
    }
    WebElement row = rows.get(rowNum);
    List<WebElement> cells = row.findElements(cellSelector);
    return cells.get(colNum);
  }

  public boolean isTableEmpty() {
    return listTable.findElements(emptyTableSelector).size() == 1;
  }
}
