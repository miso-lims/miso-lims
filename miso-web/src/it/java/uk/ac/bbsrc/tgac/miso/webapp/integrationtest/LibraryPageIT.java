package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.FormPageTestUtils.*;

import java.util.Map;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.Index;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LibraryPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.LibraryPage.Field;

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

  private static final DateTimeFormatter dateFormatter = ISODateTimeFormat.date();

  private static void assertDetailedLibraryAttributes(Map<Field, String> expectedValues, DetailedLibrary lib) {
    assertAttribute(Field.ID, expectedValues.get(Field.ID), Long.toString(lib.getId()));
    assertAttribute(Field.NAME, expectedValues.get(Field.NAME), lib.getName());
    assertAttribute(Field.ALIAS, expectedValues.get(Field.ALIAS), lib.getAlias());
    assertAttribute(Field.DESCRIPTION, expectedValues.get(Field.DESCRIPTION), lib.getDescription());
    assertAttribute(Field.CREATION_DATE, expectedValues.get(Field.CREATION_DATE), dateFormatter.print(lib.getCreationDate().getTime()));
    assertAttribute(Field.PLATFORM, expectedValues.get(Field.PLATFORM), lib.getPlatformType().getKey());
    assertAttribute(Field.LIBRARY_TYPE, expectedValues.get(Field.LIBRARY_TYPE), lib.getLibraryType().getDescription());
    assertAttribute(Field.DESIGN, expectedValues.get(Field.DESIGN),
        lib.getLibraryDesign() == null ? "(None)" : lib.getLibraryDesign().getName());
    assertAttribute(Field.DESIGN_CODE, expectedValues.get(Field.DESIGN_CODE),
        lib.getLibraryDesignCode() == null ? null : lib.getLibraryDesignCode().getCode());
    assertAttribute(Field.SELECTION, expectedValues.get(Field.SELECTION),
        lib.getLibrarySelectionType() == null ? "(None)" : lib.getLibrarySelectionType().getName());
    assertAttribute(Field.STRATEGY, expectedValues.get(Field.STRATEGY),
        lib.getLibraryStrategyType() == null ? "(None)" : lib.getLibraryStrategyType().getName());
    assertAttribute(Field.INDEX_FAMILY, expectedValues.get(Field.INDEX_FAMILY),
        lib.getIndices() == null || lib.getIndices().isEmpty() ? "No indices" : lib.getIndices().get(0).getFamily().getName());
    if (expectedValues.containsKey(Field.INDEX_1)) {
      assertAttribute(Field.INDEX_1, expectedValues.get(Field.INDEX_1), getIndexString(lib, 1));
    }
    if (expectedValues.containsKey(Field.INDEX_2)) {
      assertAttribute(Field.INDEX_2, expectedValues.get(Field.INDEX_2), getIndexString(lib, 2));
    }
    assertAttribute(Field.QC_PASSED,
        expectedValues.get(Field.QC_PASSED), lib.getQcPassed() == null ? null : lib.getQcPassed().toString());
    assertAttribute(Field.LOW_QUALITY, expectedValues.get(Field.LOW_QUALITY), Boolean.toString(lib.isLowQuality()));
    assertAttribute(Field.SIZE, expectedValues.get(Field.SIZE), lib.getDnaSize() == null ? null : lib.getDnaSize().toString());
    assertAttribute(Field.VOLUME, expectedValues.get(Field.VOLUME), lib.getVolume() == null ? null : lib.getVolume().toString());
    assertAttribute(Field.DISCARDED, expectedValues.get(Field.DISCARDED), Boolean.toString(lib.isDiscarded()));
    assertAttribute(Field.LOCATION, expectedValues.get(Field.LOCATION), lib.getLocationBarcode());
    assertAttribute(Field.KIT, expectedValues.get(Field.KIT), lib.getKitDescriptor() == null ? null : lib.getKitDescriptor().getName());
    assertAttribute(Field.CONCENTRATION, expectedValues.get(Field.CONCENTRATION),
        lib.getInitialConcentration() == null ? null : lib.getInitialConcentration().toString());
    assertAttribute(Field.ARCHIVED, expectedValues.get(Field.ARCHIVED), lib.getArchived().toString());
  }

  private static String getIndexString(Library lib, int position) {
    Index index = lib.getIndices().stream().filter(i -> i.getPosition() == position).findFirst().orElse(null);
    return index == null ? "No index (null)" : index.getLabel();
  }

}
