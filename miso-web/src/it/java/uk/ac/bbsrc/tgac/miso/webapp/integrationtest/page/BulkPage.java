package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public abstract class BulkPage extends HeaderFooterPage {

  private static final Logger log = LoggerFactory.getLogger(BulkPage.class);

  private static final By TOOLBAR = By.id("bulkactions");
  private static final By DIALOG = By.id("dialog");
  private static final By DIALOG_OK = By.id("ok");

  private static final String ACTION_SORT = "Sort";

  @FindBy(id = "ajaxLoader")
  private WebElement ajaxLoader;

  @FindBy(id = "save")
  private WebElement saveButton;

  @FindBy(id = "dialog")
  private WebElement dialog;

  @FindBy(id = "successMessage")
  private WebElement successMessage;

  @FindBy(id = "errors")
  private WebElement errors;

  public BulkPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitUntil(invisibilityOf(ajaxLoader));
  }

  public abstract HandsOnTable getTable();
  
  protected abstract void refreshElements();

  public boolean save(boolean confirmRequired) {
    return save(confirmRequired, false);
  }

  public boolean save(boolean confirmRequired, boolean failExpected) {
    saveButton.click();
    if (confirmRequired) {
      waitUntil(or(visibilityOf(dialog), visibilityOf(errors)));
      if (dialog.isDisplayed()) {
        WebElement okButton = getDriver().findElement(By.id("ok"));
        okButton.click();
        waitUntil(invisibilityOf(okButton));
      }
    }
    waitUntil(invisibilityOf(ajaxLoader));
    if (successMessage.isDisplayed()) {
      refreshElements();
    } else if (!failExpected) {
      StringBuilder sb = new StringBuilder();
      sb.append("Bulk table save failed.");
      Map<Integer, Set<String>> validationErrors = getTable().getInvalidCellsByRow();
      if (!validationErrors.isEmpty()) {
        sb.append("\nFront-end validation errors:");
        validationErrors.forEach((key, value) -> {
          sb.append("\n  * Row ").append(key).append(": ").append(String.join(", ", value));
        });
      }
      List<WebElement> otherErrors = getDriver().findElements(By.cssSelector("#errors *"));
      if (!otherErrors.isEmpty()) {
        sb.append("\nOther errors:");
        List<Integer> indentTracker = new ArrayList<>();
        for (int i = 0; i < otherErrors.size(); i++) {
          WebElement line = otherErrors.get(i);
          sb.append("\n");
          if ("ul".equals(line.getTagName())) {
            indentTracker.add(Integer.parseInt(line.getAttribute("childElementCount")));
          } else {
            for (int j = 1; j < indentTracker.size(); j++) {
              sb.append("  ");
            }
            sb.append("* ").append(line.getText());
            for (int j = indentTracker.size() - 1; j >= 0; j--) {
              int val = indentTracker.get(j) - 1;
              if (val == 0) {
                indentTracker.remove(j);
              } else {
                indentTracker.set(j, val);
              }
            }
          }
        }
      }
      log.error(sb.toString());
    }
    return successMessage.isDisplayed();
  }

  public void sortTable(String sortOption) {
    ClickAction(ACTION_SORT);
    WebElement dialog = getDriver().findElement(DIALOG);
    waitUntil(visibilityOf(dialog));
    Select primarySort = new Select(getDriver().findElements(By.tagName("select")).get(0));
    if (primarySort.getOptions().stream().noneMatch(opt -> sortOption.equals(opt.getText()))) {
      throw new IllegalArgumentException(String.format("Sort option %s not found in dropdown", sortOption));
    }
    primarySort.selectByVisibleText(sortOption);
    getDriver().findElement(DIALOG_OK).click();
    waitUntil(invisibilityOf(dialog));
    refreshElements();
  }

  private void ClickAction(String buttonText) {
    WebElement toolbar = getDriver().findElement(TOOLBAR);
    WebElement actionButton = toolbar.findElement(By.linkText(buttonText));
    actionButton.click();
  }

}
