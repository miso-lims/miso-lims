package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.ButtonText;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.NotesSection;

public class PoolPage extends FormPage<PoolPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("poolId"), FieldType.LABEL),
    NAME(By.id("name"), FieldType.LABEL),
    ALIAS(By.id("alias"), FieldType.TEXT),
    BARCODE(By.id("identificationBarcode"), FieldType.TEXT),
    DESCRIPTION(By.id("description"), FieldType.TEXT),
    PLATFORM(By.id("platformType"), FieldType.DROPDOWN),
    CONCENTRATION(By.id("concentration"), FieldType.TEXT),
    CREATE_DATE(By.id("creationDate"), FieldType.TEXT),
    QC_PASSED(By.name("qcPassed"), FieldType.RADIO),
    VOLUME(By.id("volume"), FieldType.TEXT),
    DISCARDED(By.id("discarded"), FieldType.CHECKBOX),
    LOCATION(By.id("location"), FieldType.LABEL);

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

  public static class PoolTableWrapperId {
    public static final String QCS = "list_qcs_wrapper";
    public static final String REQUESTED_ORDERS = "list_order_wrapper";
    public static final String ORDER_STATUS = "list_completion_wrapper";
    public static final String RUNS = "list_run_wrapper";
    public static final String INCLUDED_DILUTIONS = "list_included_wrapper";
    public static final String AVAILABLE_DILUTIONS = "list_available_wrapper";
    public static final String CHANGES = "changelog_wrapper";
  }

  private static final String NEW_TITLE_PREFIX = "New Pool ";

  @FindBy(id = "save")
  private WebElement saveButton;

  private final NotesSection<PoolPage> notesSection;

  public PoolPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(or(titleContains("Pool "), titleContains(NEW_TITLE_PREFIX)));
    boolean isNew = driver.getTitle().startsWith(NEW_TITLE_PREFIX);
    notesSection = isNew ? null : new NotesSection<>(driver, PoolPage::new);
  }

  public static PoolPage getForCreate(WebDriver driver, String baseUrl) {
    driver.get(baseUrl + "miso/pool/new");
    return new PoolPage(driver);
  }

  public static PoolPage getForEdit(WebDriver driver, String baseUrl, long poolId) {
    driver.get(baseUrl + "miso/pool/" + poolId);
    return new PoolPage(driver);
  }

  public PoolPage save(boolean confirmMissingBarcode) {
    WebElement html = getHtmlElement();
    saveButton.click();
    if (confirmMissingBarcode) {
      WebElement okButton = getDriver().findElement(By.id("ok"));
      okButton.click();
      waitUntil(invisibilityOf(okButton));
    }
    waitForPageRefresh(html);
    return new PoolPage(getDriver());
  }

  public PoolPage addSelectedDilutions() {
    clickLinkButtonAndGetUrl(ButtonText.ADD, null);
    return new PoolPage(getDriver());
  }

  public PoolPage removeSelectedDilutions() {
    clickLinkButtonAndGetUrl(ButtonText.REMOVE, null);
    return new PoolPage(getDriver());
  }

  public NotesSection<PoolPage> getNotesSection() {
    return notesSection;
  }

  public DataTable getTable(String tableWrapperId) {
    return new DataTable(getDriver(), tableWrapperId);
  }
}
