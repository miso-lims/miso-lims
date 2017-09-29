package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesign;
import uk.ac.bbsrc.tgac.miso.core.data.LibraryDesignCode;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.kit.KitDescriptor;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibrarySelectionType;
import uk.ac.bbsrc.tgac.miso.core.data.type.LibraryStrategyType;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LibraryPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LibraryPage.Field;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.dialog.AddNoteDialog;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.Note;

public class LibraryPageIT extends AbstractIT {

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testChangeValues() throws Exception {
    LibraryPage page = LibraryPage.get(getDriver(), getBaseUrl(), 110001L);

    // check initial values
    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "110001");
    fields.put(Field.NAME, "LIB110001");
    fields.put(Field.ALIAS, "1LIB_0001_Ly_P_PE_251_WG");
    fields.put(Field.BARCODE, "libbar110001");
    fields.put(Field.DESCRIPTION, "libdesc110001");
    fields.put(Field.CREATION_DATE, "2017-07-24");
    fields.put(Field.PLATFORM, "Illumina");
    fields.put(Field.LIBRARY_TYPE, "Paired End");
    fields.put(Field.DESIGN, "(None)");
    fields.put(Field.DESIGN_CODE, "WG");
    fields.put(Field.SELECTION, "PCR");
    fields.put(Field.STRATEGY, "WGS");
    fields.put(Field.INDEX_FAMILY, "Dual Index 6bp");
    fields.put(Field.INDEX_1, "A01 (AAACCC)");
    fields.put(Field.INDEX_2, "B01 (AAATTT)");
    fields.put(Field.QC_PASSED, "false");
    fields.put(Field.LOW_QUALITY, "false");
    fields.put(Field.SIZE, "251");
    fields.put(Field.VOLUME, "2.5");
    fields.put(Field.DISCARDED, "false");
    fields.put(Field.LOCATION, null);
    fields.put(Field.BOX_LOCATION, null);
    fields.put(Field.KIT, "Test Kit");
    fields.put(Field.CONCENTRATION, "10.0");
    fields.put(Field.ARCHIVED, "false");
    assertFieldValues("loaded", fields, page);

