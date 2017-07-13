package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;

public class HandsOnTable {

  private static final By columnHeadingsSelector = By.cssSelector("div.ht_master table.htCore span.colHeader");
  private static final By inputRowsSelector = By.cssSelector("div.ht_master table.htCore tbody tr");
  private static final By inputCellSelector = By.tagName("td");
  private static final By dropdownArrowSelector = By.className("htAutocompleteArrow");
  private static final By activeDropdownSelector = By.cssSelector("div.handsontableInputHolder[style*='block']");
  private static final By dropdownOptionRowsSelector = By.cssSelector("div.ht_master table.htCore > tbody > tr");

  private final WebElement hotContainer;
  private final List<String> columnHeadings;
  private final List<WebElement> inputRows;

  public HandsOnTable(WebElement hotContainer) {
    this.hotContainer = hotContainer;
    this.columnHeadings = hotContainer.findElements(columnHeadingsSelector).stream()
        .map(element -> element.getText().trim())
        .collect(Collectors.toList());
    this.inputRows = hotContainer.findElements(inputRowsSelector);
  }

  public List<String> getColumnHeadings() {
    return Lists.newArrayList(columnHeadings);
  }

  public int getRowCount() {
    return inputRows.size();
  }

  public String getText(String columnHeading, int rowNum) {
    WebElement cell = getCell(columnHeading, rowNum);
    return cleanOptionLabel(cell.getText());
  }

  public void enterText(String columnHeading, int rowNum, String text) {
    WebElement cell = getCell(columnHeading, rowNum);
    cell.click();
    cell.sendKeys(text);
    cell.sendKeys(Keys.ENTER);
  }

  public List<String> getDropdownOptions(String columnHeading, int rowNum) {
    WebElement cell = getCell(columnHeading, rowNum);
    cell.sendKeys(Keys.ESCAPE);
    WebElement dropdownArrow = cell.findElement(dropdownArrowSelector);
    dropdownArrow.click();
    WebElement dropdown = hotContainer.findElement(activeDropdownSelector);
    List<WebElement> optionRows = dropdown.findElements(dropdownOptionRowsSelector);
    List<String> singleColumnsOptions = optionRows.stream()
        .map(element -> cleanOptionLabel(element.findElement(By.tagName("td")).getText()))
        .collect(Collectors.toList());
    cell.sendKeys(Keys.ESCAPE);
    return singleColumnsOptions;
  }

  /**
   * Remove non-ASCII characters from a String. This is mainly to get rid of the dropdown arrow that becomes part of the text
   * 
   * @param option String to clean
   * @return cleaned String
   */
  private static String cleanOptionLabel(String option) {
    return CharMatcher.ascii().retainFrom(option)
        .trim();
  }

  private WebElement getCell(String columnHeading, int rowNum) {
    int colNum = columnHeadings.indexOf(columnHeading);
    if (colNum == -1) {
      throw new IllegalArgumentException("Column " + columnHeading + " doesn't exist");
    }
    WebElement row = inputRows.get(rowNum);
    List<WebElement> cells = row.findElements(inputCellSelector);
    return cells.get(colNum);
  }

}
