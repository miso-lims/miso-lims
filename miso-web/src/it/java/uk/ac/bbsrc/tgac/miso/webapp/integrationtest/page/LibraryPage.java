package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HoverMenu;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.Note;

public class LibraryPage extends FormPage<LibraryPage.Field> {

  public static enum Field implements FormPage.FieldElement {
    ID(By.id("libraryId"), FieldType.LABEL),
    NAME(By.id("name"), FieldType.LABEL),
    ALIAS(By.id("alias"), FieldType.TEXT),
    DESCRIPTION(By.id("description"), FieldType.TEXT),
    CREATION_DATE(By.id("creationDate"), FieldType.LABEL),
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
    ARCHIVED(By.id("archived"), FieldType.CHECKBOX);

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

  public static class AddNoteDialog extends FormPage<AddNoteDialog.Field> {

    public static enum Field implements FormPage.FieldElement {
      INTERNAL_ONLY(By.id("internalOnly"), FieldType.CHECKBOX),
      TEXT(By.id("notetext"), FieldType.TEXT);

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

    }

    @FindBy(xpath = "//div[@id='addLibraryNoteDialog']/..")
    private WebElement dialogContainer;
    private final WebElement submitButton;
    private final WebElement cancelButton;

    protected AddNoteDialog(WebDriver driver) {
      super(driver);
      PageFactory.initElements(driver, this);
      waitUntil(visibilityOf(dialogContainer));
      submitButton = dialogContainer.findElement(By.xpath("//button/span[text()='Add Note']"));
      cancelButton = dialogContainer.findElement(By.xpath("//button/span[text()='Cancel']"));
    }

    public boolean isDisplayed() {
      try {
        return dialogContainer.isDisplayed();
      } catch (StaleElementReferenceException e) {
        return false;
      }
    }

    /**
     * Submits the Add Note form
     * 
     * @return null if the form was invalid; otherwise, a refreshed LibraryPage
     */
    public LibraryPage submit() {
      WebElement html = getHtmlElement();
      submitButton.click();
      // if invalid (e.g. no text), there's no indication and no refresh. Dialog just stays open
      if (isDisplayed()) {
        return null;
      }
      waitForPageRefresh(html);
      return new LibraryPage(getDriver());
    }

    public void cancel() {
      cancelButton.click();
      waitUntil(invisibilityOf(dialogContainer));
    }

  } // end AddNoteDialog class

  private static final By NOTES_MENU_SELECTOR = By.id("notesMenuHandle");
  private static final By DELETE_NOTE_SELECTOR = By.className("ui-icon-trash"); // child of Note div
  private static final By QCS_MENU_SELECTOR = By.id("notesMenuHandle");
  private static final By DILUTIONS_MENU_SELECTOR = By.id("notesMenuHandle");

  @FindBy(id = "save")
  private WebElement saveButton;

  @FindBy(css = "div.note > div.exppreview")
  private List<WebElement> notes;

  private final HoverMenu notesMenu;
  private final HoverMenu qcsMenu;
  private final HoverMenu dilutionsMenu;

  public LibraryPage(WebDriver driver) {
    super(driver);
    PageFactory.initElements(driver, this);
    waitWithTimeout().until(titleContains("Library "));
    notesMenu = new HoverMenu(getDriver(), NOTES_MENU_SELECTOR);
    qcsMenu = new HoverMenu(getDriver(), QCS_MENU_SELECTOR);
    dilutionsMenu = new HoverMenu(getDriver(), DILUTIONS_MENU_SELECTOR);
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

  public AddNoteDialog openAddNoteDialog() {
    notesMenu.clickOption("Add Note");
    return new AddNoteDialog(getDriver());
  }

  public List<Note> getNotes() {
    return notes.stream().map(note -> new Note(note.getText())).collect(Collectors.toList());
  }

  public LibraryPage deleteNote(String noteText) {
    for (WebElement noteElement : notes) {
      Note note = new Note(noteElement.getText());
      if (noteText.equals(note.getText())) {
        WebElement html = getHtmlElement();
        WebElement deleteButton = noteElement.findElement(DELETE_NOTE_SELECTOR);
        deleteButton.click();
        waitUntil(alertIsPresent());
        Alert jsConfirm = getDriver().switchTo().alert();
        jsConfirm.accept();
        waitForPageRefresh(html);
        return new LibraryPage(getDriver());
      }
    }
    throw new IllegalArgumentException("Note not found");
  }

}
