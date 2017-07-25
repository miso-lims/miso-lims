package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.common.collect.Lists;

public class DataTable {

  private static final By columnHeadingsSelector = By.tagName("th");

  private final WebElement listTable;
  private final String id;
  private final List<WebElement> columnHeaders;
  private final List<String> columnHeadings;

  public DataTable(WebElement listTable, String tableId) {
    this.listTable = listTable;
    this.columnHeaders = listTable.findElements(columnHeadingsSelector).stream()
        .collect(Collectors.toList());
    this.columnHeadings = columnHeaders.stream()
        .map(element -> element.getText().trim())
        .collect(Collectors.toList());
    this.id = tableId;
  }

  public List<WebElement> getColumnHeaders() {
    return Lists.newArrayList(columnHeaders);
  }

  public List<String> getColumnHeadings() {
    return Lists.newArrayList(columnHeadings);
  }

  public String getId() {
    return id;
  }

  public void clickToSort(String columnHeading) {
    WebElement sortTarget = getHeader(columnHeading);
    sortTarget.click();
  }

  private WebElement getHeader(String columnHeading) {
    int colNum = columnHeading.indexOf(columnHeading);
    if (colNum == -1) {
      throw new IllegalArgumentException("Column " + columnHeading + " doesn't exist");
    }
    return columnHeaders.get(colNum);
  }

}
