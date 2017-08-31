package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.type.PlatformType;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.PoolPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.PoolPage.Field;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.dialog.AddNoteDialog;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.dialog.IdBarcodeDialog;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.Note;

public class PoolPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testCreate() throws Exception {
    PoolPage page1 = PoolPage.getForCreate(getDriver(), getBaseUrl());

    // default values
    Map<PoolPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "Unsaved");
    fields.put(Field.NAME, "Unsaved");
    fields.put(Field.DESCRIPTION, null);
    fields.put(Field.QC_PASSED, null);
    fields.put(Field.READY_TO_RUN, Boolean.TRUE.toString());
    fields.put(Field.VOLUME, null);
    fields.put(Field.DISCARDED, Boolean.FALSE.toString());
    fields.put(Field.LOCATION, null);
    assertFieldValues("default values", fields, page1);

    // enter pool info
    Map<PoolPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.ALIAS, "1IPO_testCreate_POOL");
    changes.put(Field.PLATFORM, "Illumina");
    changes.put(Field.CONCENTRATION, "2.34");
    changes.put(Field.CREATE_DATE, "2017-08-24");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("pre-save", fields, page1);

    PoolPage page2 = page1.save();
    fields.remove(Field.ID);
    fields.remove(Field.NAME);
    assertFieldValues("post-save", fields, page2);
    long savedId = Long.parseLong(page2.getField(Field.ID));
    Pool savedPool = (Pool) getSession().get(PoolImpl.class, savedId);
    fields.put(Field.NAME, "IPO" + savedId);
    assertPoolAttributes(fields, savedPool);
  }

  @Test
  public void testChangeValues() throws Exception {
    PoolPage page1 = PoolPage.getForEdit(getDriver(), getBaseUrl(), 120001L);

    // initial values
    Map<PoolPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "120001");
    fields.put(Field.NAME, "IPO120001");
    fields.put(Field.ALIAS, "1IPO_POOL_1");
    fields.put(Field.DESCRIPTION, "ipodesc120001");
    fields.put(Field.PLATFORM, PlatformType.ILLUMINA.getKey());
    fields.put(Field.CONCENTRATION, "6.5");
    fields.put(Field.CREATE_DATE, "2017-08-15");
    fields.put(Field.QC_PASSED, Boolean.FALSE.toString());
    fields.put(Field.READY_TO_RUN, Boolean.FALSE.toString());
    fields.put(Field.VOLUME, "12.0");
    fields.put(Field.DISCARDED, Boolean.FALSE.toString());
    fields.put(Field.LOCATION, null);
    assertFieldValues("initial values", fields, page1);

    Map<PoolPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.ALIAS, "1IPO_POOL_1_CHANGED");
    changes.put(Field.DESCRIPTION, "changed desc");
    changes.put(Field.CONCENTRATION, "7.25");
    changes.put(Field.QC_PASSED, Boolean.TRUE.toString());
    changes.put(Field.READY_TO_RUN, Boolean.TRUE.toString());
    changes.put(Field.VOLUME, "8.91");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);

    PoolPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    Pool savedPool = (Pool) getSession().get(PoolImpl.class, 120001L);
    assertPoolAttributes(fields, savedPool);
  }

  @Test
  public void testAddValues() throws Exception {
    PoolPage page1 = PoolPage.getForEdit(getDriver(), getBaseUrl(), 120002L);

    // initial values
    Map<PoolPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "120002");
    fields.put(Field.NAME, "IPO120002");
    fields.put(Field.ALIAS, "1IPO_POOL_2");
    fields.put(Field.DESCRIPTION, null);
    fields.put(Field.PLATFORM, PlatformType.ILLUMINA.getKey());
    fields.put(Field.CONCENTRATION, "6.5");
    fields.put(Field.CREATE_DATE, "2017-08-15");
    fields.put(Field.QC_PASSED, null);
    fields.put(Field.READY_TO_RUN, Boolean.FALSE.toString());
    fields.put(Field.VOLUME, null);
    fields.put(Field.DISCARDED, Boolean.FALSE.toString());
    fields.put(Field.LOCATION, null);
    assertFieldValues("initial values", fields, page1);

    Map<PoolPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.DESCRIPTION, "added desc");
    changes.put(Field.QC_PASSED, Boolean.TRUE.toString());
    changes.put(Field.VOLUME, "9.99");
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);

    PoolPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    Pool savedPool = (Pool) getSession().get(PoolImpl.class, 120002L);
    assertPoolAttributes(fields, savedPool);
  }

  @Test
  public void testRemoveValues() throws Exception {
    PoolPage page1 = PoolPage.getForEdit(getDriver(), getBaseUrl(), 120003L);

    // initial values
    Map<PoolPage.Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "120003");
    fields.put(Field.NAME, "IPO120003");
    fields.put(Field.ALIAS, "1IPO_POOL_3");
    fields.put(Field.DESCRIPTION, "ipodesc120003");
    fields.put(Field.PLATFORM, PlatformType.ILLUMINA.getKey());
    fields.put(Field.CONCENTRATION, "6.5");
    fields.put(Field.CREATE_DATE, "2017-08-15");
    fields.put(Field.QC_PASSED, Boolean.FALSE.toString());
    fields.put(Field.READY_TO_RUN, Boolean.FALSE.toString());
    fields.put(Field.VOLUME, "12.0");
    fields.put(Field.DISCARDED, Boolean.FALSE.toString());
    fields.put(Field.LOCATION, null);
    assertFieldValues("initial values", fields, page1);

    Map<PoolPage.Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.DESCRIPTION, null);
    changes.put(Field.QC_PASSED, null);
    changes.put(Field.VOLUME, null);
    page1.setFields(changes);

    fields.putAll(changes);
    assertFieldValues("changes pre-save", fields, page1);

    PoolPage page2 = page1.save();
    assertFieldValues("post-save", fields, page2);
    Pool savedPool = (Pool) getSession().get(PoolImpl.class, 120003L);
    assertPoolAttributes(fields, savedPool);
  }

  @Test
  public void testDiscardedEffects() throws Exception {
    // goal: ensure that discarding a Pool affects volume as expected
    PoolPage page1 = PoolPage.getForEdit(getDriver(), getBaseUrl(), 120004L);
    assertEquals("12.0", page1.getField(Field.VOLUME));
    assertTrue(page1.isEditable(Field.VOLUME));
    page1.setField(Field.DISCARDED, "true");
    assertFalse(page1.isEditable(Field.VOLUME));
    page1.setField(Field.DISCARDED, "false");
    assertTrue(page1.isEditable(Field.VOLUME));
    assertEquals("12.0", page1.getField(Field.VOLUME));
    page1.setField(Field.DISCARDED, "true");
    assertFalse(page1.isEditable(Field.VOLUME));

    PoolPage page2 = page1.save();
    Map<Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.DISCARDED, "true");
    changes.put(Field.VOLUME, "0.0");
    assertFieldValues("changes post-save", changes, page2);

    Pool savedPool = (Pool) getSession().get(PoolImpl.class, 120004L);
    assertTrue(savedPool.isDiscarded());
    assertEquals(new Double(0D), savedPool.getVolume());
  }

  @Test
  public void testIdBarcode() throws Exception {
    final String EMPTY = "";

    // initially null
    PoolPage page1 = PoolPage.getForEdit(getDriver(), getBaseUrl(), 120005L);
    assertNull(page1.getIdBarcode());
    IdBarcodeDialog dialog1 = page1.openAssignBarcodeDialog();
    assertEquals(EMPTY, dialog1.getField(IdBarcodeDialog.Field.CURRENT));
    assertEquals(EMPTY, dialog1.getField(IdBarcodeDialog.Field.INPUT));
    String addedBarcode = "120005_added";
    dialog1.setField(IdBarcodeDialog.Field.INPUT, addedBarcode);

    // add barcode
    PoolPage page2 = dialog1.submit(PoolPage::new);
    assertEquals(addedBarcode, page2.getIdBarcode());
    IdBarcodeDialog dialog2 = page2.openAssignBarcodeDialog();
    assertEquals(addedBarcode, dialog2.getField(IdBarcodeDialog.Field.CURRENT));
    assertEquals(EMPTY, dialog2.getField(IdBarcodeDialog.Field.INPUT));
    String changedBarcode = "120005_changed";
    dialog2.setField(IdBarcodeDialog.Field.INPUT, changedBarcode);

    // change barcode
    PoolPage page3 = dialog2.submit(PoolPage::new);
    assertEquals(changedBarcode, page3.getIdBarcode());
    IdBarcodeDialog dialog3 = page3.openAssignBarcodeDialog();
    assertEquals(changedBarcode, dialog3.getField(IdBarcodeDialog.Field.CURRENT));
    assertEquals(EMPTY, dialog3.getField(IdBarcodeDialog.Field.INPUT));
    dialog3.setField(IdBarcodeDialog.Field.INPUT, null);

    // remove barcode
    PoolPage page4 = dialog3.submit(PoolPage::new);
    assertNull(page4.getIdBarcode());
  }

  @Test
  public void testAddNoteInvalid() throws Exception {
    PoolPage page = PoolPage.getForEdit(getDriver(), getBaseUrl(), 120001L);
    AddNoteDialog<PoolPage> dialog = page.getNotesSection().openAddNoteDialog();
    assertTrue(dialog.isDisplayed());
    // submit invalid note (no text)
    PoolPage page2 = dialog.submit();
    assertNull(page2);
    // dialog remains open
    assertTrue(dialog.isDisplayed());
  }

  @Test
  public void testAddNote() throws Exception {
    PoolPage page1 = PoolPage.getForEdit(getDriver(), getBaseUrl(), 120001L);
    final String text = "test note";
    Predicate<Note> expectedText = note -> text.equals(note.getText());

    List<Note> initialNotes = page1.getNotesSection().getNotes();
    assertFalse(initialNotes.stream().anyMatch(expectedText));

    AddNoteDialog<PoolPage> dialog = page1.getNotesSection().openAddNoteDialog();
    dialog.setField(AddNoteDialog.Field.INTERNAL_ONLY, "true");
    dialog.setField(AddNoteDialog.Field.TEXT, text);
    assertEquals("true", dialog.getField(AddNoteDialog.Field.INTERNAL_ONLY));
    assertEquals("test note", dialog.getField(AddNoteDialog.Field.TEXT));
    PoolPage page2 = dialog.submit();

    List<Note> afterAddNotes = page2.getNotesSection().getNotes();
    assertEquals(initialNotes.size() + 1, afterAddNotes.size());
    assertTrue(afterAddNotes.stream().anyMatch(expectedText));
  }

  @Test
  public void testDeleteNote() throws Exception {
    PoolPage page1 = PoolPage.getForEdit(getDriver(), getBaseUrl(), 120001L);
    final String text = "IPO120001 existing note";
    Predicate<Note> expectedText = note -> text.equals(note.getText());

    List<Note> initialNotes = page1.getNotesSection().getNotes();
    assertTrue(initialNotes.stream().anyMatch(expectedText));

    PoolPage page2 = page1.getNotesSection().deleteNote(text);
    List<Note> afterDeleteNotes = page2.getNotesSection().getNotes();
    assertEquals(initialNotes.size() - 1, afterDeleteNotes.size());
    assertFalse(afterDeleteNotes.stream().anyMatch(expectedText));
  }

  private void assertPoolAttributes(Map<PoolPage.Field, String> expectedValues, Pool pool) {
    assertAttribute(Field.ID, expectedValues, Long.toString(pool.getId()));
    assertAttribute(Field.NAME, expectedValues, pool.getName());
    assertAttribute(Field.ALIAS, expectedValues, pool.getAlias());
    assertAttribute(Field.DESCRIPTION, expectedValues, pool.getDescription());
    assertAttribute(Field.PLATFORM, expectedValues, pool.getPlatformType().getKey());
    assertAttribute(Field.CONCENTRATION, expectedValues, nullOrToString(pool.getConcentration()));
    assertAttribute(Field.CREATE_DATE, expectedValues, LimsUtils.formatDate(pool.getCreationDate()));
    assertAttribute(Field.QC_PASSED, expectedValues, nullOrToString(pool.getQcPassed()));
    assertAttribute(Field.READY_TO_RUN, expectedValues, Boolean.toString(pool.getReadyToRun()));
    assertAttribute(Field.VOLUME, expectedValues, nullOrToString(pool.getVolume()));
    assertAttribute(Field.DISCARDED, expectedValues, Boolean.toString(pool.isDiscarded()));
    assertAttribute(Field.LOCATION, expectedValues, pool.getLocationBarcode());
  }

}
