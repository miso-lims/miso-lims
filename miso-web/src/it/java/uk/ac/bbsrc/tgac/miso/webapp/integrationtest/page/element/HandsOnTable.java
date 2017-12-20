package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.MoreExpectedConditions.textDoesNotContain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;

public class HandsOnTable extends AbstractElement {

  private static final By columnHeadingsSelector = By.cssSelector("div.ht_master table.htCore span.colHeader");
  private static final By inputRowsSelector = By.cssSelector("div.ht_master table.htCore tbody tr");
  private static final By lockedRowsSelector = By.cssSelector("div.ht_clone_left table.htCore tbody tr");
  private static final By inputCellSelector = By.tagName("td");
  private static final By dropdownArrowSelector = By.className("htAutocompleteArrow");
  private static final By activeDropdownSelector = By.cssSelector("div.handsontableInputHolder[style*='block']");
  private static final By activeCellEditorSelector = By.cssSelector("div.handsontableInputHolder[style*='block'] > textarea");
  private static final By dropdownOptionRowsSelector = By.cssSelector("div.ht_master table.htCore > tbody > tr");
  private final List<String> columnHeadings;
  private final List<WebElement> inputRows;
  private final List<WebElement> lockedRows;


  @FindBy(id = "hotContainer")
  private WebElement hotContainer;

  @FindBy(id = "save")
  private WebElement saveButton;

  @FindBy(id = "ajaxLoader")
  private WebElement ajaxLoader;

  public HandsOnTable(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    this.columnHeadings = hotContainer.findElements(columnHeadingsSelector).stream()
        .map(element -> element.getText().trim())
        .collect(Collectors.toList());
    this.inputRows = hotContainer.findElements(inputRowsSelector);
    this.lockedRows = hotContainer.findElements(lockedRowsSelector);
  }

  public List<String> getColumnHeadings() {
    return Lists.newArrayList(columnHeadings);
  }

  public int getRowCount() {
    return inputRows.size();
  }

  public String getText(String columnHeading, int rowNum) {
    WebElement cell = getCell(columnHeading, rowNum);
    return cleanAscii(cell.getText());
  }

  /**
   * Enters text into a writable cell. Will fail if the cell is read-only. See {@link #isWritable(String, int)}
   * 
   * @param columnHeading
   * @param rowNum data row number. 0 is the first data row
   * @param text text to enter. Only ASCII characters are accepted
   * @throws IllegalArgumentException if text contains non-ASCII characters
   */
  public void enterText(String columnHeading, int rowNum, String text) {
    if (LimsUtils.isStringEmptyOrNull(text)) {
      clearField(columnHeading, rowNum);
      return;
    }
    if (!text.equals(cleanAscii(text))) {
      throw new IllegalArgumentException("text contains non-ASCII characters");
    }
    WebElement cell = getCell(columnHeading, rowNum);
    cancelEditing();
    if (!cell.getAttribute("class").contains("current")) {
      cell.click();
      waitUntil((driver) -> getCell(columnHeading, rowNum).getAttribute("class").contains("current"));
    }
    WebElement cellEditor = findElementIfExists(activeCellEditorSelector);
    if (cellEditor == null) {
      new Actions(getDriver()).sendKeys(Keys.ENTER).build().perform();
      waitUntil(presenceOfElementLocated(activeCellEditorSelector));
      cellEditor = getDriver().findElement(activeCellEditorSelector);
    }
    cellEditor.clear();
    waitUntil(textToBe(activeCellEditorSelector, ""));
    cellEditor.sendKeys(text);
    waitUntil(attributeToBe(activeCellEditorSelector, "value", text));
    cellEditor.sendKeys(Keys.ENTER);
    waitUntil(invisibilityOf(cellEditor));
  }

  public void clearField(String columnHeading, int rowNum) {
    WebElement cell = getCell(columnHeading, rowNum);
    new Actions(getDriver())
        .click(cell)
        .sendKeys(Keys.DELETE)
        .sendKeys(Keys.ESCAPE)
        .build().perform();
  }

  /**
   * Check if a cell is writable
   * 
   * @param columnHeading
   * @param rowNum
   * @return true if the cell is writable, false if it is read-only
   */
  public boolean isWritable(String columnHeading, int rowNum) {
    WebElement cell = getCell(columnHeading, rowNum);
    cancelEditing();
    if (!cell.getAttribute("class").contains("current")) {
      cell.click();
    }
    new Actions(getDriver()).sendKeys(Keys.ENTER).build().perform();
    WebElement cellEditor = findElementIfExists(activeCellEditorSelector);
    if (cell.getAttribute("class").contains("htDimmed")) {
      if (cellEditor == null) {
        return false;
      } else {
        throw new IllegalStateException("Cell is dimmed, but an editor was found");
      }
    } else {
      if (cellEditor == null) {
        throw new IllegalStateException("Cell isn't dimmed, but no editor was found");
      } else {
        return true;
      }
    }
  }

