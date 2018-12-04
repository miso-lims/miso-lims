package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class InstrumentPage extends FormPage<InstrumentPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("instrumentId"), FieldType.LABEL), //
    INSTRUMENT_MODEL(By.id("instrumentModel"), FieldType.LABEL), //
    SERIAL_NUMBER(By.id("serialNumber"), FieldType.TEXT), //
    NAME(By.id("name"), FieldType.TEXT), //
    COMMISSIONED(By.id("datecommissionedpicker"), FieldType.DATEPICKER), //
    DECOMMISSIONED(By.id("datedecommissionedpicker"), FieldType.DATEPICKER), //
    STATUS(By.name("status"), FieldType.RADIO), //
    UPGRADED_INSTRUMENT(By.id("upgradedInstrument"), FieldType.DROPDOWN);

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
  @FindBy(id = "recordsHider")
  private WebElement recordsHider;

  private final DataTable serviceRecords;

  public InstrumentPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Instrument "));
    this.serviceRecords = new DataTable(getDriver(), "list_servicerecords_wrapper");
  }

  public static InstrumentPage get(WebDriver driver, String baseUrl, long instrumentId) {
    driver.get(baseUrl + "miso/instrument/" + instrumentId);
    return new InstrumentPage(driver);
  }

  public InstrumentPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new InstrumentPage(getDriver());
  }

  public ServiceRecordPage addServiceRecord() {
    By records = By.id(serviceRecords.getId());
    if (!getDriver().findElement(records).isDisplayed()) {
      recordsHider.click();
      waitUntil(ExpectedConditions.visibilityOfElementLocated(records));
    }
    serviceRecords.clickButton("Add");
    return new ServiceRecordPage(getDriver());
  }
}
