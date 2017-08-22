package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.dialog;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.FormPage;

public class IdBarcodeDialog extends FormPage<IdBarcodeDialog.Field> {

  public static enum Field implements FormPage.FieldElement {
    CURRENT(By.id("idBarcodeCurrent"), FieldType.LABEL),
    INPUT(By.id("idBarcodeInput"), FieldType.TEXT);

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

  @FindBy(xpath = "//div[@id='changeIdBarcodeDialog']/..")
  private WebElement dialogContainer;
  private final WebElement submitButton;
  private final WebElement cancelButton;

  public IdBarcodeDialog(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitUntil(visibilityOf(dialogContainer));
    submitButton = dialogContainer.findElement(By.xpath("//button/span[text()='Save']"));
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
   * Submits the Assign New Barcode form
   * 
   * @param parentPageConstructor function to construct a new page after the refresh
   * @return a refreshed parent page
   */
  public <T extends FormPage<?>> T submit(Function<WebDriver, T> parentPageConstructor) {
    WebElement html = getHtmlElement();
    submitButton.click();
    waitForPageRefresh(html);
    return parentPageConstructor.apply(getDriver());
  }

  public void cancel() {
    cancelButton.click();
    waitUntil(invisibilityOf(dialogContainer));
  }

}