  public List<String> getDropdownOptions(String columnHeading, int rowNum) {
    WebElement cell = getCell(columnHeading, rowNum);
    cancelEditing();
    WebElement dropdownArrow = cell.findElement(dropdownArrowSelector);
    dropdownArrow.click();
    WebElement dropdown = hotContainer.findElement(activeDropdownSelector);

    // HOT may not render all options if there are too many; scroll through and collect all values
    // (options near top may be removed as you scroll down as well)
    Action down = new Actions(getDriver()).sendKeys(Keys.DOWN).build();
    List<WebElement> optionRows = Collections.emptyList();
    List<WebElement> newOptionRows = dropdown.findElements(dropdownOptionRowsSelector);
    List<String> options = Lists.newArrayList();
    do {
      optionRows = newOptionRows;
      boolean newValues = options.isEmpty();
      for (int i = 0; i < newOptionRows.size(); i++) {
        String option = getOptionLabelFromMenuRow(newOptionRows.get(i));
        if (newValues) {
          if (options.contains(option)) {
            throw new IllegalStateException("Unexpected duplicate values in dropdown");
          }
          options.add(option);
        } else {
          // Assumes that duplicates are not allowed (asserted by exception above)
          if (options.get(options.size() - 1).equals(option)) {
            newValues = true;
          }
        }
        down.perform();
      }
      newOptionRows = dropdown.findElements(dropdownOptionRowsSelector);
    } while (haveDifferentOptions(optionRows, newOptionRows));

    cancelEditing();
    return options;
  }

  private boolean haveDifferentOptions(List<WebElement> tracked, List<WebElement> updated) {
    if (updated.size() > tracked.size()) {
      return true;
    }
    if (updated.isEmpty()) {
      return false;
    }
    String finalTrackedOption = getOptionLabelFromMenuRow(tracked.get(tracked.size() - 1));
    String finalUpdatedOption = getOptionLabelFromMenuRow(updated.get(updated.size() - 1));
    return !finalUpdatedOption.equals(finalTrackedOption);
  }

  private String getOptionLabelFromMenuRow(WebElement menuRow) {
    WebElement menuCell = menuRow.findElement(By.tagName("td"));
    return cleanAscii(menuCell.getText());
  }

  private void cancelEditing() {
    new Actions(getDriver())
        .sendKeys(Keys.ESCAPE)
        .build().perform();
  }

  public HandsOnTableSaveResult save() {
    saveButton.click();
    waitUntil(invisibilityOf(ajaxLoader));
    return new HandsOnTableSaveResult(getDriver(), this);
  }

  /**
   * Remove non-ASCII characters from a String. This is mainly to get rid of the dropdown arrow that becomes part of the text
   * 
   * @param option String to clean
   * @return cleaned String
   */
  private static String cleanAscii(String option) {
    return CharMatcher.ascii().retainFrom(option)
        .trim();
  }

  protected WebElement getCell(String columnHeading, int rowNum) {
    int colNum = columnHeadings.indexOf(columnHeading);
    if (colNum == -1) {
      throw new IllegalArgumentException("Column " + columnHeading + " doesn't exist");
    }
    if (!lockedRows.isEmpty()) {
      WebElement lockedRow = lockedRows.get(rowNum);
      List<WebElement> lockedCells = lockedRow.findElements(inputCellSelector);
      if (lockedCells.size() > colNum) {
        return lockedCells.get(colNum);
      }
    }
    WebElement row = inputRows.get(rowNum);
    List<WebElement> cells = row.findElements(inputCellSelector);
    return cells.get(colNum);
  }

  public Map<Integer, Set<String>> getInvalidCellsByRow() {
    Map<Integer, Set<String>> all = Maps.newHashMap();
    for (int row = 0; row < getRowCount(); row++) {
      Set<String> invalid = getInvalidCells(row);
      if (!invalid.isEmpty()) {
        all.put(row, invalid);
      }
    }
    return all;
  }

  public Set<String> getInvalidCells(int rowNum) {
    Set<String> invalid = Sets.newHashSet();
    for (int i = 0; i < columnHeadings.size(); i++) {
      String column = columnHeadings.get(i);
      WebElement cell = getCell(column, rowNum);
      if (cell.getAttribute("class").contains("htInvalid")) {
        invalid.add(column);
      }
    }
    return invalid;
  }

  public void waitForSearch(String resultColumnHeading, int rowNum) {
    WebElement resultField = getCell(resultColumnHeading, rowNum);
    waitUntil(textDoesNotContain(resultField, "(...searching...)"));
  }

}
