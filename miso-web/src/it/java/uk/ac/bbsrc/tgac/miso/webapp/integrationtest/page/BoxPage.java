package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BoxPage extends FormPage<BoxPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("id"), FieldType.LABEL), //
    NAME(By.id("name"), FieldType.LABEL), //
    ALIAS(By.id("alias"), FieldType.TEXT), //
    BARCODE(By.id("identificationBarcode"), FieldType.TEXT), //
    DESCRIPTION(By.id("description"), FieldType.TEXT), //
    USE(By.id("useId"), FieldType.DROPDOWN), //
    SIZE(By.id("sizeId"), By.id("sizeIdLabel"), FieldType.DROPDOWN) {
      @Override
      public String get(WebDriver driver) {
        return super.get(driver).replaceFirst(" \\(.*\\)", "");
      }
    }, //
    LOCATION(By.id("locationBarcode"), FieldType.TEXT);

    private final By selector;
    private final By labelSelector;
    private final FieldType type;

    private Field(By selector, FieldType type) {
      this.selector = selector;
      this.labelSelector = null;
      this.type = type;
    }

    private Field(By selector, By labelSelector, FieldType type) {
      this.selector = selector;
      this.labelSelector = labelSelector;
      this.type = type;
    }

    @Override
    public By getSelector() {
      return selector;
    }

    @Override
    public By getLabelSelector() {
      return labelSelector;
    }

    @Override
    public FieldType getType() {
      return type;
    }
  }

  @FindBy(id = "save")
  private WebElement saveButton;

  private final BoxVisualization visualization;

  public BoxPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Box"));
    if (driver.getTitle().startsWith("New Box")) {
      visualization = null;
    } else {
      visualization = new BoxVisualization(driver);
    }
  }

  public static BoxPage get(WebDriver driver, String baseUrl, Long boxId) {
    driver.get(baseUrl + "miso/box/" + (boxId == null ? "new" : boxId));

    return new BoxPage(driver);
  }

  public BoxVisualization getVisualization() {
    return visualization;
  }

  public BoxPage clickSave() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new BoxPage(getDriver());
  }

}
