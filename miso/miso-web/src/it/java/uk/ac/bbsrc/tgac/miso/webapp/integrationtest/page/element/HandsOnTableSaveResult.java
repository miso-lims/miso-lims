package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class HandsOnTableSaveResult extends AbstractElement {

  // Assumption: these same IDs are used for all MISO HOT pages
  @FindBy(id = "successMessages")
  private WebElement successMessagesContainer;

  @FindBy(id = "serverErrors")
  private WebElement serverErrorsContainer;

  @FindBy(id = "saveErrors")
  private WebElement saveErrorsContainer;

  private static final Pattern SAVE_MESSAGE_REGEX = Pattern.compile(".*Saved (\\d+) items\\..*");

  private static final By LIST_ITEM_SELECTOR = By.cssSelector("li");

  private final int tableRows;
  private final int itemsSaved;
  private final Map<Integer, Set<String>> invalidFields;
  private final List<String> serverErrors;
  private final List<String> saveErrors;

  public HandsOnTableSaveResult(WebDriver driver, HandsOnTable table) {
    super(driver);
    PageFactory.initElements(driver, this);
    this.tableRows = table.getRowCount();
    this.itemsSaved = findSavedCount();
    this.invalidFields = Collections.unmodifiableMap(findInvalidFields(table));
    this.serverErrors = Collections.unmodifiableList(findServerErrorMessages());
    this.saveErrors = Collections.unmodifiableList(findSaveErrorMessages());
  }

  private int findSavedCount() {
    Matcher m = SAVE_MESSAGE_REGEX.matcher(successMessagesContainer.getText());
    if (!m.matches()) {
      return 0;
    } else {
      return Integer.parseInt(m.group(1));
    }
  }

  private Map<Integer, Set<String>> findInvalidFields(HandsOnTable table) {
    Map<Integer, Set<String>> map = Maps.newHashMap();
    table.getInvalidCellsByRow().forEach((key, val) -> map.put(key, Collections.unmodifiableSet(val)));
    return map;
  }

  private List<String> findServerErrorMessages() {
    List<String> errors = Lists.newArrayList();
    List<WebElement> elements = serverErrorsContainer.findElements(LIST_ITEM_SELECTOR);
    for (WebElement element : elements) {
      errors.add(element.getText());
    }
    return errors;
  }

  private List<String> findSaveErrorMessages() {
    List<String> errors = Lists.newArrayList();
    List<WebElement> elements = saveErrorsContainer.findElements(LIST_ITEM_SELECTOR);
    for (WebElement element : elements) {
      errors.add(element.getText());
    }
    return errors;
  }

  public int getTableRowCount() {
    return tableRows;
  }

  public int getItemsSaved() {
    return itemsSaved;
  }

  public Map<Integer, Set<String>> getInvalidFields() {
    return invalidFields;
  }

  public List<String> getServerErrors() {
    return serverErrors;
  }

  public List<String> getSaveErrors() {
    return saveErrors;
  }

  public boolean isCompleteSuccess() {
    return itemsSaved == tableRows
        && invalidFields.isEmpty()
        && serverErrors.isEmpty()
        && saveErrors.isEmpty();
  }

  public String printSummary() {
    if (isCompleteSuccess()) {
      return "Handsontable save successful";
    } else {
      StringBuilder sb = new StringBuilder();
      sb.append("Handsontable save errors:")
          .append("\n\tRows saved successfully: ").append(itemsSaved);

      serverErrors.forEach(s -> sb.append("\n\tServer error: ").append(s));
      saveErrors.forEach(s -> sb.append("\n\tSave error: ").append(s));

      if (!invalidFields.isEmpty()) {
        sb.append("\n\tInvalid fields: ");
        invalidFields.forEach((row, fields) -> {
          sb.append("\n\t\tRow ").append(row).append(": ");
          sb.append(Joiner.on(", ").join(fields));
        });
      }

      sb.append("\n");
      return sb.toString();
    }
  }

}
