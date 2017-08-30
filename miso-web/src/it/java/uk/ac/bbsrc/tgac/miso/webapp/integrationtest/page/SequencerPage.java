package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SequencerPage extends FormPage<SequencerPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("instrumentId"), FieldType.LABEL), //
    PLATFORM(By.id("platform"), FieldType.LABEL), //
    SERIAL_NUMBER(By.id("serialNumber"), FieldType.TEXT), //
    NAME(By.id("name"), FieldType.TEXT), //
    IP_ADDRESS(By.id("ipAddress"), FieldType.TEXT), //
    COMMISSIONED(By.id("datecommissionedpicker"), FieldType.DATEPICKER), //
    DECOMMISSIONED(By.id("datedecommissionedpicker"), FieldType.DATEPICKER), //
    STATUS(By.name("status"), FieldType.RADIO), //
    UPGRADED_REF(By.id("upgradedSequencerReference"), FieldType.DROPDOWN);

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
  @FindBy(id = "addServiceRecord")
  private WebElement addServiceRecordButton;

  public SequencerPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Sequencer "));
  }

  public static SequencerPage get(WebDriver driver, String baseUrl, long sequencerId) {
    driver.get(baseUrl + "miso/sequencer/" + sequencerId);
    return new SequencerPage(driver);
  }

  public SequencerPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new SequencerPage(getDriver());
  }

  public ServiceRecordPage addServiceRecord() {
    addServiceRecordButton.click();
    return new ServiceRecordPage(getDriver());
  }
}
