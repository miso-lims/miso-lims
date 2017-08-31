package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.dialog;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.FormPage;

public class AddNoteDialog<T extends AbstractPage> extends FormPage<AddNoteDialog.Field> {

  public static enum Field implements FormPage.FieldElement {
    INTERNAL_ONLY(By.id("internalOnly"), FieldType.CHECKBOX),
    TEXT(By.id("notetext"), FieldType.TEXT);

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

  @FindBy(xpath = "//div[@id='addNoteDialog']/..")
  private WebElement dialogContainer;
  private final WebElement submitButton;
  private final WebElement cancelButton;
  private final Function<WebDriver, T> parentPageConstructor;

  public AddNoteDialog(WebDriver driver, Function<WebDriver, T> parentPageConstructor) {
    super(driver);
    this.parentPageConstructor = parentPageConstructor;
    PageFactory.initElements(driver, this);
    waitUntil(visibilityOf(dialogContainer));
    submitButton = dialogContainer.findElement(By.xpath("//button/span[text()='Add Note']"));
    cancelButton = dialogContainer.findElement(By.xpath("//button/span[text()='Cancel']"));
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
    // if invalid (e.g. no text), there's no indication and no refresh. Dialog just stays open
    if (isDisplayed()) {
      return null;
    }
    waitForPageRefresh(html);
    return parentPageConstructor.apply(getDriver());
  }

  public void cancel() {
    cancelButton.click();
    waitUntil(invisibilityOf(dialogContainer));
  }

}
