package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.dialog.AddNoteDialog;

public class NotesSection<T extends AbstractPage> extends AbstractElement {

  private static final By NOTES_MENU_SELECTOR = By.id("notesMenuHandle");
  private static final By DELETE_NOTE_SELECTOR = By.className("ui-icon-trash"); // child of Note div

  @FindBy(css = "div.note > div.exppreview")
  private List<WebElement> notes;

  @FindBy(id = "ok")
  private WebElement dialogOk;

  private final HoverMenu notesMenu;
  private final Function<WebDriver, T> parentPageConstructor;

  public NotesSection(WebDriver driver, Function<WebDriver, T> parentPageConstructor) {
    super(driver);
    this.parentPageConstructor = parentPageConstructor;
    PageFactory.initElements(driver, this);
    notesMenu = new HoverMenu(getDriver(), NOTES_MENU_SELECTOR);
  }

  public AddNoteDialog<T> openAddNoteDialog() {
    notesMenu.clickOption("Add Note");
    return new AddNoteDialog<T>(getDriver(), parentPageConstructor);
  }

  public List<Note> getNotes() {
    return notes.stream().map(note -> new Note(note.getText())).collect(Collectors.toList());
  }

  public T deleteNote(String noteText) {
    for (WebElement noteElement : notes) {
      Note note = new Note(noteElement.getText());
      if (noteText.equals(note.getText())) {
        WebElement html = getDriver().findElement(By.tagName("html"));
        WebElement deleteButton = noteElement.findElement(DELETE_NOTE_SELECTOR);
        deleteButton.click();
        waitUntil(ExpectedConditions.visibilityOf(dialogOk));
        dialogOk.click();
        waitUntil(ExpectedConditions.stalenessOf(html));
        return parentPageConstructor.apply(getDriver());
      }
    }
    throw new IllegalArgumentException("Note not found");
  }

}
