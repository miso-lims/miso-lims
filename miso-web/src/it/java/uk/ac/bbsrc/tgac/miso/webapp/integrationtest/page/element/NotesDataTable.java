package uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.dialog.AddNoteDialog;

public class NotesDataTable<T extends AbstractPage> extends DataTable {

  private final Function<WebDriver, T> parentPageConstructor;

  public NotesDataTable(WebDriver driver, String tableWrapperId, Function<WebDriver, T> parentPageConstructor) {
    super(driver, tableWrapperId);
    this.parentPageConstructor = parentPageConstructor;
  }

  public AddNoteDialog<T> openAddNoteDialog() {
    clickButton("Add Note");
    return new AddNoteDialog<>(getDriver(), parentPageConstructor);
  }

  public List<Note> getNotes() {
    List<String> texts = getColumnValues("Note");
    List<String> owners = getColumnValues("Owner");
    List<String> dates = getColumnValues("Created");

    List<Note> notes = new ArrayList<>();
    for (int i = 0; i < texts.size(); i++) {
      notes.add(new Note(texts.get(i), owners.get(i), dates.get(i)));
    }

    return notes;
  }

  public T deleteNote(String noteText) {
    List<Note> notes = getNotes();
    for (int i = 0; i < notes.size(); i++) {
      if (Objects.equals(noteText, notes.get(i).getText())) {
        checkBoxForRow(i);
        WebElement html = getDriver().findElement(By.tagName("html"));
        clickButton("Delete");
        WebElement dialogOk = getDriver().findElement(By.id("ok"));
        waitUntil(ExpectedConditions.visibilityOf(dialogOk));
        dialogOk.click();
        waitUntil(ExpectedConditions.stalenessOf(html));
        return parentPageConstructor.apply(getDriver());
      }
    }
    throw new IllegalArgumentException("Note not found");
  }

}
