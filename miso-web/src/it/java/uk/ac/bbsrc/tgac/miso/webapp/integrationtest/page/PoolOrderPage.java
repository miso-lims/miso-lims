package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class PoolOrderPage extends FormPage<PoolOrderPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("orderForm_id")), //
    ALIAS(By.id("orderForm_alias")), //
    DESCRIPTION(By.id("orderForm_description")), //
    PURPOSE(By.id("orderForm_purposeId")), //
    SPECIFY_SEQUENCING(By.id("orderForm_sequencingRequirements")), //
    PLATFORM(By.id("orderForm_platform")), //
    INSTRUMENT_MODEL(By.id("orderForm_instrumentModel")), //
    SEQUENCING_PARAMETERS(By.id("orderForm_parametersId")), //
    PARTITIONS(By.id("orderForm_partitions")), //
    DRAFT(By.id("orderForm_draft")); //

    private final By selector;

    private Field(By selector) {
      this.selector = selector;
    }

    @Override
    public By getSelector() {
      return selector;
    }
  }

  @FindBy(id = "save")
  private WebElement saveButton;

  private final DataTable aliquotsTable;

  public PoolOrderPage(WebDriver driver) {
    super(driver, "orderForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Pool Order "));
    aliquotsTable = new DataTable(driver, "listAliquots_wrapper");
  }

  public static PoolOrderPage getForCreate(WebDriver driver, String baseUrl) {
    driver.get(baseUrl + "poolorder/new");
    return new PoolOrderPage(driver);
  }

  public static PoolOrderPage getForEdit(WebDriver driver, String baseUrl, long orderId) {
    driver.get(baseUrl + "poolorder/" + orderId);
    return new PoolOrderPage(driver);
  }

  public PoolOrderPage clickSave() {
    WebElement html = getHtmlElement();
    WebElement errorBox = getDriver().findElement(By.className("bs-callout"));
    if (errorBox.isDisplayed()) {
      saveButton.click();
      waitUntil(ExpectedConditions.or(ExpectedConditions.stalenessOf(html), ExpectedConditions.stalenessOf(errorBox)));
    } else {
      saveButton.click();
      try {
        waitUntil(
            ExpectedConditions.or(ExpectedConditions.stalenessOf(html), ExpectedConditions.visibilityOf(errorBox)));
      } catch (StaleElementReferenceException e) {
        waitUntil(ExpectedConditions.stalenessOf(html));
      }
    }
    if (ExpectedConditions.stalenessOf(html).apply(getDriver())) {
      waitForPageRefresh(html);
      return new PoolOrderPage(getDriver());
    } else {
      printValidationErrors("orderForm");
      return null;
    }
  }

  public void addAliquots(Collection<String> searchStrings) {
    aliquotsTable.clickButton("Add");
    By dialogSelector = By.id("dialog");
    By okButtonSelector = By.id("ok");
    waitUntil(ExpectedConditions.visibilityOfElementLocated(dialogSelector));
    WebElement searchBox = getDriver().findElement(By.cssSelector("#dialog textarea"));
    searchBox.click();
    for (String searchString : searchStrings) {
      searchBox.sendKeys(searchString);
      searchBox.sendKeys(Keys.ENTER);
    }
    WebElement okButton = getDriver().findElement(okButtonSelector);
    okButton.click();
    waitUntil(ExpectedConditions.stalenessOf(okButton));
    waitUntil(ExpectedConditions.visibilityOfElementLocated(okButtonSelector));
    okButton = getDriver().findElement(okButtonSelector);
    okButton.click();
    waitUntil(ExpectedConditions.stalenessOf(okButton));
    waitUntil(ExpectedConditions.visibilityOfElementLocated(okButtonSelector));
    okButton = getDriver().findElement(okButtonSelector);
    WebElement dialog = getDriver().findElement(dialogSelector);
    okButton.click();
    waitUntil(ExpectedConditions.invisibilityOf(dialog));
    // The AJAX dialog is fast enough that finding it be visibility seems to be hard
    getDriver().manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
  }

}
