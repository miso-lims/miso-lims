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
    ID(By.id("instrumentForm_id")), //
    INSTRUMENT_MODEL(By.id("instrumentForm_instrumentModelId")), //
    SERIAL_NUMBER(By.id("instrumentForm_serialNumber")), //
    NAME(By.id("instrumentForm_name")), //
    COMMISSIONED(By.id("instrumentForm_dateCommissioned")), //
    DECOMMISSIONED(By.id("instrumentForm_dateDecommissioned")), //
    STATUS(By.id("instrumentForm_status")), //
    UPGRADED_INSTRUMENT(By.id("instrumentForm_upgradedInstrumentId")), //
    DEFAULT_PURPOSE(By.id("instrumentForm_defaultRunPurposeId"));

    private final By selector;

    private Field(By selector) {
      this.selector = selector;
    }

    @Override
    public By getSelector() {
      return selector;
    }
  } // end Field enum

  @FindBy(id = "save")
  private WebElement saveButton;
  @FindBy(id = "recordsHider")
  private WebElement recordsHider;

  private final DataTable serviceRecords;

  public InstrumentPage(WebDriver driver) {
    super(driver, "instrumentForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Instrument "));
    this.serviceRecords = new DataTable(getDriver(), "list_servicerecords_wrapper");
  }

  public static InstrumentPage get(WebDriver driver, String baseUrl, long instrumentId) {
    driver.get(baseUrl + "instrument/" + instrumentId);
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
