package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils.assertColumnValues;

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
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTableSaveResult;

public class BulkLibraryIT extends AbstractIT {

  private static final Logger log = LoggerFactory.getLogger(BulkLibraryIT.class);

  private static final Set<String> commonColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.ID_BARCODE,
      Columns.DESCRIPTION, Columns.GROUP_ID, Columns.GROUP_DESC, Columns.DESIGN, Columns.CODE, Columns.PLATFORM, Columns.LIBRARY_TYPE,
      Columns.SELECTION, Columns.STRATEGY, Columns.INDEX_FAMILY, Columns.INDEX_1, Columns.INDEX_2, Columns.KIT_DESCRIPTOR,
      Columns.QC_PASSED, Columns.SIZE, Columns.VOLUME, Columns.CONCENTRATION);

  private static final Set<String> editColumns = Sets.newHashSet(Columns.RECEIVE_DATE, Columns.SAMPLE_ALIAS, Columns.SAMPLE_LOCATION);

  private static final Set<String> propagateColumns = Sets.newHashSet(Columns.SAMPLE_ALIAS, Columns.SAMPLE_LOCATION);

  private static final Set<String> receiptColumns = Sets.newHashSet(BulkSamplePage.Columns.SAMPLE_TYPE,
      BulkSamplePage.Columns.SCIENTIFIC_NAME, BulkSamplePage.Columns.PROJECT, BulkSamplePage.Columns.EXTERNAL_NAME,
      BulkSamplePage.Columns.IDENTITY_ALIAS, BulkSamplePage.Columns.DONOR_SEX, BulkSamplePage.Columns.SAMPLE_CLASS,
      BulkSamplePage.Columns.TISSUE_ORIGIN, BulkSamplePage.Columns.TISSUE_TYPE, BulkSamplePage.Columns.PASSAGE_NUMBER,
      BulkSamplePage.Columns.TIMES_RECEIVED, BulkSamplePage.Columns.TUBE_NUMBER, BulkSamplePage.Columns.TISSUE_MATERIAL,
      BulkSamplePage.Columns.REGION, Columns.RECEIVE_DATE);

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

    assertEquals("LIBT_0001_Ly_P_1-1_D1", table.getText(Columns.SAMPLE_ALIAS, 0));
    assertEquals("Unknown", table.getText(Columns.QC_PASSED, 0));
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

    assertEquals("Unknown", table.getText(Columns.QC_PASSED, 0));

    // test extra field for RNA subtypes
    BulkLibraryPage page2 = BulkLibraryPage.getForReceive(getDriver(), getBaseUrl(), 2, null, 19);
    HandsOnTable table2 = page2.getTable();
    List<String> headings2 = table2.getColumnHeadings();
    assertEquals(commonColumns.size() + receiptColumns.size() + 1, headings2.size());
    assertTrue("Check for column: '" + BulkSamplePage.Columns.DNASE_TREATED + "'", headings2.contains(BulkSamplePage.Columns.DNASE_TREATED));
  }

  @Test
  public void testPropagateDropdowns() throws Exception {
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(100004L), 1);
    HandsOnTable table = page.getTable();

    List<String> designs = table.getDropdownOptions(Columns.DESIGN, 0);
    assertEquals(7, designs.size());
    assertTrue(designs.contains("WG"));
    assertTrue(designs.contains("AS"));

    List<String> codes = table.getDropdownOptions(Columns.CODE, 0);
    assertEquals(9, codes.size());
    assertTrue(codes.contains("EX"));
    assertTrue(codes.contains("MR"));

    List<String> platforms = table.getDropdownOptions(Columns.PLATFORM, 0);
    assertEquals(2, platforms.size());
    assertTrue(platforms.contains("Illumina"));
    assertTrue(platforms.contains("PacBio"));

    List<String> selections = table.getDropdownOptions(Columns.SELECTION, 0);
    assertEquals(27, selections.size());
    assertTrue(selections.contains("cDNA"));
    assertTrue(selections.contains("PCR"));

    List<String> strategies = table.getDropdownOptions(Columns.STRATEGY, 0);
    assertEquals(20, strategies.size());
    assertTrue(strategies.contains("AMPLICON"));
    assertTrue(strategies.contains("OTHER"));

    List<String> qcValues = table.getDropdownOptions(Columns.QC_PASSED, 0);
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
    List<String> types = table.getDropdownOptions(Columns.LIBRARY_TYPE, 0);
    assertTrue(types.isEmpty());
    List<String> families = table.getDropdownOptions(Columns.INDEX_FAMILY, 0);
    // may contain an empty String, which doesn't do any harm
    assertTrue(families.isEmpty() || (families.size() == 1 && isStringEmptyOrNull(families.get(0))));
    List<String> kits = table.getDropdownOptions(Columns.KIT_DESCRIPTOR, 0);
    assertTrue(kits.isEmpty());

    // select platform
    table.enterText(Columns.PLATFORM, 0, "Illumina");

    // verify platform-dependant options
    types = table.getDropdownOptions(Columns.LIBRARY_TYPE, 0);
    assertEquals(4, types.size());
    assertTrue(types.contains("Paired End"));
    assertTrue(types.contains("Single End"));

    families = table.getDropdownOptions(Columns.INDEX_FAMILY, 0);
    assertEquals(3, families.size());
    assertTrue(families.contains(NO_INDEX_FAMILY));
    assertTrue(families.contains("Single Index 6bp"));
    assertTrue(families.contains("Dual Index 6bp"));

    kits = table.getDropdownOptions(Columns.KIT_DESCRIPTOR, 0);
    assertEquals(2, kits.size());
    assertTrue(kits.contains("Test Kit"));

    // indices depend on index family
    List<String> index1s = table.getDropdownOptions(Columns.INDEX_1, 0);
    assertEquals(1, index1s.size());
    assertTrue(index1s.contains(NO_INDEX));
    List<String> index2s = table.getDropdownOptions(Columns.INDEX_2, 0);
    assertEquals(1, index2s.size());
    assertTrue(index2s.contains(NO_INDEX));

    // single index family
    table.enterText(Columns.INDEX_FAMILY, 0, "Single Index 6bp");

    index1s = table.getDropdownOptions(Columns.INDEX_1, 0);
    assertTrue(index1s.contains("Index 01 (AAAAAA)"));
    assertTrue(index1s.contains("Index 02 (CCCCCC)"));
    index2s = table.getDropdownOptions(Columns.INDEX_2, 0);
    assertEquals(1, index2s.size());
    assertTrue(index2s.contains(NO_INDEX));

    // dual index
    table.enterText(Columns.INDEX_FAMILY, 0, "Dual Index 6bp");

    index1s = table.getDropdownOptions(Columns.INDEX_1, 0);
    assertEquals(4, index1s.size());
    assertTrue(index1s.contains("A01 (AAACCC)"));
    assertTrue(index1s.contains("A02 (CCCAAA)"));
    index2s = table.getDropdownOptions(Columns.INDEX_2, 0);
    assertEquals(5, index2s.size());
    assertTrue(index2s.contains(NO_INDEX));
    assertTrue(index2s.contains("B01 (AAATTT)"));
    assertTrue(index2s.contains("B02 (CCCGGG)"));
  }

  @Test
  public void testReadOnlyCells() {
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(100004L), 1);
    HandsOnTable table = page.getTable();

    assertFalse(table.isWritable(Columns.NAME, 0));
    assertFalse(table.isWritable(Columns.SAMPLE_ALIAS, 0));

    assertTrue(table.isWritable(Columns.CODE, 0));
    assertTrue(table.isWritable(Columns.SELECTION, 0));
    assertTrue(table.isWritable(Columns.STRATEGY, 0));

    table.enterText(Columns.DESIGN, 0, "WG");
    assertFalse(table.isWritable(Columns.CODE, 0));
    assertFalse(table.isWritable(Columns.SELECTION, 0));
    assertFalse(table.isWritable(Columns.STRATEGY, 0));
  }

  @Test
  public void testPropagate() {
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(100004L), 1);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.ID_BARCODE, "LIBT_PROP1");
    attrs.put(Columns.DESCRIPTION, "LIBT propagate test");
    attrs.put(Columns.DESIGN, "WG");
    attrs.put(Columns.PLATFORM, "Illumina");
    attrs.put(Columns.LIBRARY_TYPE, "Paired End");
    attrs.put(Columns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(Columns.INDEX_1, "A01 (AAACCC)");
    attrs.put(Columns.INDEX_2, "B01 (AAATTT)");
    attrs.put(Columns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(Columns.QC_PASSED, "True");
    attrs.put(Columns.SIZE, "123");
    attrs.put(Columns.VOLUME, "6.66");
    attrs.put(Columns.CONCENTRATION, "12.57");

    fillRow(table, 0, attrs);

    // should be set by selecting Design
    attrs.put(Columns.CODE, "WG");
    attrs.put(Columns.SELECTION, "PCR");
    attrs.put(Columns.STRATEGY, "WGS");

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
    attrs.put(Columns.CODE, "WG");
    attrs.put(Columns.PLATFORM, "Illumina");
    attrs.put(Columns.LIBRARY_TYPE, "Paired End");
    attrs.put(Columns.SELECTION, "CAGE");
    attrs.put(Columns.STRATEGY, "AMPLICON");
    attrs.put(Columns.INDEX_FAMILY, NO_INDEX_FAMILY);
    attrs.put(Columns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(Columns.SIZE, "205");
    attrs.put(Columns.QC_PASSED, "Unknown");

    fillRow(table, 0, attrs);
    fillRow(table, 1, attrs);
    table.enterText(Columns.SIZE, 1, "206");

    assertColumnValues(table, 0, attrs, "pre-save");
    attrs.put(Columns.SIZE, "206");
    assertColumnValues(table, 1, attrs, "pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 1, attrs, "pre-save");
    attrs.put(Columns.SIZE, "205");
    assertColumnValues(table, 0, attrs, "pre-save");

    Long newId1 = getSavedId(table, 0);
    Long newId2 = getSavedId(table, 1);
    DetailedLibrary saved1 = (DetailedLibrary) getSession().get(LibraryImpl.class, newId1);
    DetailedLibrary saved2 = (DetailedLibrary) getSession().get(LibraryImpl.class, newId2);
    assertDetailedLibraryAttributes(attrs, saved1);
    attrs.put(Columns.SIZE, "205");
    assertDetailedLibraryAttributes(attrs, saved2);
  }

  @Test
  public void testEditChangeValues() {
    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(100001L));
    HandsOnTable table = page.getTable();

    // test initial values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.NAME, "LIB100001");
    attrs.put(Columns.ALIAS, "LIBT_0001_Ly_P_PE_251_WG");
    attrs.put(Columns.SAMPLE_ALIAS, "LIBT_0001_Ly_P_1-1_D1");
    attrs.put(Columns.ID_BARCODE, "libbar100001");
    attrs.put(Columns.DESCRIPTION, "libdesc100001");
    attrs.put(Columns.DESIGN, NO_DESIGN);
    attrs.put(Columns.CODE, "WG");
    attrs.put(Columns.PLATFORM, "Illumina");
    attrs.put(Columns.LIBRARY_TYPE, "Paired End");
    attrs.put(Columns.SELECTION, "PCR");
    attrs.put(Columns.STRATEGY, "WGS");
    attrs.put(Columns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(Columns.INDEX_1, "A01 (AAACCC)");
    attrs.put(Columns.INDEX_2, "B01 (AAATTT)");
    attrs.put(Columns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(Columns.QC_PASSED, "False");
    attrs.put(Columns.SIZE, "251");
    attrs.put(Columns.VOLUME, "2.5");
    attrs.put(Columns.CONCENTRATION, "10.0");
    assertColumnValues(table, 0, attrs, "loaded");

    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.ALIAS, "LIBT_0001_Ly_P_PE_241_WG");
    changes.put(Columns.ID_BARCODE, "changed100001");
    changes.put(Columns.DESCRIPTION, "changed100001");
    changes.put(Columns.DESIGN, "EX");
    changes.put(Columns.LIBRARY_TYPE, "Single End");
    changes.put(Columns.INDEX_FAMILY, "Single Index 6bp");
    changes.put(Columns.INDEX_1, "Index 01 (AAAAAA)");
    changes.put(Columns.KIT_DESCRIPTOR, "Test Kit Two");
    changes.put(Columns.QC_PASSED, "True");
    changes.put(Columns.SIZE, "241");
    changes.put(Columns.VOLUME, "1.88");
    changes.put(Columns.CONCENTRATION, "12.34");
    fillRow(table, 0, changes);

    // unchanged
    changes.put(Columns.NAME, "LIB100001");
    changes.put(Columns.SAMPLE_ALIAS, "LIBT_0001_Ly_P_1-1_D1");
    changes.put(Columns.PLATFORM, "Illumina");

    // set based on other changes
    changes.put(Columns.CODE, "EX");
    changes.put(Columns.SELECTION, "Hybrid Selection");
    changes.put(Columns.STRATEGY, "WXS");
    changes.put(Columns.INDEX_2, NO_INDEX);
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
    attrs.put(Columns.NAME, "LIB100002");
    attrs.put(Columns.ALIAS, "LIBT_0001_Ly_P_PE_252_WG");
    attrs.put(Columns.SAMPLE_ALIAS, "LIBT_0001_Ly_P_1-1_D1");
    attrs.put(Columns.ID_BARCODE, "libbar100002");
    attrs.put(Columns.DESCRIPTION, "libdesc100002");
    attrs.put(Columns.DESIGN, "WG");
    attrs.put(Columns.CODE, "WG");
    attrs.put(Columns.PLATFORM, "Illumina");
    attrs.put(Columns.LIBRARY_TYPE, "Paired End");
    attrs.put(Columns.SELECTION, "PCR");
    attrs.put(Columns.STRATEGY, "WGS");
    attrs.put(Columns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(Columns.INDEX_1, "A02 (CCCAAA)");
    attrs.put(Columns.INDEX_2, "B02 (CCCGGG)");
    attrs.put(Columns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(Columns.QC_PASSED, "False");
    attrs.put(Columns.SIZE, "252");
    attrs.put(Columns.VOLUME, "4.0");
    attrs.put(Columns.CONCENTRATION, "6.3");
    assertColumnValues(table, 0, attrs, "loaded");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.ID_BARCODE, "");
    changes.put(Columns.DESCRIPTION, "");
    changes.put(Columns.DESIGN, NO_DESIGN);
    changes.put(Columns.CODE, "CH");
    changes.put(Columns.SELECTION, "cDNA");
    changes.put(Columns.STRATEGY, "CLONE");
    changes.put(Columns.INDEX_FAMILY, NO_INDEX_FAMILY);
    changes.put(Columns.QC_PASSED, "True");
    changes.put(Columns.SIZE, "241");
    changes.put(Columns.VOLUME, "1.88");
    changes.put(Columns.CONCENTRATION, "12.34");
    fillRow(table, 0, changes);

    // set based on other changes
    changes.put(Columns.INDEX_1, NO_INDEX);
    changes.put(Columns.INDEX_2, NO_INDEX);

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
    attrs.put(Columns.NAME, "LIB100003");
    attrs.put(Columns.ALIAS, "LIBT_0001_Ly_P_PE_253_WG");
    attrs.put(Columns.SAMPLE_ALIAS, "LIBT_0001_Ly_P_1-1_D1");
    attrs.put(Columns.ID_BARCODE, null);
    attrs.put(Columns.DESCRIPTION, null);
    attrs.put(Columns.DESIGN, NO_DESIGN);
    attrs.put(Columns.CODE, "WG");
    attrs.put(Columns.PLATFORM, "Illumina");
    attrs.put(Columns.LIBRARY_TYPE, "Paired End");
    attrs.put(Columns.SELECTION, "PCR");
    attrs.put(Columns.STRATEGY, "WGS");
    attrs.put(Columns.INDEX_FAMILY, NO_INDEX_FAMILY);
    attrs.put(Columns.INDEX_1, NO_INDEX);
    attrs.put(Columns.INDEX_2, NO_INDEX);
    attrs.put(Columns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(Columns.QC_PASSED, "Unknown");
    attrs.put(Columns.SIZE, null);
    attrs.put(Columns.VOLUME, null);
    attrs.put(Columns.CONCENTRATION, null);
    assertColumnValues(table, 0, attrs, "loaded");

    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.ID_BARCODE, "changed_bar_100003");
    changes.put(Columns.DESCRIPTION, "changed_desc_100003");
    changes.put(Columns.DESIGN, "TS (PCR)");
    changes.put(Columns.INDEX_FAMILY, "Dual Index 6bp");
    changes.put(Columns.INDEX_1, "A04 (TTTGGG)");
    changes.put(Columns.INDEX_2, "B04 (TTTAAA)");
    changes.put(Columns.QC_PASSED, "True");
    changes.put(Columns.SIZE, "253");
    changes.put(Columns.VOLUME, "18.0");
    changes.put(Columns.CONCENTRATION, "7.6");
    fillRow(table, 0, changes);

    // changed because of design
    changes.put(Columns.CODE, "TS");
    changes.put(Columns.SELECTION, "PCR");
    changes.put(Columns.STRATEGY, "AMPLICON");

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
    attrs.put(BulkSamplePage.Columns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(BulkSamplePage.Columns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(BulkSamplePage.Columns.PROJECT, "LIBT");
    attrs.put(BulkSamplePage.Columns.EXTERNAL_NAME, "lkjh");
    attrs.put(BulkSamplePage.Columns.DONOR_SEX, "Unknown");
    attrs.put(BulkSamplePage.Columns.TISSUE_ORIGIN, "Bn (Brain)");
    attrs.put(BulkSamplePage.Columns.TISSUE_TYPE, "P (Primary tumour)");
    attrs.put(BulkSamplePage.Columns.TIMES_RECEIVED, "3");
    attrs.put(BulkSamplePage.Columns.TUBE_NUMBER, "4");
    attrs.put(Columns.ID_BARCODE, "LIBT_RCV1");
    attrs.put(Columns.DESCRIPTION, "LIBT receive test");
    attrs.put(Columns.RECEIVE_DATE, "2017-10-12");
    attrs.put(Columns.DESIGN, "WG");
    attrs.put(Columns.PLATFORM, "Illumina");
    attrs.put(Columns.LIBRARY_TYPE, "Paired End");
    attrs.put(Columns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(Columns.INDEX_1, "A01 (AAACCC)");
    attrs.put(Columns.INDEX_2, "B01 (AAATTT)");
    attrs.put(Columns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(Columns.QC_PASSED, "True");
    attrs.put(Columns.SIZE, "123");
    attrs.put(Columns.VOLUME, "6.66");
    attrs.put(Columns.CONCENTRATION, "12.57");

    fillRow(table, 0, attrs);

    // should be set by selecting Design
    attrs.put(Columns.CODE, "WG");
    attrs.put(Columns.SELECTION, "PCR");
    attrs.put(Columns.STRATEGY, "WGS");

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
    attrs.put(Columns.CODE, "WG");
    attrs.put(Columns.PLATFORM, "Illumina");
    attrs.put(Columns.LIBRARY_TYPE, "Paired End");
    attrs.put(Columns.SELECTION, "CAGE");
    attrs.put(Columns.STRATEGY, "AMPLICON");
    attrs.put(Columns.INDEX_FAMILY, NO_INDEX_FAMILY);
    attrs.put(Columns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(Columns.SIZE, "207");
    attrs.put(Columns.QC_PASSED, "Unknown");
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

    table2.enterText(Columns.SELECTION, 0, "cDNA");
    attrs.put(Columns.SELECTION, "cDNA");
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
    assertEquals("libdesc100004", table.getText(Columns.DESCRIPTION, 0));
    String descEdit1 = "changed once";
    table.enterText(Columns.DESCRIPTION, 0, descEdit1);
    assertEquals(descEdit1, table.getText(Columns.DESCRIPTION, 0));

    saveAndAssertSuccess(table);
    DetailedLibrary saved = (DetailedLibrary) getSession().get(LibraryImpl.class, 100004L);
    assertEquals(descEdit1, saved.getDescription());

    // edit twice
    BulkLibraryPage page2 = page.chainEdit();
    HandsOnTable table2 = page2.getTable();
    assertEquals(descEdit1, table2.getText(Columns.DESCRIPTION, 0));
    String descEdit2 = "changed twice";
    table2.enterText(Columns.DESCRIPTION, 0, descEdit2);
    assertEquals(descEdit2, table2.getText(Columns.DESCRIPTION, 0));

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

    initial.forEach((k, v) -> assertTrue("initial value " + v + " in row " + k, table.getText(Columns.SAMPLE_LOCATION, k).endsWith(v)));
    
    Map<Integer, String> sortCols = new HashMap<>();
    sortCols.put(0, Libs.B05);
    sortCols.put(1, Libs.C06);
    sortCols.put(2, Libs.A07);
    
    page.sortBySampleLocationColumns();
    sortCols.forEach((k, v) -> assertTrue("sortCols value " + v + " in row " + k, table.getText(Columns.SAMPLE_LOCATION, k).endsWith(v)));
    
    Map<Integer, String> sortRows = new HashMap<>();
    sortRows.put(0, Libs.A07);
    sortRows.put(1, Libs.B05);
    sortRows.put(2, Libs.C06);
    
    page.sortBySampleLocationRows();
    sortRows.forEach((k, v) -> assertTrue(table.getText(Columns.SAMPLE_LOCATION, k).endsWith(v)));
  }

  private void fillRow(HandsOnTable table, int rowNum, Map<String, String> attributes) {
    attributes.forEach((key, val) -> table.enterText(key, rowNum, val));
  }

  private long getSavedId(HandsOnTable table, int rowNum) {
    return Long.valueOf(table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length()));
  }

  private void assertPlainLibraryAttributes(Map<String, String> attributes, Library library) {
    testLibraryAttribute(Columns.NAME, attributes, library, Library::getName);
    testLibraryAttribute(Columns.ALIAS, attributes, library, Library::getAlias);
    testLibraryAttribute(Columns.DESCRIPTION, attributes, library, Library::getDescription);
    testLibraryAttribute(Columns.PLATFORM, attributes, library, lib -> lib.getPlatformType().getKey());
    testLibraryAttribute(Columns.ID_BARCODE, attributes, library, Library::getIdentificationBarcode);
    testLibraryAttribute(Columns.LIBRARY_TYPE, attributes, library, lib -> lib.getLibraryType().getDescription());
    testLibraryAttribute(Columns.INDEX_FAMILY, attributes, library, lib -> {
      if (lib.getIndices() == null || lib.getIndices().isEmpty()) {
        return null;
      }
      return lib.getIndices().get(0).getFamily().getName();
    });
    testLibraryAttribute(Columns.INDEX_1, attributes, library, indexGetter(1));
    testLibraryAttribute(Columns.INDEX_2, attributes, library, indexGetter(2));
    testLibraryAttribute(Columns.QC_PASSED, attributes, library, lib -> getQcPassedString(lib.getQcPassed()));
    testLibraryAttribute(Columns.SIZE, attributes, library, lib -> {
      return lib.getDnaSize() == null ? null : lib.getDnaSize().toString();
    });
    testLibraryAttribute(Columns.VOLUME, attributes, library, lib -> lib.getVolume().toString());
    testLibraryAttribute(Columns.CONCENTRATION, attributes, library, lib -> lib.getInitialConcentration().toString());
    testLibraryAttribute(Columns.RECEIVE_DATE, attributes, library, lib -> {
      return lib.getReceivedDate() == null ? null : LimsUtils.formatDate(lib.getReceivedDate());
    });
  }

  private void assertDetailedLibraryAttributes(Map<String, String> attributes, DetailedLibrary library) {
    assertPlainLibraryAttributes(attributes, library);

    testLibraryAttribute(Columns.DESIGN, attributes, library, lib -> {
      return lib.getLibraryDesign() == null ? null : lib.getLibraryDesign().getName();
    });
    testLibraryAttribute(Columns.KIT_DESCRIPTOR, attributes, library, lib -> lib.getKitDescriptor().getName());
    testLibraryAttribute(Columns.CODE, attributes, library, lib -> lib.getLibraryDesignCode().getCode());
    testLibraryAttribute(Columns.SELECTION, attributes, library, lib -> lib.getLibrarySelectionType().getName());
    testLibraryAttribute(Columns.STRATEGY, attributes, library, lib -> lib.getLibraryStrategyType().getName());
    testLibraryAttribute(Columns.GROUP_ID, attributes, library, DetailedLibrary::getGroupId);
    testLibraryAttribute(Columns.GROUP_DESC, attributes, library, DetailedLibrary::getGroupDescription);
  }

  private void assertParentSampleAttributes(Map<String, String> attributes, DetailedLibrary library) {
    testLibraryAttribute(BulkSamplePage.Columns.SAMPLE_TYPE, attributes, library, lib -> lib.getSample().getSampleType());
    testLibraryAttribute(BulkSamplePage.Columns.SCIENTIFIC_NAME, attributes, library, lib -> lib.getSample().getScientificName());
    testLibraryAttribute(BulkSamplePage.Columns.PROJECT, attributes, library, lib -> lib.getSample().getProject().getShortName());
    testLibraryAttribute(BulkSamplePage.Columns.EXTERNAL_NAME, attributes, library, lib -> identityGetter.apply(lib).getExternalName());
    testLibraryAttribute(BulkSamplePage.Columns.DONOR_SEX, attributes, library, lib -> identityGetter.apply(lib).getDonorSex().getLabel());
    testLibraryAttribute(BulkSamplePage.Columns.TISSUE_ORIGIN, attributes, library,
        lib -> tissueGetter.apply(lib).getTissueOrigin().getItemLabel());
    testLibraryAttribute(BulkSamplePage.Columns.TISSUE_TYPE, attributes, library,
        lib -> tissueGetter.apply(lib).getTissueType().getItemLabel());
    testLibraryAttribute(BulkSamplePage.Columns.TIMES_RECEIVED, attributes, library,
        lib -> tissueGetter.apply(lib).getTimesReceived().toString());
    testLibraryAttribute(BulkSamplePage.Columns.TUBE_NUMBER, attributes, library,
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

  private String getQcPassedString(Boolean qcPassed) {
    if (qcPassed == null) {
      return "Unknown";
    } else if (qcPassed) {
      return "True";
    } else {
      return "False";
    }
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
    case Columns.INDEX_FAMILY:
      return NO_INDEX_FAMILY.equals(value) ? null : value;
    case Columns.INDEX_1:
    case Columns.INDEX_2:
      return NO_INDEX.equals(value) ? null : value;
    case Columns.DESIGN:
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
      assertTrue("Library name generation", table.getText(Columns.NAME, i).contains("LIB"));
      assertTrue("Library alias generation", !isStringEmptyOrNull(table.getText(Columns.ALIAS, i)));
    }
  }

}