    // make changes
    Map<Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.ALIAS, "1LIB_0001_Ly_P_PE_151_WG");
    changes.put(Field.BARCODE, "newbarcode");
    changes.put(Field.DESCRIPTION, "libdesc_changed_110001");
    changes.put(Field.LIBRARY_TYPE, "Total RNA");
    changes.put(Field.DESIGN_CODE, "MR");
    changes.put(Field.SELECTION, "cDNA");
    changes.put(Field.STRATEGY, "RNA-Seq");
    changes.put(Field.INDEX_FAMILY, "Single Index 6bp");
    changes.put(Field.INDEX_1, "Index 01 (AAAAAA)");
    changes.put(Field.QC_PASSED, "true");
    changes.put(Field.LOW_QUALITY, "true");
    changes.put(Field.SIZE, "151");
    changes.put(Field.VOLUME, "0.5");
    changes.put(Field.LOCATION, "on the floor");
    changes.put(Field.KIT, "Test Kit Two");
    changes.put(Field.CONCENTRATION, "8.33");
    changes.put(Field.ARCHIVED, "true");
    page.setFields(changes);

    // copy unchanged
    fields.forEach((key, val) -> {
      if (!changes.containsKey(key)) changes.put(key, val);
    });
    changes.remove(Field.INDEX_2);
    assertFieldValues("changes pre-save", changes, page);

    LibraryPage page2 = page.save();
    assertNotNull(page2);
    assertFieldValues("changes post-save", changes, page2);

    DetailedLibrary lib = (DetailedLibrary) getSession().get(LibraryImpl.class, 110001L);
    assertDetailedLibraryAttributes(changes, lib);
  }

  @Test
  public void testRemoveValues() throws Exception {
    LibraryPage page = LibraryPage.get(getDriver(), getBaseUrl(), 110002L);

    // check initial values
    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "110002");
    fields.put(Field.NAME, "LIB110002");
    fields.put(Field.ALIAS, "1LIB_0001_Ly_P_PE_252_WG");
    fields.put(Field.BARCODE, "libbar110002");
    fields.put(Field.DESCRIPTION, "libdesc110002");
    fields.put(Field.CREATION_DATE, "2017-07-24");
    fields.put(Field.PLATFORM, "Illumina");
    fields.put(Field.LIBRARY_TYPE, "Paired End");
    fields.put(Field.DESIGN, "WG");
    fields.put(Field.DESIGN_CODE, "WG");
    fields.put(Field.SELECTION, "PCR");
    fields.put(Field.STRATEGY, "WGS");
    fields.put(Field.INDEX_FAMILY, "Dual Index 6bp");
    fields.put(Field.INDEX_1, "A01 (AAACCC)");
    fields.put(Field.INDEX_2, "B01 (AAATTT)");
    fields.put(Field.QC_PASSED, "false");
    fields.put(Field.LOW_QUALITY, "false");
    fields.put(Field.SIZE, "252");
    fields.put(Field.VOLUME, "4.0");
    fields.put(Field.DISCARDED, "false");
    fields.put(Field.LOCATION, "lib_location_110002");
    fields.put(Field.BOX_LOCATION, null);
    fields.put(Field.KIT, "Test Kit");
    fields.put(Field.CONCENTRATION, "6.3");
    fields.put(Field.ARCHIVED, "false");
    assertFieldValues("loaded", fields, page);

    // make changes
    Map<Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.BARCODE, null);
    changes.put(Field.DESCRIPTION, null);
    changes.put(Field.DESIGN, "(None)");
    changes.put(Field.SELECTION, "(None)");
    changes.put(Field.STRATEGY, "(None)");
    changes.put(Field.INDEX_FAMILY, "No indices");
    changes.put(Field.QC_PASSED, null);
    changes.put(Field.SIZE, null);
    changes.put(Field.VOLUME, null);
    changes.put(Field.LOCATION, null);
    changes.put(Field.CONCENTRATION, null);
    page.setFields(changes);

    // copy unchanged
    fields.forEach((key, val) -> {
      if (!changes.containsKey(key)) changes.put(key, val);
    });
    changes.remove(Field.INDEX_1);
    changes.remove(Field.INDEX_2);
    assertFieldValues("changes pre-save", changes, page);

    LibraryPage page2 = page.save();
    assertNotNull(page2);
    assertFieldValues("changes post-save", changes, page2);

    DetailedLibrary lib = (DetailedLibrary) getSession().get(LibraryImpl.class, 110002L);
    assertDetailedLibraryAttributes(changes, lib);
  }

  @Test
  public void testAddValues() throws Exception {
    LibraryPage page = LibraryPage.get(getDriver(), getBaseUrl(), 110003L);

    // check initial values
    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.ID, "110003");
    fields.put(Field.NAME, "LIB110003");
    fields.put(Field.ALIAS, "1LIB_0001_Ly_P_PE_253_WG");
    fields.put(Field.BARCODE, null);
    fields.put(Field.DESCRIPTION, null);
    fields.put(Field.CREATION_DATE, "2017-07-24");
    fields.put(Field.PLATFORM, "Illumina");
    fields.put(Field.LIBRARY_TYPE, "Paired End");
    fields.put(Field.DESIGN, "(None)");
    fields.put(Field.DESIGN_CODE, "WG");
    fields.put(Field.SELECTION, "(None)");
    fields.put(Field.STRATEGY, "(None)");
    fields.put(Field.INDEX_FAMILY, "No indices");
    fields.put(Field.INDEX_1, "No index (null)");
    fields.put(Field.QC_PASSED, null);
    fields.put(Field.LOW_QUALITY, "false");
    fields.put(Field.SIZE, null);
    fields.put(Field.VOLUME, null);
    fields.put(Field.DISCARDED, "false");
    fields.put(Field.LOCATION, null);
    fields.put(Field.BOX_LOCATION, null);
    fields.put(Field.KIT, "Test Kit");
    fields.put(Field.CONCENTRATION, null);
    fields.put(Field.ARCHIVED, "false");
    assertFieldValues("loaded", fields, page);

    // make changes
    Map<Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.BARCODE, "preciouspreciousbarcode");
    changes.put(Field.DESCRIPTION, "lib_110003_desc");
    changes.put(Field.DESIGN, "WG");
    changes.put(Field.INDEX_FAMILY, "No indices");
    changes.put(Field.QC_PASSED, "true");
    changes.put(Field.SIZE, "253");
    changes.put(Field.VOLUME, "1000.12");
    changes.put(Field.LOCATION, "lib_110003_location");
    changes.put(Field.CONCENTRATION, "30.2");
    page.setFields(changes);

    // copy unchanged
    fields.forEach((key, val) -> {
      if (!changes.containsKey(key)) changes.put(key, val);
    });
    // affected by library design change
    changes.put(Field.SELECTION, "PCR");
    changes.put(Field.STRATEGY, "WGS");
    assertFieldValues("changes pre-save", changes, page);

    LibraryPage page2 = page.save();
    assertNotNull(page2);
    assertFieldValues("changes post-save", changes, page2);

    DetailedLibrary lib = (DetailedLibrary) getSession().get(LibraryImpl.class, 110003L);
    assertDetailedLibraryAttributes(changes, lib);
  }

  @Test
  public void testDiscardedEffects() throws Exception {
    // goal: ensure that discarding a Library affects volume as expected
    LibraryPage page = LibraryPage.get(getDriver(), getBaseUrl(), 110004L);
    assertEquals("5.0", page.getField(Field.VOLUME));
    assertTrue(page.isEditable(Field.VOLUME));
    page.setField(Field.DISCARDED, "true");
    assertFalse(page.isEditable(Field.VOLUME));
    page.setField(Field.DISCARDED, "false");
    assertTrue(page.isEditable(Field.VOLUME));
    assertEquals("5.0", page.getField(Field.VOLUME));
    page.setField(Field.DISCARDED, "true");
    assertFalse(page.isEditable(Field.VOLUME));

    LibraryPage page2 = page.save();
    Map<Field, String> changes = Maps.newLinkedHashMap();
    changes.put(Field.DISCARDED, "true");
    changes.put(Field.VOLUME, "0.0");
    assertFieldValues("changes post-save", changes, page2);

    DetailedLibrary lib = (DetailedLibrary) getSession().get(LibraryImpl.class, 110004L);
    assertTrue(lib.isDiscarded());
    assertEquals(new Double(0D), lib.getVolume());
  }

  @Test
  public void testDesignEffects() throws Exception {
    // goal: ensure the Library Design dropdown affects other options as expected
    LibraryPage page = LibraryPage.get(getDriver(), getBaseUrl(), 110005L);

    Map<Field, String> fields = Maps.newLinkedHashMap();
    fields.put(Field.DESIGN, "(None)");
    fields.put(Field.DESIGN_CODE, "EX");
    fields.put(Field.SELECTION, "(None)");
    fields.put(Field.STRATEGY, "(None)");
    assertFieldValues("loaded", fields, page);
    assertTrue(page.isEditable(Field.DESIGN_CODE));
    assertTrue(page.isEditable(Field.SELECTION));
    assertTrue(page.isEditable(Field.STRATEGY));

    page.setField(Field.DESIGN, "WG");
    // fields affected by design
    fields.put(Field.DESIGN, "WG");
    fields.put(Field.DESIGN_CODE, "WG");
    fields.put(Field.SELECTION, "PCR");
    fields.put(Field.STRATEGY, "WGS");

    assertFieldValues("loaded", fields, page);
    assertFalse(page.isEditable(Field.DESIGN_CODE));
    assertFalse(page.isEditable(Field.SELECTION));
    assertFalse(page.isEditable(Field.STRATEGY));

    page.setField(Field.DESIGN, "(None)");
    fields.put(Field.DESIGN, "(None)");
    assertFieldValues("loaded", fields, page);
    assertTrue(page.isEditable(Field.DESIGN_CODE));
    assertTrue(page.isEditable(Field.SELECTION));
    assertTrue(page.isEditable(Field.STRATEGY));
  }

  @Test
  public void testPlatformEffects() throws Exception {
    // goal: ensure the Platform dropdown affects Library Type and Indices as expected
    LibraryPage page = LibraryPage.get(getDriver(), getBaseUrl(), 110005L);

    assertEquals("Illumina", page.getField(Field.PLATFORM));
    assertEquals("Paired End", page.getField(Field.LIBRARY_TYPE));
    assertEquals("Dual Index 6bp", page.getField(Field.INDEX_FAMILY));
    assertEquals("A01 (AAACCC)", page.getField(Field.INDEX_1));
    assertEquals("B01 (AAATTT)", page.getField(Field.INDEX_2));

    page.setField(Field.PLATFORM, "PacBio");

    assertEquals("PacBio", page.getField(Field.PLATFORM));
    assertNotEquals("Paired End", page.getField(Field.LIBRARY_TYPE));
    assertEquals("No indices", page.getField(Field.INDEX_FAMILY));
    assertEquals("No index (null)", page.getField(Field.INDEX_1));
  }

  @Test
  public void testCancelAddNote() throws Exception {
    LibraryPage page = LibraryPage.get(getDriver(), getBaseUrl(), 110005L);
    AddNoteDialog<LibraryPage> dialog = page.getNotesSection().openAddNoteDialog();
    assertTrue(dialog.isDisplayed());
    dialog.setField(AddNoteDialog.Field.INTERNAL_ONLY, "true");
    dialog.setField(AddNoteDialog.Field.TEXT, "test note");
    dialog.cancel();
    assertFalse(dialog.isDisplayed());
  }

  @Test
  public void testAddNoteInvalid() throws Exception {
    LibraryPage page = LibraryPage.get(getDriver(), getBaseUrl(), 110005L);
    AddNoteDialog<LibraryPage> dialog = page.getNotesSection().openAddNoteDialog();
    assertTrue(dialog.isDisplayed());
    // submit invalid note (no text)
    LibraryPage page2 = dialog.submit();
    assertNull(page2);
    // dialog remains open
    assertTrue(dialog.isDisplayed());
  }

  @Test
  public void testAddNote() throws Exception {
    LibraryPage page1 = LibraryPage.get(getDriver(), getBaseUrl(), 110005L);
    final String text = "test note";
    Predicate<Note> expectedText = note -> text.equals(note.getText());

    List<Note> initialNotes = page1.getNotesSection().getNotes();
    assertFalse(initialNotes.stream().anyMatch(expectedText));

    AddNoteDialog<LibraryPage> dialog = page1.getNotesSection().openAddNoteDialog();
    dialog.setField(AddNoteDialog.Field.INTERNAL_ONLY, "true");
    dialog.setField(AddNoteDialog.Field.TEXT, text);
    assertEquals("true", dialog.getField(AddNoteDialog.Field.INTERNAL_ONLY));
    assertEquals("test note", dialog.getField(AddNoteDialog.Field.TEXT));
    LibraryPage page2 = dialog.submit();

    List<Note> afterAddNotes = page2.getNotesSection().getNotes();
    assertEquals(initialNotes.size() + 1, afterAddNotes.size());
    assertTrue(afterAddNotes.stream().anyMatch(expectedText));
  }

  @Test
  public void testDeleteNote() throws Exception {
    LibraryPage page1 = LibraryPage.get(getDriver(), getBaseUrl(), 110005L);
    final String text = "LIB110005 existing note";
    Predicate<Note> expectedText = note -> text.equals(note.getText());

    List<Note> initialNotes = page1.getNotesSection().getNotes();
    assertTrue(initialNotes.stream().anyMatch(expectedText));

    LibraryPage page2 = page1.getNotesSection().deleteNote(text);
    List<Note> afterDeleteNotes = page2.getNotesSection().getNotes();
    assertEquals(initialNotes.size() - 1, afterDeleteNotes.size());
    assertFalse(afterDeleteNotes.stream().anyMatch(expectedText));
  }

  private static final DateTimeFormatter dateFormatter = ISODateTimeFormat.date();

  private static void assertDetailedLibraryAttributes(Map<Field, String> expectedValues, DetailedLibrary lib) {
    assertAttribute(Field.ID, expectedValues, Long.toString(lib.getId()));
    assertAttribute(Field.NAME, expectedValues, lib.getName());
    assertAttribute(Field.ALIAS, expectedValues, lib.getAlias());
    assertAttribute(Field.BARCODE, expectedValues, lib.getIdentificationBarcode());
    assertAttribute(Field.DESCRIPTION, expectedValues, lib.getDescription());
    assertAttribute(Field.CREATION_DATE, expectedValues, dateFormatter.print(lib.getCreationDate().getTime()));
    assertAttribute(Field.PLATFORM, expectedValues, lib.getPlatformType().getKey());
    assertAttribute(Field.LIBRARY_TYPE, expectedValues, lib.getLibraryType().getDescription());
    assertAttribute(Field.DESIGN, expectedValues, nullValueOrGet(lib.getLibraryDesign(), LibraryDesign::getName, "(None)"));
    assertAttribute(Field.DESIGN_CODE, expectedValues, nullOrGet(lib.getLibraryDesignCode(), LibraryDesignCode::getCode));
    assertAttribute(Field.SELECTION, expectedValues,
        nullValueOrGet(lib.getLibrarySelectionType(), LibrarySelectionType::getName, "(None)"));
    assertAttribute(Field.STRATEGY, expectedValues,
        nullValueOrGet(lib.getLibraryStrategyType(), LibraryStrategyType::getName, "(None)"));
    assertAttribute(Field.INDEX_FAMILY, expectedValues,
        lib.getIndices() == null || lib.getIndices().isEmpty() ? "No indices" : lib.getIndices().get(0).getFamily().getName());
    if (expectedValues.containsKey(Field.INDEX_1)) {
      assertAttribute(Field.INDEX_1, expectedValues, getIndexString(lib, 1));
    }
    if (expectedValues.containsKey(Field.INDEX_2)) {
      assertAttribute(Field.INDEX_2, expectedValues, getIndexString(lib, 2));
    }
    assertAttribute(Field.QC_PASSED, expectedValues, nullOrToString(lib.getQcPassed()));
    assertAttribute(Field.LOW_QUALITY, expectedValues, Boolean.toString(lib.isLowQuality()));
    assertAttribute(Field.SIZE, expectedValues, nullOrToString(lib.getDnaSize()));
    assertAttribute(Field.VOLUME, expectedValues, nullOrToString(lib.getVolume()));
    assertAttribute(Field.DISCARDED, expectedValues, Boolean.toString(lib.isDiscarded()));
    assertAttribute(Field.LOCATION, expectedValues, lib.getLocationBarcode());
    assertAttribute(Field.KIT, expectedValues, nullOrGet(lib.getKitDescriptor(), KitDescriptor::getName));
    assertAttribute(Field.CONCENTRATION, expectedValues, nullOrToString(lib.getInitialConcentration()));
    assertAttribute(Field.ARCHIVED, expectedValues, lib.getArchived().toString());
  }

  private static String getIndexString(Library lib, int position) {
    Index index = lib.getIndices().stream().filter(i -> i.getPosition() == position).findFirst().orElse(null);
    return nullValueOrGet(index, Index::getLabel, "No index (null)");
  }

}
