package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.google.common.base.Functions;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.Columns;
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
    QC_STATUS(By.id("poolForm_qcPassed")), //
    VOLUME(By.id("poolForm_volume")), //
    DISCARDED(By.id("poolForm_discarded")), //
    DISTRIBUTED(By.id("poolForm_distributed")), //
    DISTRIBUTION_DATE(By.id("poolForm_distributionDate")), //
    DISTRIBUTION_RECIPIENT(By.id("poolForm_distributionRecipient")), //
    LOCATION(By.id("poolForm_boxPosition")), //

    WARNINGS(By.id("warnings")); //

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

  private static final String NEW_TITLE_PREFIX = "New Pool ";
  private static final String EDIT_TITLE_PREFIX = "Pool ";
  private static final String FORM_ID = "poolForm";
  private static final String ALIQUOTS_TABLE_WRAPPER = "listAliquots_wrapper";

  @FindBy(id = "save")
  private WebElement saveButton;

  @FindBy(id = "dialog")
  private WebElement dialogContainer;

  private final By dialogTitleSelector = By.className("ui-dialog-title");

  private final NotesSection<PoolPage> notesSection;

  private DataTable aliquotsTable = null;

  private PoolPage(WebDriver driver, boolean isNew) {
    super(driver, FORM_ID);
    PageFactory.initElements(driver, this);
    if (isNew) {
      assertTrue(driver.getTitle().startsWith(NEW_TITLE_PREFIX));
      notesSection = null;
    } else {
      assertTrue(driver.getTitle().startsWith(EDIT_TITLE_PREFIX));
      notesSection = new NotesSection<>(driver, d -> new PoolPage(d, false));
    }
    if (findElementIfExists(By.id(ALIQUOTS_TABLE_WRAPPER)) != null) {
      aliquotsTable = new DataTable(driver, ALIQUOTS_TABLE_WRAPPER);
    }
  }

  public static PoolPage getForCreate(WebDriver driver, String baseUrl) {
    driver.get(baseUrl + "pool/new");
    return new PoolPage(driver, true);
  }

  public static PoolPage getForEdit(WebDriver driver, String baseUrl, long poolId) {
    driver.get(baseUrl + "pool/" + poolId);
    return new PoolPage(driver, false);
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
    return new PoolPage(getDriver(), false);
  }

  public NotesSection<PoolPage> getNotesSection() {
    return notesSection;
  }

  public int countAliquots() {
    return aliquotsTable == null ? 0 : aliquotsTable.countRows();
  }

  public boolean addAliquots(List<String> searchTerms) {
    return addAliquots(searchTerms.stream().collect(Collectors.toMap(Functions.identity(), str -> 1)));
  }

  public boolean addAliquots(Map<String, Integer> searchTermsAndProportions) {
    aliquotsTable.clickButton("Add");
    waitUntil(visibilityOf(dialogContainer));
    WebElement searchBox = dialogContainer.findElement(By.tagName("textarea"));
    searchBox.sendKeys(searchTermsAndProportions.keySet().stream().collect(Collectors.joining("\n")));
    clickOk();
    waitUntil(or(textToBe(dialogTitleSelector, "Select Aliquots"), textToBe(dialogTitleSelector, "Error")));
    if ("Error".equals(getDriver().findElement(dialogTitleSelector).getText())) {
      return false;
    }
    clickOk();
    waitUntil(textToBe(dialogTitleSelector, "Edit Proportions"));
    List<WebElement> elements = dialogContainer.findElements(By.tagName("p"));
    for (WebElement element : elements) {
      Pattern p = Pattern.compile("^(LDI\\d+) \\((.*)\\)\\*:$");
      Matcher m = p.matcher(element.getText());
      if (!m.matches()) {
        throw new IllegalStateException("Doesn't match: '" + element.getText() + "'");
      }
      Integer proportion = searchTermsAndProportions.entrySet().stream()
          .filter(entry -> entry.getKey().equals(m.group(1)) || entry.getKey().equals(m.group(2)))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException(
              "Failed to match search term to proportion field with text: " + element.getText()))
          .getValue();
      element.findElement(By.tagName("input")).sendKeys(proportion.toString());
    }
    clickOk();
    waitUntil(invisibilityOf(dialogContainer));
    return true;
  }

  public void removeAliquotsByName(List<String> namesToRemove) {
    List<String> tableNames = aliquotsTable.getColumnValues("Library Aliquot Name").stream()
        .map(this::stripWarnings)
        .collect(Collectors.toList());
    int selected = 0;
    for (int i = 0; i < tableNames.size(); i++) {
      if (namesToRemove.contains(tableNames.get(i))) {
        aliquotsTable.checkBoxForRow(i);
        selected++;
      }
    }
    if (selected != namesToRemove.size()) {
      throw new IllegalArgumentException(
          String.format("Found only %d/%d of the specified aliquots", selected, namesToRemove.size()));
    }
    aliquotsTable.clickButton("Remove");
    waitUntil(invisibilityOf(dialogContainer));
  }

  public String stripWarnings(String value) {
    return value.contains(" ") ? value.substring(0, value.indexOf(" ")) : value;
  }

  public boolean hasAliquotWarning(String warning) {
    return aliquotsTable.doesColumnContainTooltip(Columns.LIBRARY_ALIQUOT_NAME, warning);
  }

}
