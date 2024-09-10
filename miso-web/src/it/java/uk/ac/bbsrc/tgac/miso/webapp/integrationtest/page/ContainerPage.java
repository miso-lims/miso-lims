package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ContainerPage extends FormPage<ContainerPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("containerForm_id"), By.id("containerForm_idLabel")), //
    SERIAL_NUMBER(By.id("containerForm_identificationBarcode")), //
    MODEL(By.id("containerForm_model_id")), //
    CLUSTERING_KIT(By.id("containerForm_clusteringKitId")), //
    MULTIPLEXING_KIT(By.id("containerForm_multiplexingKitId"));

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
  } // end Field enum

  @FindBy(id = "save")
  private WebElement saveButton;

  public ContainerPage(WebDriver driver) {
    super(driver, "containerForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Flow Cell "), titleContains("8Pac ")));
  }

  public static ContainerPage getForCreate(WebDriver driver, String baseUrl, long containerModelId) {
    driver.get(baseUrl + "container/new/" + containerModelId);
    return new ContainerPage(driver);
  }

  public static ContainerPage getForEdit(WebDriver driver, String baseUrl, long containerId) {
    driver.get(baseUrl + "container/" + containerId);
    return new ContainerPage(driver);
  }

  public ContainerPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new ContainerPage(getDriver());
  }
}
