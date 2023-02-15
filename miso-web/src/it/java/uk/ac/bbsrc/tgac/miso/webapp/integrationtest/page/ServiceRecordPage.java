package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ServiceRecordPage extends FormPage<ServiceRecordPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("serviceRecordForm_id")), //
    TITLE(By.id("serviceRecordForm_title")), //
    DETAILS(By.id("serviceRecordForm_details")), //
    SERVICED_BY(By.id("serviceRecordForm_servicedBy")), //
    REFERENCE_NUMBER(By.id("serviceRecordForm_referenceNumber")), //
    SERVICE_DATE(By.id("serviceRecordForm_serviceDate")), //
    START_TIME(By.id("serviceRecordForm_startTime")), //
    OUT_OF_SERVICE(By.id("serviceRecordForm_outOfService")), //
    END_TIME(By.id("serviceRecordForm_endTime"));

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

  public ServiceRecordPage(WebDriver driver) {
    super(driver, "serviceRecordForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Service Record "));
  }

  public static ServiceRecordPage get(WebDriver driver, String baseUrl, Long sequencerId, Long serviceRecordId,
      Long instrumentId) {
    if (sequencerId == null && serviceRecordId == null) {
      throw new IllegalArgumentException("Must specify either instrument ID or service record ID");
    } else if (serviceRecordId == null) {
      driver.get(baseUrl + "miso/instrument/" + instrumentId + "/servicerecord/new/" + sequencerId);
    } else {
      driver.get(baseUrl + "miso/instrument/" + instrumentId + "/servicerecord/" + serviceRecordId);
    }
    return new ServiceRecordPage(driver);
  }

  public ServiceRecordPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new ServiceRecordPage(getDriver());
  }

}
