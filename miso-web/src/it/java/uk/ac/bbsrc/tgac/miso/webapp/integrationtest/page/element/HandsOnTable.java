package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import static org.openqa.selenium.support.ui.ExpectedConditions.invisibilityOf;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;

public class HandsOnTable extends AbstractElement {

  private static final By columnHeadingsSelector = By.cssSelector("div.ht_master table.htCore span.colHeader");
  private static final By inputRowsSelector = By.cssSelector("div.ht_master table.htCore tbody tr");
  private static final By inputCellSelector = By.tagName("td");
  private static final By dropdownArrowSelector = By.className("htAutocompleteArrow");
  private static final By activeDropdownSelector = By.cssSelector("div.handsontableInputHolder[style*='block']");
  private static final By dropdownOptionRowsSelector = By.cssSelector("div.ht_master table.htCore > tbody > tr");
  private final List<String> columnHeadings;
  private final List<WebElement> inputRows;


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
    new Actions(getDriver())
        .click(cell)
        .sendKeys(text)
        .sendKeys(Keys.ENTER)
        .build().perform();
  }

  public List<String> getDropdownOptions(String columnHeading, int rowNum) {
    WebElement cell = getCell(columnHeading, rowNum);
    cancelEditing();
    WebElement dropdownArrow = cell.findElement(dropdownArrowSelector);
    dropdownArrow.click();
    WebElement dropdown = hotContainer.findElement(activeDropdownSelector);
    List<WebElement> optionRows = dropdown.findElements(dropdownOptionRowsSelector);
    List<String> singleColumnsOptions = optionRows.stream()
        .map(element -> cleanOptionLabel(element.findElement(By.tagName("td")).getText()))
        .collect(Collectors.toList());
    cancelEditing();
    return singleColumnsOptions;
  }

  private void cancelEditing() {
    new Actions(getDriver())
        .sendKeys(Keys.ESCAPE)
        .build().perform();
  }

  public HandsOnTableSaveResult save() {
    saveButton.click();
    waitUntil(invisibilityOf(ajaxLoader));
    return new HandsOnTableSaveResult(getDriver());
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

  protected WebElement getCell(String columnHeading, int rowNum) {
    int colNum = columnHeadings.indexOf(columnHeading);
    if (colNum == -1) {
      throw new IllegalArgumentException("Column " + columnHeading + " doesn't exist");
    }
    WebElement row = inputRows.get(rowNum);
    List<WebElement> cells = row.findElements(inputCellSelector);
    return cells.get(colNum);
  }

}
