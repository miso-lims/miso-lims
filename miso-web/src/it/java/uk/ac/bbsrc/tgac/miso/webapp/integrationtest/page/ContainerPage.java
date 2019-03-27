package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ContainerPage extends FormPage<ContainerPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("id"), By.id("idLabel"), FieldType.LABEL), //
    SERIAL_NUMBER(By.id("identificationBarcode"), FieldType.TEXT), //
    MODEL(By.id("model.id"), By.id("model.idLabel"), FieldType.LABEL), //
    CLUSTERING_KIT(By.id("clusteringKitId"), FieldType.DROPDOWN), //
    MULTIPLEXING_KIT(By.id("multiplexingKitId"), FieldType.DROPDOWN);

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
  } // end Field enum

  @FindBy(id = "save")
  private WebElement saveButton;

  public ContainerPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Flow Cell "), titleContains("8Pac ")));
  }

  public static ContainerPage getForCreate(WebDriver driver, String baseUrl, long containerModelId) {
    driver.get(baseUrl + "miso/container/new/" + containerModelId);
    return new ContainerPage(driver);
  }

  public static ContainerPage getForEdit(WebDriver driver, String baseUrl, long containerId) {
    driver.get(baseUrl + "miso/container/" + containerId);
    return new ContainerPage(driver);
  }

  public ContainerPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new ContainerPage(getDriver());
  }
}
