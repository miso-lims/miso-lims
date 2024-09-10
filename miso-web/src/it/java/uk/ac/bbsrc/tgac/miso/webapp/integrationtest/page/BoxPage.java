package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BoxPage extends FormPage<BoxPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("boxForm_id")), //
    NAME(By.id("boxForm_name")), //
    ALIAS(By.id("boxForm_alias")), //
    BARCODE(By.id("boxForm_identificationBarcode")), //
    DESCRIPTION(By.id("boxForm_description")), //
    USE(By.id("boxForm_useId")), //
    SIZE(By.id("boxForm_sizeId"), By.id("boxForm_sizeIdLabel")) {
      @Override
      public String get(WebDriver driver) {
        return super.get(driver).replaceFirst(" \\(.*\\)", "");
      }
    }, //
    LOCATION(By.id("boxForm_locationBarcode"));

    private final By selector;
    private final By labelSelector;

    private Field(By selector) {
      this.selector = selector;
      this.labelSelector = null;
    }

    private Field(By selector, By labelSelector) {
      this.selector = selector;
      this.labelSelector = labelSelector;
    }

    @Override
    public By getSelector() {
      return selector;
    }

    @Override
    public By getLabelSelector() {
      return labelSelector;
    }
  }

  @FindBy(id = "save")
  private WebElement saveButton;

  private final BoxVisualization visualization;

  public BoxPage(WebDriver driver) {
    super(driver, "boxForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Box"));
    if (driver.getTitle().startsWith("New Box")) {
      visualization = null;
    } else {
      visualization = new BoxVisualization(driver);
    }
  }

  public static BoxPage get(WebDriver driver, String baseUrl, Long boxId) {
    driver.get(baseUrl + "box/" + (boxId == null ? "new" : boxId));

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
