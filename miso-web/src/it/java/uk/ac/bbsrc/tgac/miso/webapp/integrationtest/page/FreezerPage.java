package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class FreezerPage extends FormPage<FreezerPage.Field> {

  public static enum Field implements FormPage.FieldElement {

    ID(By.id("idLabel"), FieldType.LABEL), //
    ROOM(By.id("parentLocationId"), FieldType.DROPDOWN), //
    ALIAS(By.id("alias"), FieldType.TEXT), //
    BARCODE(By.id("identificationBarcode"), FieldType.TEXT), //
    MAP_URL(By.id("mapUrl"), FieldType.TEXT), //
    PROBE_ID(By.id("probeId"), FieldType.TEXT); //

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

  @FindBy(id = "save")
  private WebElement saveButton;

  public FreezerPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Freezer "));
  }

  public static FreezerPage get(WebDriver driver, String baseUrl, long freezerId) {
    driver.get(baseUrl + "miso/freezer/" + freezerId);
    return new FreezerPage(driver);
  }

  public static FreezerPage getForCreate(WebDriver driver, String baseUrl) {
    driver.get(baseUrl + "miso/freezer/new");
    return new FreezerPage(driver);
  }

  public FreezerPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new FreezerPage(getDriver());
  }

}
