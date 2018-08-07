package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.titleContains;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HoverMenu;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.NotesSection;

public class LibraryPage extends FormPage<LibraryPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("libraryId"), FieldType.LABEL),
    NAME(By.id("name"), FieldType.LABEL),
    ALIAS(By.id("alias"), FieldType.TEXT),
    BARCODE(By.id("identificationBarcode"), FieldType.TEXT),
    DESCRIPTION(By.id("description"), FieldType.TEXT),
    CREATION_DATE(By.id("creationdatepicker"), FieldType.DATEPICKER),
    PLATFORM(By.id("platformTypes"), FieldType.DROPDOWN),
    LIBRARY_TYPE(By.id("libraryTypes"), FieldType.DROPDOWN),
    DESIGN(By.id("libraryDesignTypes"), FieldType.DROPDOWN),
    DESIGN_CODE(By.id("libraryDesignCodes"), FieldType.DROPDOWN),
    SELECTION(By.id("librarySelectionTypes"), FieldType.DROPDOWN),
    STRATEGY(By.id("libraryStrategyTypes"), FieldType.DROPDOWN),
    INDEX_FAMILY(By.id("indexFamily"), FieldType.DROPDOWN),
    INDEX_1(By.id("index1"), FieldType.DROPDOWN),
    INDEX_2(By.id("index2"), FieldType.DROPDOWN),
    QC_PASSED(By.name("qcPassed"), FieldType.RADIO),
    LOW_QUALITY(By.id("lowQuality"), FieldType.CHECKBOX),
    SIZE(By.id("dnaSize"), FieldType.TEXT),
    VOLUME(By.id("volume"), FieldType.TEXT),
    DISCARDED(By.id("discarded"), FieldType.CHECKBOX),
    LOCATION(By.id("locationBarcode"), FieldType.TEXT),
    BOX_LOCATION(By.id("boxLocation"), FieldType.LABEL),
    KIT(By.id("libraryKit"), FieldType.DROPDOWN),
    CONCENTRATION(By.id("initialConcentration"), FieldType.TEXT),
    ARCHIVED(By.id("archived"), FieldType.CHECKBOX),

    WARNINGS(By.className("big-warning"), FieldType.LABEL);

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

  private static final By QCS_MENU_SELECTOR = By.id("notesMenuHandle");
  private static final By DILUTIONS_MENU_SELECTOR = By.id("notesMenuHandle");

  @FindBy(id = "save")
  private WebElement saveButton;

  private final HoverMenu qcsMenu;
  private final HoverMenu dilutionsMenu;
  private final NotesSection<LibraryPage> notesSection;

  public LibraryPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Library "));
    qcsMenu = new HoverMenu(getDriver(), QCS_MENU_SELECTOR);
    dilutionsMenu = new HoverMenu(getDriver(), DILUTIONS_MENU_SELECTOR);
    notesSection = new NotesSection<>(driver, LibraryPage::new);
  }

  public static LibraryPage get(WebDriver driver, String baseUrl, long libraryId) {
    driver.get(baseUrl + "miso/library/" + libraryId);
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

}
