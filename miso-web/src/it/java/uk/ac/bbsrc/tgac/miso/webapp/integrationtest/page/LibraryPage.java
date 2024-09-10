package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.NotesSection;

public class LibraryPage extends FormPage<LibraryPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("libraryForm_id")), //
    NAME(By.id("libraryForm_name")), //
    ALIAS(By.id("libraryForm_alias")), //
    BARCODE(By.id("libraryForm_identificationBarcode")), //
    DESCRIPTION(By.id("libraryForm_description")), //
    CREATION_DATE(By.id("libraryForm_creationDate")), //
    PLATFORM(By.id("libraryForm_platformType")), //
    LIBRARY_TYPE(By.id("libraryForm_libraryTypeId")), //
    DESIGN(By.id("libraryForm_libraryDesignId")), //
    DESIGN_CODE(By.id("libraryForm_libraryDesignCodeId")), //
    SELECTION(By.id("libraryForm_librarySelectionTypeId")), //
    STRATEGY(By.id("libraryForm_libraryStrategyTypeId")), //
    INDEX_FAMILY(By.id("libraryForm_indexFamilyId")), //
    INDEX_1(By.id("libraryForm_index1Id")), //
    INDEX_2(By.id("libraryForm_index2Id")), //
    DETAILED_QC_STATUS(By.id("libraryForm_detailedQcStatusId")), //
    QC_STATUS_NOTE(By.id("libraryForm_detailedQcStatusNote")), //
    LOW_QUALITY(By.id("libraryForm_lowQuality")), //
    SIZE(By.id("libraryForm_dnaSize")), //
    VOLUME(By.id("libraryForm_volume")), //
    DISCARDED(By.id("libraryForm_discarded")), //
    LOCATION(By.id("libraryForm_locationBarcode")), //
    BOX_LOCATION(By.id("libraryForm_boxPositionLabel")), //
    KIT(By.id("libraryForm_kitDescriptorId")), //
    CONCENTRATION(By.id("libraryForm_concentration")), //
    ARCHIVED(By.id("libraryForm_archived")), //

    WARNINGS(By.className("big"));

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

  private final NotesSection<LibraryPage> notesSection;

  public LibraryPage(WebDriver driver) {
    super(driver, "libraryForm");
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Library "));
    notesSection = new NotesSection<>(driver, LibraryPage::new);
  }

  public static LibraryPage get(WebDriver driver, String baseUrl, long libraryId) {
    driver.get(baseUrl + "library/" + libraryId);
    return new LibraryPage(driver);
  }

  public LibraryPage save() {
    WebElement html = getHtmlElement();
    saveButton.click();
    waitForPageRefresh(html);
    return new LibraryPage(getDriver());
  }

  public NotesSection<LibraryPage> getNotesSection() {
    return notesSection;
  }

  public DataTable getChangeLogTable() {
    return new DataTable(getDriver(), "changelog_wrapper");
  }

}
