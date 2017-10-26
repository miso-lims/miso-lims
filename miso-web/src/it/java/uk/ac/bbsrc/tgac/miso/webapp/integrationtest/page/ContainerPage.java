package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ContainerPage extends FormPage<ContainerPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("containerId"), FieldType.LABEL), //
    SERIAL_NUMBER(By.id("identificationBarcode"), FieldType.TEXT), //
    PLATFORM(By.id("platform"), FieldType.LABEL), //
    MODEL(By.id("model"), FieldType.LABEL), //
    CLUSTERING_KIT(By.id("clusteringKit"), FieldType.DROPDOWN), //
    MULTIPLEXING_KIT(By.id("multiplexingKit"), FieldType.DROPDOWN);

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
  } // end Field enum

  @FindBy(id = "save")
  private WebElement saveButton;

  public ContainerPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Flow Cell "), titleContains("8Pac ")));
  }

  public static ContainerPage getForCreate(WebDriver driver, String baseUrl, Long sequencerModelId,
      int numPartitions) {
    driver.get(baseUrl + "miso/container/new/" + sequencerModelId.toString() + "?count=" + numPartitions);
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
