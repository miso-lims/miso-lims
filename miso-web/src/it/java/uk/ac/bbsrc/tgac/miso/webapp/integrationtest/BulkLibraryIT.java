package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryPage.LibColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTableSaveResult;

public class BulkLibraryIT extends AbstractIT {

  private static final Logger log = LoggerFactory.getLogger(BulkLibraryIT.class);

  private static final Set<String> commonColumns = Sets.newHashSet(LibColumns.NAME, LibColumns.ALIAS, LibColumns.ID_BARCODE,
      LibColumns.BOX_SEARCH, LibColumns.BOX_ALIAS, LibColumns.BOX_POSITION, LibColumns.DISCARDED, LibColumns.DESCRIPTION,
      LibColumns.GROUP_ID, LibColumns.GROUP_DESC, LibColumns.DESIGN, LibColumns.CODE, LibColumns.PLATFORM, LibColumns.LIBRARY_TYPE,
      LibColumns.SELECTION, LibColumns.STRATEGY, LibColumns.INDEX_FAMILY, LibColumns.INDEX_1, LibColumns.INDEX_2,
      LibColumns.KIT_DESCRIPTOR, LibColumns.QC_PASSED, LibColumns.SIZE, LibColumns.VOLUME, LibColumns.VOLUME_UNITS,
      LibColumns.CONCENTRATION, LibColumns.CONCENTRATION_UNITS);

  private static final Set<String> editColumns = Sets.newHashSet(LibColumns.RECEIVE_DATE, LibColumns.SAMPLE_ALIAS,
      LibColumns.SAMPLE_LOCATION, LibColumns.EFFECTIVE_GROUP_ID, LibColumns.CREATION_DATE);

  private static final Set<String> propagateColumns = Sets.newHashSet(LibColumns.SAMPLE_ALIAS, LibColumns.SAMPLE_LOCATION,
      LibColumns.EFFECTIVE_GROUP_ID, LibColumns.CREATION_DATE);

  private static final Set<String> receiptColumns = Sets.newHashSet(SamColumns.SAMPLE_TYPE,
      SamColumns.SCIENTIFIC_NAME, SamColumns.PROJECT, SamColumns.SUBPROJECT, SamColumns.EXTERNAL_NAME,
      SamColumns.IDENTITY_ALIAS, SamColumns.DONOR_SEX, SamColumns.CONSENT, SamColumns.SAMPLE_CLASS,
      SamColumns.TISSUE_ORIGIN, SamColumns.TISSUE_TYPE, SamColumns.PASSAGE_NUMBER,
      SamColumns.TIMES_RECEIVED, SamColumns.TUBE_NUMBER, SamColumns.TISSUE_MATERIAL,
      SamColumns.REGION, LibColumns.RECEIVE_DATE);

