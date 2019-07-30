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
    ID(By.id("poolForm_id")), //
    NAME(By.id("poolForm_name")), //
    ALIAS(By.id("poolForm_alias")), //
    BARCODE(By.id("poolForm_identificationBarcode")), //
    DESCRIPTION(By.id("poolForm_description")), //
    PLATFORM(By.id("poolForm_platformType"), By.id("poolForm_platformTypeLabel")), //
    CONCENTRATION(By.id("poolForm_concentration")), //
    CREATE_DATE(By.id("poolForm_creationDate")), //
    QC_PASSED(By.id("poolForm_qcPassed")), //
    VOLUME(By.id("poolForm_volume")), //
    DISCARDED(By.id("poolForm_discarded")), //
    DISTRIBUTED(By.id("poolForm_distributed")), //
    DISTRIBUTION_DATE(By.id("poolForm_distributionDate")), //
    DISTRIBUTION_RECIPIENT(By.id("poolForm_distributionRecipient")), //
    LOCATION(By.id("poolForm_boxPosition")), //
    
    WARNINGS(By.className("big")); //

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

  public static class PoolTableWrapperId {
    public static final String QCS = "list_qcs_wrapper";
    public static final String REQUESTED_ORDERS = "list_order_wrapper";
    public static final String ORDER_STATUS = "list_completion_wrapper";
    public static final String RUNS = "list_run_wrapper";
    public static final String INCLUDED_LIBRARY_ALIQUOTS = "list_included_wrapper";
    public static final String AVAILABLE_LIBRARY_ALIQUOTS = "list_available_wrapper";
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

  public PoolPage addSelectedAliquots() {
    clickLinkButtonAndGetUrl(ButtonText.ADD, null, false);
    return new PoolPage(getDriver());
  }

  public PoolPage removeSelectedAliquots() {
    clickLinkButtonAndGetUrl(ButtonText.REMOVE, null, false);
    return new PoolPage(getDriver());
  }

  public NotesSection<PoolPage> getNotesSection() {
    return notesSection;
  }

  public DataTable getTable(String tableWrapperId) {
    return new DataTable(getDriver(), tableWrapperId);
  }

}
