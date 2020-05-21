package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public abstract class BulkPage extends HeaderFooterPage {

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
    }
    return successMessage.isDisplayed();
  }

}