  private static final String NO_INDEX_FAMILY = "No indices";
  private static final String NO_INDEX = "No index";
  private static final String NO_DESIGN = "(None)";

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testEditSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(100001L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(commonColumns.size() + editColumns.size(), headings.size());
    for (String col : commonColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    for (String col : editColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testPropagateSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(100004L), 4);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(commonColumns.size() + propagateColumns.size(), headings.size());
    for (String col : commonColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    for (String col : propagateColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(4, table.getRowCount());

    assertEquals("LIBT_0001_Ly_P_1-1_D1", table.getText(LibColumns.SAMPLE_ALIAS, 0));
    assertEquals("Unknown", table.getText(LibColumns.QC_PASSED, 0));
  }

  @Test
  public void testReceiveSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    // test for gDNA aliquot parent
    BulkLibraryPage page = BulkLibraryPage.getForReceive(getDriver(), getBaseUrl(), 2, null, 15);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(commonColumns.size() + receiptColumns.size(), headings.size());
    for (String col : commonColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    for (String col : receiptColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(2, table.getRowCount());

    assertEquals("Unknown", table.getText(LibColumns.QC_PASSED, 0));

    // test extra field for RNA subtypes
    BulkLibraryPage page2 = BulkLibraryPage.getForReceive(getDriver(), getBaseUrl(), 2, null, 19);
    HandsOnTable table2 = page2.getTable();
    List<String> headings2 = table2.getColumnHeadings();
    assertEquals(commonColumns.size() + receiptColumns.size() + 1, headings2.size());
    assertTrue("Check for column: '" + SamColumns.DNASE_TREATED + "'", headings2.contains(SamColumns.DNASE_TREATED));
  }

  @Test
  public void testPropagateDropdowns() throws Exception {
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(100004L), 1);
    HandsOnTable table = page.getTable();

    Set<String> designs = table.getDropdownOptions(LibColumns.DESIGN, 0);
    assertEquals(7, designs.size());
    assertTrue(designs.contains("WG"));
    assertTrue(designs.contains("AS"));

    Set<String> codes = table.getDropdownOptions(LibColumns.CODE, 0);
    assertEquals(9, codes.size());
    assertTrue(codes.contains("EX"));
    assertTrue(codes.contains("MR"));

    Set<String> platforms = table.getDropdownOptions(LibColumns.PLATFORM, 0);
    assertEquals(6, platforms.size()); // All members of PlatformType
    assertTrue(platforms.contains("Illumina"));
    assertTrue(platforms.contains("PacBio"));

    Set<String> selections = table.getDropdownOptions(LibColumns.SELECTION, 0);
    assertEquals(27, selections.size());
    assertTrue(selections.contains("cDNA"));
    assertTrue(selections.contains("PCR"));

    Set<String> strategies = table.getDropdownOptions(LibColumns.STRATEGY, 0);
    assertEquals(20, strategies.size());
    assertTrue(strategies.contains("AMPLICON"));
    assertTrue(strategies.contains("OTHER"));

    Set<String> qcValues = table.getDropdownOptions(LibColumns.QC_PASSED, 0);
    assertEquals(3, qcValues.size());
    assertTrue(qcValues.contains("True"));
    assertTrue(qcValues.contains("False"));
    assertTrue(qcValues.contains("Unknown"));
  }

  @Test
  public void testPropagateDependencyCells() {
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(100004L), 1);
    HandsOnTable table = page.getTable();

    // verify empty options; library type, index family, and kit depend on platform
    Set<String> types = table.getDropdownOptions(LibColumns.LIBRARY_TYPE, 0);
    assertTrue(types.isEmpty());
    Set<String> families = table.getDropdownOptions(LibColumns.INDEX_FAMILY, 0);
    // may contain an empty String, which doesn't do any harm
    assertTrue(families.isEmpty() || (families.size() == 1 && isStringEmptyOrNull(families.iterator().next())));
    Set<String> kits = table.getDropdownOptions(LibColumns.KIT_DESCRIPTOR, 0);
    assertTrue(kits.isEmpty());

    // select platform
    table.enterText(LibColumns.PLATFORM, 0, "Illumina");

    // verify platform-dependant options
    types = table.getDropdownOptions(LibColumns.LIBRARY_TYPE, 0);
    assertEquals(4, types.size());
    assertTrue(types.contains("Paired End"));
    assertTrue(types.contains("Single End"));

    families = table.getDropdownOptions(LibColumns.INDEX_FAMILY, 0);
    assertEquals(4, families.size());
    assertTrue(families.contains(NO_INDEX_FAMILY));
    assertTrue(families.contains("Single Index 6bp"));
    assertTrue(families.contains("Dual Index 6bp"));
    assertTrue(families.contains("Similar Index Pair"));

    kits = table.getDropdownOptions(LibColumns.KIT_DESCRIPTOR, 0);
    assertEquals(2, kits.size());
    assertTrue(kits.contains("Test Kit"));

    // indices depend on index family
    Set<String> index1s = table.getDropdownOptions(LibColumns.INDEX_1, 0);
    assertEquals(1, index1s.size());
    assertTrue(index1s.contains(NO_INDEX));
    Set<String> index2s = table.getDropdownOptions(LibColumns.INDEX_2, 0);
    assertEquals(1, index2s.size());
    assertTrue(index2s.contains(NO_INDEX));

    // single index family
    table.enterText(LibColumns.INDEX_FAMILY, 0, "Single Index 6bp");

    index1s = table.getDropdownOptions(LibColumns.INDEX_1, 0);
    assertTrue(index1s.contains("Index 01 (AAAAAA)"));
    assertTrue(index1s.contains("Index 02 (CCCCCC)"));
    index2s = table.getDropdownOptions(LibColumns.INDEX_2, 0);
    assertEquals(1, index2s.size());
    assertTrue(index2s.contains(NO_INDEX));

    // dual index
    table.enterText(LibColumns.INDEX_FAMILY, 0, "Dual Index 6bp");

    index1s = table.getDropdownOptions(LibColumns.INDEX_1, 0);
    assertEquals(4, index1s.size());
    assertTrue(index1s.contains("A01 (AAACCC)"));
    assertTrue(index1s.contains("A02 (CCCAAA)"));
    index2s = table.getDropdownOptions(LibColumns.INDEX_2, 0);
    assertEquals(5, index2s.size());
    assertTrue(index2s.contains(NO_INDEX));
    assertTrue(index2s.contains("B01 (AAATTT)"));
    assertTrue(index2s.contains("B02 (CCCGGG)"));
  }

  @Test
  public void testReadOnlyCells() {
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(100004L), 1);
    HandsOnTable table = page.getTable();

    assertFalse(table.isWritable(LibColumns.NAME, 0));
    assertFalse(table.isWritable(LibColumns.SAMPLE_ALIAS, 0));

    assertTrue(table.isWritable(LibColumns.CODE, 0));
    assertTrue(table.isWritable(LibColumns.SELECTION, 0));
    assertTrue(table.isWritable(LibColumns.STRATEGY, 0));

    table.enterText(LibColumns.DESIGN, 0, "WG");
    assertFalse(table.isWritable(LibColumns.CODE, 0));
    assertFalse(table.isWritable(LibColumns.SELECTION, 0));
    assertFalse(table.isWritable(LibColumns.STRATEGY, 0));
  }

  @Test
  public void testPropagate() {
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(100004L), 1);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibColumns.ID_BARCODE, "LIBT_PROP1");
    attrs.put(LibColumns.DESCRIPTION, "LIBT propagate test");
    attrs.put(LibColumns.DESIGN, "WG");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(LibColumns.INDEX_1, "A01 (AAACCC)");
    attrs.put(LibColumns.INDEX_2, "B01 (AAATTT)");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_PASSED, "True");
    attrs.put(LibColumns.SIZE, "123");
    attrs.put(LibColumns.VOLUME, "6.66");
    attrs.put(LibColumns.CONCENTRATION, "12.57");

    fillRow(table, 0, attrs);

    // should be set by selecting Design
    attrs.put(LibColumns.CODE, "WG");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");

    assertColumnValues(table, 0, attrs, "pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    DetailedLibrary saved = (DetailedLibrary) getSession().get(LibraryImpl.class, newId);
    assertDetailedLibraryAttributes(attrs, saved);
  }

  @Test
  public void testPropagateTwoMinimal() {
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(100004L), 2);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibColumns.CODE, "WG");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "CAGE");
    attrs.put(LibColumns.STRATEGY, "AMPLICON");
    attrs.put(LibColumns.INDEX_FAMILY, NO_INDEX_FAMILY);
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.SIZE, "205");
    attrs.put(LibColumns.QC_PASSED, "Unknown");

    fillRow(table, 0, attrs);
    fillRow(table, 1, attrs);
    table.enterText(LibColumns.SIZE, 1, "206");

    assertColumnValues(table, 0, attrs, "pre-save");
    attrs.put(LibColumns.SIZE, "206");
    assertColumnValues(table, 1, attrs, "pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 1, attrs, "pre-save");
    attrs.put(LibColumns.SIZE, "205");
    assertColumnValues(table, 0, attrs, "pre-save");

    Long newId1 = getSavedId(table, 0);
    Long newId2 = getSavedId(table, 1);
    DetailedLibrary saved1 = (DetailedLibrary) getSession().get(LibraryImpl.class, newId1);
    DetailedLibrary saved2 = (DetailedLibrary) getSession().get(LibraryImpl.class, newId2);
    assertDetailedLibraryAttributes(attrs, saved1);
    attrs.put(LibColumns.SIZE, "205");
    assertDetailedLibraryAttributes(attrs, saved2);
  }

  @Test
  public void testEditChangeValues() {
    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(100001L));
    HandsOnTable table = page.getTable();

    // test initial values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibColumns.NAME, "LIB100001");
    attrs.put(LibColumns.ALIAS, "LIBT_0001_Ly_P_PE_251_WG");
    attrs.put(LibColumns.SAMPLE_ALIAS, "LIBT_0001_Ly_P_1-1_D1");
    attrs.put(LibColumns.ID_BARCODE, "libbar100001");
    attrs.put(LibColumns.DESCRIPTION, "libdesc100001");
    attrs.put(LibColumns.DESIGN, NO_DESIGN);
    attrs.put(LibColumns.CODE, "WG");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");
    attrs.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(LibColumns.INDEX_1, "A01 (AAACCC)");
    attrs.put(LibColumns.INDEX_2, "B01 (AAATTT)");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_PASSED, "False");
    attrs.put(LibColumns.SIZE, "251");
    attrs.put(LibColumns.VOLUME, "2.5");
    attrs.put(LibColumns.CONCENTRATION, "10.0");
    assertColumnValues(table, 0, attrs, "loaded");

    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(LibColumns.ALIAS, "LIBT_0001_Ly_P_PE_241_WG");
    changes.put(LibColumns.ID_BARCODE, "changed100001");
    changes.put(LibColumns.DESCRIPTION, "changed100001");
    changes.put(LibColumns.DESIGN, "EX");
    changes.put(LibColumns.LIBRARY_TYPE, "Single End");
    changes.put(LibColumns.INDEX_FAMILY, "Single Index 6bp");
    changes.put(LibColumns.INDEX_1, "Index 01 (AAAAAA)");
    changes.put(LibColumns.KIT_DESCRIPTOR, "Test Kit Two");
    changes.put(LibColumns.QC_PASSED, "True");
    changes.put(LibColumns.SIZE, "241");
    changes.put(LibColumns.VOLUME, "1.88");
    changes.put(LibColumns.CONCENTRATION, "12.34");
    fillRow(table, 0, changes);

    // unchanged
    changes.put(LibColumns.NAME, "LIB100001");
    changes.put(LibColumns.SAMPLE_ALIAS, "LIBT_0001_Ly_P_1-1_D1");
    changes.put(LibColumns.PLATFORM, "Illumina");

    // set based on other changes
    changes.put(LibColumns.CODE, "EX");
    changes.put(LibColumns.SELECTION, "Hybrid Selection");
    changes.put(LibColumns.STRATEGY, "WXS");
    changes.put(LibColumns.INDEX_2, NO_INDEX);
    assertColumnValues(table, 0, changes, "changes pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, changes, "post-save");
    DetailedLibrary lib = (DetailedLibrary) getSession().get(LibraryImpl.class, 100001L);
    assertDetailedLibraryAttributes(changes, lib);
  }

  @Test
  public void testEditValueRemovals() {
    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(100002L));
    HandsOnTable table = page.getTable();

    // test initial values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibColumns.NAME, "LIB100002");
    attrs.put(LibColumns.ALIAS, "LIBT_0001_Ly_P_PE_252_WG");
    attrs.put(LibColumns.SAMPLE_ALIAS, "LIBT_0001_Ly_P_1-1_D1");
    attrs.put(LibColumns.ID_BARCODE, "libbar100002");
    attrs.put(LibColumns.DESCRIPTION, "libdesc100002");
    attrs.put(LibColumns.DESIGN, "WG");
    attrs.put(LibColumns.CODE, "WG");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");
    attrs.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(LibColumns.INDEX_1, "A02 (CCCAAA)");
    attrs.put(LibColumns.INDEX_2, "B02 (CCCGGG)");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_PASSED, "False");
    attrs.put(LibColumns.SIZE, "252");
    attrs.put(LibColumns.VOLUME, "4.0");
    attrs.put(LibColumns.CONCENTRATION, "6.3");
    assertColumnValues(table, 0, attrs, "loaded");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(LibColumns.ID_BARCODE, "");
    changes.put(LibColumns.DESCRIPTION, "");
    changes.put(LibColumns.DESIGN, NO_DESIGN);
    changes.put(LibColumns.CODE, "CH");
    changes.put(LibColumns.SELECTION, "cDNA");
    changes.put(LibColumns.STRATEGY, "CLONE");
    changes.put(LibColumns.INDEX_FAMILY, NO_INDEX_FAMILY);
    changes.put(LibColumns.QC_PASSED, "True");
    changes.put(LibColumns.SIZE, "241");
    changes.put(LibColumns.VOLUME, "1.88");
    changes.put(LibColumns.CONCENTRATION, "12.34");
    fillRow(table, 0, changes);

    // set based on other changes
    changes.put(LibColumns.INDEX_1, NO_INDEX);
    changes.put(LibColumns.INDEX_2, NO_INDEX);

    // unchanged
    attrs.forEach((key, val) -> {
      if (!changes.containsKey(key)) {
        changes.put(key, val);
      }
    });
    assertColumnValues(table, 0, changes, "changes pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, changes, "post-save");
    DetailedLibrary lib = (DetailedLibrary) getSession().get(LibraryImpl.class, 100002L);
    assertDetailedLibraryAttributes(changes, lib);
  }

  @Test
  public void testEditValueAdditions() {
    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(100003L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibColumns.NAME, "LIB100003");
    attrs.put(LibColumns.ALIAS, "LIBT_0001_Ly_P_PE_253_WG");
    attrs.put(LibColumns.SAMPLE_ALIAS, "LIBT_0001_Ly_P_1-1_D1");
    attrs.put(LibColumns.ID_BARCODE, null);
    attrs.put(LibColumns.DESCRIPTION, null);
    attrs.put(LibColumns.DESIGN, NO_DESIGN);
    attrs.put(LibColumns.CODE, "WG");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");
    attrs.put(LibColumns.INDEX_FAMILY, NO_INDEX_FAMILY);
    attrs.put(LibColumns.INDEX_1, NO_INDEX);
    attrs.put(LibColumns.INDEX_2, NO_INDEX);
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_PASSED, "Unknown");
    attrs.put(LibColumns.SIZE, null);
    attrs.put(LibColumns.VOLUME, null);
    attrs.put(LibColumns.CONCENTRATION, null);
    assertColumnValues(table, 0, attrs, "loaded");

    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(LibColumns.ID_BARCODE, "changed_bar_100003");
    changes.put(LibColumns.DESCRIPTION, "changed_desc_100003");
    changes.put(LibColumns.DESIGN, "TS (PCR)");
    changes.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    changes.put(LibColumns.INDEX_1, "A04 (TTTGGG)");
    changes.put(LibColumns.INDEX_2, "B04 (TTTAAA)");
    changes.put(LibColumns.QC_PASSED, "True");
    changes.put(LibColumns.SIZE, "253");
    changes.put(LibColumns.VOLUME, "18.0");
    changes.put(LibColumns.CONCENTRATION, "7.6");
    fillRow(table, 0, changes);

    // changed because of design
    changes.put(LibColumns.CODE, "TS");
    changes.put(LibColumns.SELECTION, "PCR");
    changes.put(LibColumns.STRATEGY, "AMPLICON");

    // unchanged
    attrs.forEach((key, val) -> {
      if (!changes.containsKey(key)) {
        changes.put(key, val);
      }
    });
    assertColumnValues(table, 0, changes, "changes pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, changes, "post-save");
    DetailedLibrary lib = (DetailedLibrary) getSession().get(LibraryImpl.class, 100003L);
    assertDetailedLibraryAttributes(changes, lib);
  }

  @Test
  public void testReceipt() {
    BulkLibraryPage page = BulkLibraryPage.getForReceive(getDriver(), getBaseUrl(), 1, null, 15);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.PROJECT, "LIBT");
    attrs.put(SamColumns.EXTERNAL_NAME, "lkjh");
    attrs.put(SamColumns.DONOR_SEX, "Unknown");
    attrs.put(SamColumns.TISSUE_ORIGIN, "Bn (Brain)");
    attrs.put(SamColumns.TISSUE_TYPE, "P (Primary tumour)");
    attrs.put(SamColumns.TIMES_RECEIVED, "3");
    attrs.put(SamColumns.TUBE_NUMBER, "4");
    attrs.put(LibColumns.ID_BARCODE, "LIBT_RCV1");
    attrs.put(LibColumns.DESCRIPTION, "LIBT receive test");
    attrs.put(LibColumns.RECEIVE_DATE, "2017-10-12");
    attrs.put(LibColumns.DESIGN, "WG");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(LibColumns.INDEX_1, "A01 (AAACCC)");
    attrs.put(LibColumns.INDEX_2, "B01 (AAATTT)");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_PASSED, "True");
    attrs.put(LibColumns.SIZE, "123");
    attrs.put(LibColumns.VOLUME, "6.66");
    attrs.put(LibColumns.CONCENTRATION, "12.57");

    fillRow(table, 0, attrs);

    // should be set by selecting Design
    attrs.put(LibColumns.CODE, "WG");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");

    assertColumnValues(table, 0, attrs, "pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    DetailedLibrary saved = (DetailedLibrary) getSession().get(LibraryImpl.class, newId);
    assertDetailedLibraryAttributes(attrs, saved);
    assertParentSampleAttributes(attrs, saved);
  }

  @Test
  public void testPropagateToEditToPropagate() {
    // propagate sample to library
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(100004L), 1);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibColumns.CODE, "WG");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "CAGE");
    attrs.put(LibColumns.STRATEGY, "AMPLICON");
    attrs.put(LibColumns.INDEX_FAMILY, NO_INDEX_FAMILY);
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.SIZE, "207");
    attrs.put(LibColumns.QC_PASSED, "Unknown");
    fillRow(table, 0, attrs);
    assertColumnValues(table, 0, attrs, "pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "pre-save");

    long newId = getSavedId(table, 0);
    DetailedLibrary saved = (DetailedLibrary) getSession().get(LibraryImpl.class, newId);
    assertDetailedLibraryAttributes(attrs, saved);

    // chain edit library
    BulkLibraryPage page2 = page.chainEdit();
    HandsOnTable table2 = page2.getTable();
    assertColumnValues(table2, 0, attrs, "reload for edit");

    table2.enterText(LibColumns.SELECTION, 0, "cDNA");
    attrs.put(LibColumns.SELECTION, "cDNA");
    saveAndAssertSuccess(table2);
    assertColumnValues(table2, 0, attrs, "edit post-save");
    DetailedLibrary saved2 = (DetailedLibrary) getSession().get(LibraryImpl.class, newId);
    assertDetailedLibraryAttributes(attrs, saved2);

    // chain propagate library to dilution
    assertNotNull(page2.chainPropagateDilutions());
  }

  @Test
  public void testEditTwice() {
    // edit once
    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(100004L));
    HandsOnTable table = page.getTable();
    assertEquals("libdesc100004", table.getText(LibColumns.DESCRIPTION, 0));
    String descEdit1 = "changed once";
    table.enterText(LibColumns.DESCRIPTION, 0, descEdit1);
    assertEquals(descEdit1, table.getText(LibColumns.DESCRIPTION, 0));

    saveAndAssertSuccess(table);
    DetailedLibrary saved = (DetailedLibrary) getSession().get(LibraryImpl.class, 100004L);
    assertEquals(descEdit1, saved.getDescription());

    // edit twice
    BulkLibraryPage page2 = page.chainEdit();
    HandsOnTable table2 = page2.getTable();
    assertEquals(descEdit1, table2.getText(LibColumns.DESCRIPTION, 0));
    String descEdit2 = "changed twice";
    table2.enterText(LibColumns.DESCRIPTION, 0, descEdit2);
    assertEquals(descEdit2, table2.getText(LibColumns.DESCRIPTION, 0));

    saveAndAssertSuccess(table2);
    DetailedLibrary saved2 = (DetailedLibrary) getSession().get(LibraryImpl.class, 100004L);
    assertEquals(descEdit2, saved2.getDescription());
  }

  @Test
  public void testSortByBoxPosition() {
    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(204L, 205L, 206L));
    HandsOnTable table = page.getTable();
    
    final class Libs {
      final static String C06 = "C06";
      final static String A07 = "A07";
      final static String B05 = "B05";
    }

    Map<Integer, String> initial = new HashMap<>();
    initial.put(0, Libs.C06);
    initial.put(1, Libs.A07);
    initial.put(2, Libs.B05);

    initial.forEach((k, v) -> assertTrue("initial value " + v + " in row " + k, table.getText(LibColumns.SAMPLE_LOCATION, k).endsWith(v)));
    
    Map<Integer, String> sortCols = new HashMap<>();
    sortCols.put(0, Libs.B05);
    sortCols.put(1, Libs.C06);
    sortCols.put(2, Libs.A07);
    
    page.sortBySampleLocationColumns();
    sortCols.forEach((k, v) -> assertTrue("sortCols value " + v + " in row " + k, table.getText(LibColumns.SAMPLE_LOCATION, k).endsWith(v)));
    
    Map<Integer, String> sortRows = new HashMap<>();
    sortRows.put(0, Libs.A07);
    sortRows.put(1, Libs.B05);
    sortRows.put(2, Libs.C06);
    
    page.sortBySampleLocationRows();
    sortRows.forEach((k, v) -> assertTrue(table.getText(LibColumns.SAMPLE_LOCATION, k).endsWith(v)));
  }

  @Test
  public void testAddToBox() {
    Long libId = 100005L;
    DetailedLibrary before = (DetailedLibrary) getSession().get(LibraryImpl.class, libId);
    assertNull(before.getBox());
    assertNull(before.getBoxPosition());

    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(libId));
    HandsOnTable table = page.getTable();
    table.enterText(LibColumns.BOX_SEARCH, 0, "BOX100001");
    table.waitForSearch(LibColumns.BOX_ALIAS, 0);
    assertEquals("Bulk Boxables Test", table.getText(LibColumns.BOX_ALIAS, 0));
    assertTrue(isStringEmptyOrNull(table.getText(LibColumns.BOX_POSITION, 0)));
    assertTrue(table.getInvalidCells(0).contains(LibColumns.BOX_POSITION));
    table.enterText(LibColumns.BOX_POSITION, 0, "A01");
    assertFalse(table.getInvalidCells(0).contains(LibColumns.BOX_POSITION));
    saveAndAssertSuccess(table);

    DetailedLibrary after = (DetailedLibrary) getSession().get(LibraryImpl.class, libId);
    assertNotNull(after.getBox());
    assertEquals("BOX100001", after.getBox().getName());
    assertEquals("A01", after.getBoxPosition());
  }

  @Test
  public void testRemoveFromBox() {
    Long libId = 100006L;
    DetailedLibrary before = (DetailedLibrary) getSession().get(LibraryImpl.class, libId);
    assertNotNull(before.getBox());
    assertEquals("BOX100001", before.getBox().getName());
    assertEquals("A02", before.getBoxPosition());

    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(libId));
    HandsOnTable table = page.getTable();
    table.clearField(LibColumns.BOX_ALIAS, 0);
    saveAndAssertSuccess(table);

    DetailedLibrary after = (DetailedLibrary) getSession().get(LibraryImpl.class, libId);
    assertNull(after.getBox());
    assertNull(after.getBoxPosition());
  }

  @Test
  public void testDiscardFromBox() {
    Long libId = 100007L;
    DetailedLibrary before = (DetailedLibrary) getSession().get(LibraryImpl.class, libId);
    assertNotNull(before.getBox());
    assertEquals("BOX100001", before.getBox().getName());
    assertEquals("A03", before.getBoxPosition());
    assertFalse(before.isDiscarded());

    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(libId));
    HandsOnTable table = page.getTable();
    table.enterText(LibColumns.DISCARDED, 0, "True");
    assertTrue(table.getInvalidCells(0).contains(LibColumns.DISCARDED));
    table.clearField(LibColumns.BOX_ALIAS, 0);
    assertFalse(table.getInvalidCells(0).contains(LibColumns.DISCARDED));
    saveAndAssertSuccess(table);

    DetailedLibrary after = (DetailedLibrary) getSession().get(LibraryImpl.class, libId);
    assertNull(after.getBox());
    assertNull(after.getBoxPosition());
    assertTrue(after.isDiscarded());
  }

  @Test
  public void testUndiscardIntoBox() {
    Long libId = 100008L;
    DetailedLibrary before = (DetailedLibrary) getSession().get(LibraryImpl.class, libId);
    assertNull(before.getBox());
    assertNull(before.getBoxPosition());
    assertTrue(before.isDiscarded());

    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(libId));
    HandsOnTable table = page.getTable();
    table.enterText(LibColumns.BOX_SEARCH, 0, "BOX100001");
    table.waitForSearch(LibColumns.BOX_ALIAS, 0);
    assertEquals("Bulk Boxables Test", table.getText(LibColumns.BOX_ALIAS, 0));
    table.enterText(LibColumns.BOX_POSITION, 0, "B01");
    assertEquals("B01", table.getText(LibColumns.BOX_POSITION, 0));
    assertTrue(table.getInvalidCells(0).contains(LibColumns.DISCARDED));
    table.enterText(LibColumns.DISCARDED, 0, "False");
    assertFalse(table.getInvalidCells(0).contains(LibColumns.DISCARDED));
    saveAndAssertSuccess(table);

    DetailedLibrary after = (DetailedLibrary) getSession().get(LibraryImpl.class, libId);
    assertNotNull(after.getBox());
    assertEquals("BOX100001", after.getBox().getName());
    assertEquals("B01", after.getBoxPosition());
    assertFalse(after.isDiscarded());
  }

  private void fillRow(HandsOnTable table, int rowNum, Map<String, String> attributes) {
    attributes.forEach((key, val) -> table.enterText(key, rowNum, val));
  }

  private long getSavedId(HandsOnTable table, int rowNum) {
    return Long.valueOf(table.getText(LibColumns.NAME, 0).substring(3, table.getText(LibColumns.NAME, 0).length()));
  }

  public void assertPlainLibraryAttributes(Map<String, String> attributes, Library library) {
    testLibraryAttribute(LibColumns.NAME, attributes, library, Library::getName);
    testLibraryAttribute(LibColumns.ALIAS, attributes, library, Library::getAlias);
    testLibraryAttribute(LibColumns.DESCRIPTION, attributes, library, Library::getDescription);
    testLibraryAttribute(LibColumns.PLATFORM, attributes, library, lib -> lib.getPlatformType().getKey());
    testLibraryAttribute(LibColumns.ID_BARCODE, attributes, library, Library::getIdentificationBarcode);
    testLibraryAttribute(LibColumns.LIBRARY_TYPE, attributes, library, lib -> lib.getLibraryType().getDescription());
    testLibraryAttribute(LibColumns.INDEX_FAMILY, attributes, library, lib -> {
      if (lib.getIndices() == null || lib.getIndices().isEmpty()) {
        return null;
      }
      return lib.getIndices().get(0).getFamily().getName();
    });
    testLibraryAttribute(LibColumns.INDEX_1, attributes, library, indexGetter(1));
    testLibraryAttribute(LibColumns.INDEX_2, attributes, library, indexGetter(2));
    testLibraryAttribute(LibColumns.QC_PASSED, attributes, library, lib -> getQcPassedString(lib.getQcPassed()));
    testLibraryAttribute(LibColumns.SIZE, attributes, library, lib -> {
      return lib.getDnaSize() == null ? null : lib.getDnaSize().toString();
    });
    testLibraryAttribute(LibColumns.VOLUME, attributes, library, lib -> lib.getVolume().toString());
    testLibraryAttribute(LibColumns.CONCENTRATION, attributes, library, lib -> lib.getInitialConcentration().toString());
    testLibraryAttribute(LibColumns.RECEIVE_DATE, attributes, library, lib -> {
      return lib.getReceivedDate() == null ? null : LimsUtils.formatDate(lib.getReceivedDate());
    });
  }

  private void assertDetailedLibraryAttributes(Map<String, String> attributes, DetailedLibrary library) {
    assertPlainLibraryAttributes(attributes, library);

    testLibraryAttribute(LibColumns.DESIGN, attributes, library, lib -> {
      return lib.getLibraryDesign() == null ? null : lib.getLibraryDesign().getName();
    });
    testLibraryAttribute(LibColumns.KIT_DESCRIPTOR, attributes, library, lib -> lib.getKitDescriptor().getName());
    testLibraryAttribute(LibColumns.CODE, attributes, library, lib -> lib.getLibraryDesignCode().getCode());
    testLibraryAttribute(LibColumns.SELECTION, attributes, library, lib -> lib.getLibrarySelectionType().getName());
    testLibraryAttribute(LibColumns.STRATEGY, attributes, library, lib -> lib.getLibraryStrategyType().getName());
  }

  private void assertParentSampleAttributes(Map<String, String> attributes, DetailedLibrary library) {
    testLibraryAttribute(SamColumns.SAMPLE_TYPE, attributes, library, lib -> lib.getSample().getSampleType());
    testLibraryAttribute(SamColumns.SCIENTIFIC_NAME, attributes, library, lib -> lib.getSample().getScientificName());
    testLibraryAttribute(SamColumns.PROJECT, attributes, library, lib -> lib.getSample().getProject().getShortName());
    testLibraryAttribute(SamColumns.EXTERNAL_NAME, attributes, library, lib -> identityGetter.apply(lib).getExternalName());
    testLibraryAttribute(SamColumns.DONOR_SEX, attributes, library, lib -> identityGetter.apply(lib).getDonorSex().getLabel());
    testLibraryAttribute(SamColumns.TISSUE_ORIGIN, attributes, library,
        lib -> tissueGetter.apply(lib).getTissueOrigin().getItemLabel());
    testLibraryAttribute(SamColumns.TISSUE_TYPE, attributes, library,
        lib -> tissueGetter.apply(lib).getTissueType().getItemLabel());
    testLibraryAttribute(SamColumns.TIMES_RECEIVED, attributes, library,
        lib -> tissueGetter.apply(lib).getTimesReceived().toString());
    testLibraryAttribute(SamColumns.TUBE_NUMBER, attributes, library,
        lib -> tissueGetter.apply(lib).getTubeNumber().toString());
  }

  private final Function<Library, SampleIdentity> identityGetter = library -> {
    DetailedSample sam = (DetailedSample) library.getSample();
    do {
      sam = sam.getParent();
    } while (sam.getParent() != null);
    if (!isIdentitySample(sam)) {
      throw new IllegalStateException("library has no Identity");
    }
    return (SampleIdentity) deproxify(sam);
  };

  private final Function<Library, SampleTissue> tissueGetter = library -> {
    DetailedSample sam = (DetailedSample) library.getSample();
    do {
      sam = sam.getParent();
      if (sam == null) {
        throw new IllegalStateException("Library has no Tissue");
      }
    } while (!isTissueSample(sam));
    return (SampleTissue) deproxify(sam);
  };

  private Function<Library, String> indexGetter(int position) {
    return lib -> {
      if (lib.getIndices() == null || lib.getIndices().size() < position) {
        return null;
      }
      return lib.getIndices().stream().filter(index -> index.getPosition() == position).findFirst().get().getLabel();
    };
  }

  private <T> void testLibraryAttribute(String column, Map<String, String> attributes, T object, Function<T, String> getter) {
    if (attributes.containsKey(column)) {
      String objectAttribute = getter.apply(object);
      String tableAttribute = cleanNullValues(column, attributes.get(column));
      if (tableAttribute == null) {
        assertTrue(String.format("persisted attribute expected empty '%s'", column), isStringEmptyOrNull(objectAttribute));
      } else {
        assertEquals(String.format("persisted attribute '%s'", column), tableAttribute, objectAttribute);
      }
    }
  }

  private String cleanNullValues(String key, String value) {
    switch (key) {
    case LibColumns.INDEX_FAMILY:
      return NO_INDEX_FAMILY.equals(value) ? null : value;
    case LibColumns.INDEX_1:
    case LibColumns.INDEX_2:
      return NO_INDEX.equals(value) ? null : value;
    case LibColumns.DESIGN:
      return NO_DESIGN.equals(value) ? null : value;
    default:
      return value == null || value.isEmpty() ? null : value;
    }
  }

  private void saveAndAssertSuccess(HandsOnTable table) {
    HandsOnTableSaveResult result = table.save();

    if (result.getItemsSaved() != table.getRowCount()) {
      log.error(result.printSummary());
    }

    assertEquals("Save count", table.getRowCount(), result.getItemsSaved());
    assertTrue("Server error messages", result.getServerErrors().isEmpty());
    assertTrue("Save error messages", result.getSaveErrors().isEmpty());

    for (int i = 0; i < table.getRowCount(); i++) {
      assertTrue("Library name generation", table.getText(LibColumns.NAME, i).contains("LIB"));
      assertTrue("Library alias generation", !isStringEmptyOrNull(table.getText(LibColumns.ALIAS, i)));
    }
  }

}
