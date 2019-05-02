package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.dialog;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.function.Function;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.FormPage;

public class AddNoteDialog<T extends AbstractPage> extends FormPage<AddNoteDialog.Field> {

  public static enum Field implements FormPage.FieldElement {
    INTERNAL_ONLY(By.cssSelector("#dialog input[type='checkbox']"), FieldType.CHECKBOX), //
    TEXT(By.cssSelector("#dialog textarea"), FieldType.TEXTAREA);

    private final By selector;
    private final FieldType type;

    private Field(By selector, FieldType type) {
      this.selector = selector;
      this.type = type;
    }

    @Override
    public By getSelector() {
      return selector;
    }

    @Override
    public FieldType getType() {
      return type;
    }

  }

  @FindBy(id = "dialog")
  private WebElement dialogContainer;
  private final WebElement submitButton;
  private final WebElement cancelButton;
  private final Function<WebDriver, T> parentPageConstructor;

  public AddNoteDialog(WebDriver driver, Function<WebDriver, T> parentPageConstructor) {
    super(driver);
    this.parentPageConstructor = parentPageConstructor;
    PageFactory.initElements(driver, this);
    waitUntil(visibilityOf(dialogContainer));
    submitButton = driver.findElement(By.id("ok"));
    cancelButton = driver.findElement(By.id("cancel"));
  }

  public boolean isDisplayed() {
    try {
      return dialogContainer.isDisplayed();
    } catch (StaleElementReferenceException e) {
      return false;
    }
  }

  /**
   * Submits the Add Note form
   * 
   * @return null if the form was invalid; otherwise, a refreshed LibraryPage
   */
  public T submit() {
    WebElement html = getHtmlElement();
    submitButton.click();
    // if invalid, an alert appears
    try {
      Alert alert = getDriver().switchTo().alert();
      alert.accept();
      return null;
    } catch (NoAlertPresentException e) {
      waitForPageRefresh(html);
      return parentPageConstructor.apply(getDriver());
    }
  }

  public void cancel() {
    cancelButton.click();
    waitUntil(invisibilityOf(dialogContainer));
  }

}
