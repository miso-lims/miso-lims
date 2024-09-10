package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class FreezerPage extends FormPage<FreezerPage.Field> {

  public static enum Field implements FormPage.FieldElement {

    ID(By.id("freezerForm_idLabel")), //
    ROOM(By.id("freezerForm_parentLocationId")), //
    ALIAS(By.id("freezerForm_alias")), //
    BARCODE(By.id("freezerForm_identificationBarcode")), //
    PROBE_ID(By.id("freezerForm_probeId")); //

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

  public FreezerPage(WebDriver driver) {
    super(driver, "freezerForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Freezer "));
  }

  public static FreezerPage get(WebDriver driver, String baseUrl, long freezerId) {
    driver.get(baseUrl + "freezer/" + freezerId);
    return new FreezerPage(driver);
  }

  public static FreezerPage getForCreate(WebDriver driver, String baseUrl) {
    driver.get(baseUrl + "freezer/new");
    return new FreezerPage(driver);
  }

  public FreezerPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new FreezerPage(getDriver());
  }

}
