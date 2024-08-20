package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import jakarta.persistence.criteria.Join;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.DetailedSample;
import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.SampleIdentity;
import uk.ac.bbsrc.tgac.miso.core.data.SampleTissue;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.TransferLibrary_;
import uk.ac.bbsrc.tgac.miso.core.data.impl.transfer.Transfer_;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.impl.QueryBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryPage.LibColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils;

public class BulkLibraryIT extends AbstractIT {

  private static final Set<String> commonColumns = Sets.newHashSet(SamColumns.PROJECT, SamColumns.REQUISITION_ASSAY,
      LibColumns.NAME, LibColumns.ALIAS, LibColumns.ID_BARCODE, LibColumns.BOX_SEARCH, LibColumns.BOX_ALIAS,
      LibColumns.BOX_POSITION, LibColumns.DISCARDED, LibColumns.DESCRIPTION, LibColumns.GROUP_ID, LibColumns.GROUP_DESC,
      LibColumns.DESIGN, LibColumns.CODE, LibColumns.PLATFORM, LibColumns.LIBRARY_TYPE, LibColumns.SELECTION,
      LibColumns.STRATEGY, LibColumns.INDEX_FAMILY, LibColumns.INDEX_1, LibColumns.INDEX_2, LibColumns.UMIS,
      LibColumns.KIT_DESCRIPTOR, LibColumns.QC_STATUS, LibColumns.QC_NOTE, LibColumns.SIZE, LibColumns.VOLUME,
      LibColumns.VOLUME_UNITS, LibColumns.CONCENTRATION, LibColumns.CONCENTRATION_UNITS, LibColumns.SPIKE_IN,
      LibColumns.SPIKE_IN_DILUTION, LibColumns.SPIKE_IN_VOL);

  private static final Set<String> editColumns = Sets.newHashSet(LibColumns.TISSUE_ORIGIN, LibColumns.TISSUE_TYPE,
      LibColumns.EFFECTIVE_GROUP_ID, LibColumns.CREATION_DATE, LibColumns.SOP, LibColumns.WORKSTATION,
      LibColumns.THERMAL_CYCLER, LibColumns.INITIAL_VOLUME, LibColumns.PARENT_NG_USED, LibColumns.PARENT_VOLUME_USED,
      LibColumns.KIT_LOT);

  private static final Set<String> propagateColumns = Sets.newHashSet(LibColumns.SAMPLE_NAME, LibColumns.SAMPLE_ALIAS,
      LibColumns.SAMPLE_LOCATION, LibColumns.TISSUE_ORIGIN, LibColumns.TISSUE_TYPE, LibColumns.EFFECTIVE_GROUP_ID,
      LibColumns.CREATION_DATE, LibColumns.SOP, LibColumns.WORKSTATION, LibColumns.THERMAL_CYCLER,
      LibColumns.PARENT_NG_USED, LibColumns.PARENT_VOLUME_USED, LibColumns.KIT_LOT);

  private static final Set<String> receiptColumns = Sets.newHashSet(SamColumns.SAMPLE_TYPE, SamColumns.SCIENTIFIC_NAME,
      SamColumns.SUBPROJECT, SamColumns.EXTERNAL_NAME, SamColumns.IDENTITY_ALIAS, SamColumns.DONOR_SEX,
      SamColumns.CONSENT, SamColumns.SAMPLE_CLASS, SamColumns.TISSUE_ORIGIN, SamColumns.TISSUE_TYPE,
      SamColumns.PASSAGE_NUMBER, SamColumns.TIMES_RECEIVED, SamColumns.TUBE_NUMBER, SamColumns.TISSUE_MATERIAL,
      SamColumns.REGION, SamColumns.TIMEPOINT,
      LibColumns.RECEIVE_DATE, LibColumns.RECEIVE_TIME, LibColumns.RECEIVED_FROM, LibColumns.RECEIVED_BY,
      LibColumns.RECEIPT_CONFIRMED, LibColumns.RECEIPT_QC_PASSED, LibColumns.RECEIPT_QC_NOTE,
      LibColumns.REQUISITION_ALIAS, LibColumns.REQUISITION, LibColumns.TEMPLATE);

  private static final String NO_INDEX_FAMILY = "No indices";

  @Before
  public void setup() {
    login();
  }

  @Test
  public void testEditSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkLibraryPage page = BulkLibraryPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(100001L));
    Set<String> expectedColumns = Sets.newHashSet();
    expectedColumns.addAll(commonColumns);
    expectedColumns.addAll(editColumns);
    HandsontableUtils.testTableSetup(page, expectedColumns, 1);
  }

  @Test
  public void testPropagateSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(100004L), Arrays.asList(4));
    Set<String> expectedColumns = Sets.newHashSet();
    expectedColumns.addAll(commonColumns);
    expectedColumns.addAll(propagateColumns);
    HandsontableUtils.testTableSetup(page, expectedColumns, 4);

    HandsOnTable table = page.getTable();
    assertEquals("LIBT_0001_Ly_P_1-1_D1", table.getText(LibColumns.SAMPLE_ALIAS, 0));
    assertEquals("", table.getText(LibColumns.QC_STATUS, 0));
  }

  @Test
  public void testReceiveDnaSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    // test for gDNA aliquot parent
    BulkLibraryPage page = BulkLibraryPage.getForReceive(getDriver(), getBaseUrl(), 2, null, 15L);
    Set<String> expectedColumns = Sets.newHashSet();
    expectedColumns.addAll(commonColumns);
    expectedColumns.addAll(receiptColumns);
    HandsontableUtils.testTableSetup(page, expectedColumns, 2);

    HandsOnTable table = page.getTable();
    assertEquals("", table.getText(LibColumns.QC_STATUS, 0));
  }

  @Test
  public void testReceiveRnaSetup() throws Exception {
    BulkLibraryPage page = BulkLibraryPage.getForReceive(getDriver(), getBaseUrl(), 3, null, 19L);
    Set<String> expectedColumns = Sets.newHashSet();
    expectedColumns.addAll(commonColumns);
    expectedColumns.addAll(receiptColumns);
    expectedColumns.add(SamColumns.DNASE_TREATED);
    HandsontableUtils.testTableSetup(page, expectedColumns, 3);
  }

  @Test
  public void testPropagateDropdowns() throws Exception {
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(100004L), Arrays.asList(1));
    HandsOnTable table = page.getTable();

    Set<String> designs = table.getDropdownOptions(LibColumns.DESIGN, 0);
    assertEquals(6, designs.size());
    assertTrue(designs.contains("WG"));
    assertTrue(designs.contains("AS"));

    Set<String> codes = table.getDropdownOptions(LibColumns.CODE, 0);
    assertTrue(codes.size() > 10);
    assertTrue(codes.contains("EX (Exome)"));
    assertTrue(codes.contains("MR (mRNA)"));

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

    Set<String> qcValues = table.getDropdownOptions(LibColumns.QC_STATUS, 0);
    assertEquals(10, qcValues.size());
    assertTrue(qcValues.contains("Ready"));
    assertTrue(qcValues.contains("Failed: QC"));
  }

  @Test
  public void testPropagateDependencyCells() {
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(100004L), Arrays.asList(1));
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
    assertTrue(families.size() > 4);
    assertTrue(families.contains(NO_INDEX_FAMILY));
    assertTrue(families.contains("Single Index 6bp"));
    assertTrue(families.contains("Dual Index 6bp"));
    assertTrue(families.contains("Similar Index Pair"));

    kits = table.getDropdownOptions(LibColumns.KIT_DESCRIPTOR, 0);
    assertEquals(2, kits.size());
    assertTrue(kits.contains("Test Kit"));

    // indices depend on index family
    assertFalse(table.isWritable(LibColumns.INDEX_1, 0));
    assertFalse(table.isWritable(LibColumns.INDEX_2, 0));

    // single index family
    table.enterText(LibColumns.INDEX_FAMILY, 0, "Single Index 6bp");

    Set<String> index1s = table.getDropdownOptions(LibColumns.INDEX_1, 0);
    assertTrue(index1s.contains("Index 01 (AAAAAA)"));
    assertTrue(index1s.contains("Index 02 (CCCCCC)"));
    assertFalse(table.isWritable(LibColumns.INDEX_2, 0));

    // dual index
    table.enterText(LibColumns.INDEX_FAMILY, 0, "Dual Index 6bp");

    index1s = table.getDropdownOptions(LibColumns.INDEX_1, 0);
    assertEquals(4, index1s.size());
    assertTrue(index1s.contains("A01 (AAACCC)"));
    assertTrue(index1s.contains("A02 (CCCAAA)"));
    Set<String> index2s = table.getDropdownOptions(LibColumns.INDEX_2, 0);
    assertEquals(4, index2s.size());
    assertTrue(index2s.contains("B01 (AAATTT)"));
    assertTrue(index2s.contains("B02 (CCCGGG)"));
  }

  @Test
  public void testReadOnlyCells() {
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(100004L), Arrays.asList(1));
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
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(100004L), Arrays.asList(1));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibColumns.ID_BARCODE, "LIBT_PROP1");
    attrs.put(LibColumns.DESCRIPTION, "LIBT propagate test");
    attrs.put(LibColumns.SOP, "Library SOP 1 v.2.0");
    attrs.put(LibColumns.DESIGN, "WG");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(LibColumns.INDEX_1, "A01 (AAACCC)");
    attrs.put(LibColumns.INDEX_2, "B01 (AAATTT)");
    attrs.put(LibColumns.UMIS, "False");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.KIT_LOT, "20200728");
    attrs.put(LibColumns.QC_STATUS, "Ready");
    attrs.put(LibColumns.SIZE, "123");
    attrs.put(LibColumns.VOLUME, "6.66");
    attrs.put(LibColumns.CONCENTRATION, "12.57");

    fillRow(table, 0, attrs);

    // should be set by selecting Design
    attrs.put(LibColumns.CODE, "WG (Whole Genome)");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");

    assertColumnValues(table, 0, attrs, "pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    Long newId = getSavedId(savedTable, 0);
    DetailedLibrary saved = (DetailedLibrary) getSession().get(LibraryImpl.class, newId);
    assertDetailedLibraryAttributes(attrs, saved);
  }

  @Test
  public void testPropagateTwoMinimal() {
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(100004L), Arrays.asList(2));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibColumns.SOP, "Library SOP 1 v.2.0");
    attrs.put(LibColumns.CODE, "WG (Whole Genome)");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "CAGE");
    attrs.put(LibColumns.STRATEGY, "AMPLICON");
    attrs.put(LibColumns.INDEX_FAMILY, NO_INDEX_FAMILY);
    attrs.put(LibColumns.UMIS, "False");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.KIT_LOT, "20200728");
    attrs.put(LibColumns.SIZE, "205");
    attrs.put(LibColumns.QC_STATUS, "Not Ready");

    fillRow(table, 0, attrs);
    fillRow(table, 1, attrs);
    table.enterText(LibColumns.SIZE, 1, "206");

    assertColumnValues(table, 0, attrs, "pre-save");
    attrs.put(LibColumns.SIZE, "206");
    assertColumnValues(table, 1, attrs, "pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    attrs.put(LibColumns.SIZE, "205");

    Long newId1 = getSavedId(savedTable, 0);
    Long newId2 = getSavedId(savedTable, 1);
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
    attrs.put(LibColumns.ID_BARCODE, "libbar100001");
    attrs.put(LibColumns.DESCRIPTION, "libdesc100001");
    attrs.put(LibColumns.DESIGN, "");
    attrs.put(LibColumns.CODE, "WG (Whole Genome)");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");
    attrs.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(LibColumns.INDEX_1, "A01 (AAACCC)");
    attrs.put(LibColumns.INDEX_2, "B01 (AAATTT)");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_STATUS, "Failed: QC");
    attrs.put(LibColumns.SIZE, "251");
    attrs.put(LibColumns.VOLUME, "2.5");
    attrs.put(LibColumns.CONCENTRATION, "10.0");
    attrs.put(LibColumns.SPIKE_IN, "Spike-In One");
    attrs.put(LibColumns.SPIKE_IN_DILUTION, "1:10");
    attrs.put(LibColumns.SPIKE_IN_VOL, "12.34");
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
    changes.put(LibColumns.QC_STATUS, "Ready");
    changes.put(LibColumns.SIZE, "241");
    changes.put(LibColumns.VOLUME, "1.88");
    changes.put(LibColumns.CONCENTRATION, "12.34");
    fillRow(table, 0, changes);

    // unchanged
    changes.put(LibColumns.NAME, "LIB100001");
    changes.put(LibColumns.PLATFORM, "Illumina");

    // set based on other changes
    changes.put(LibColumns.CODE, "EX (Exome)");
    changes.put(LibColumns.SELECTION, "Hybrid Selection");
    changes.put(LibColumns.STRATEGY, "WXS");
    changes.put(LibColumns.INDEX_2, "");
    assertColumnValues(table, 0, changes, "changes pre-save");

    assertTrue(page.save(false));
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
    attrs.put(LibColumns.ID_BARCODE, "libbar100002");
    attrs.put(LibColumns.DESCRIPTION, "libdesc100002");
    attrs.put(LibColumns.DESIGN, "WG");
    attrs.put(LibColumns.CODE, "WG (Whole Genome)");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");
    attrs.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(LibColumns.INDEX_1, "A02 (CCCAAA)");
    attrs.put(LibColumns.INDEX_2, "B02 (CCCGGG)");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_STATUS, "Failed: QC");
    attrs.put(LibColumns.SIZE, "252");
    attrs.put(LibColumns.VOLUME, "4.0");
    attrs.put(LibColumns.CONCENTRATION, "6.3");
    attrs.put(LibColumns.SPIKE_IN, "Spike-In One");
    attrs.put(LibColumns.SPIKE_IN_DILUTION, "1:10");
    attrs.put(LibColumns.SPIKE_IN_VOL, "12.34");
    assertColumnValues(table, 0, attrs, "loaded");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(LibColumns.ID_BARCODE, "");
    changes.put(LibColumns.DESCRIPTION, "");
    changes.put(LibColumns.DESIGN, "");
    changes.put(LibColumns.CODE, "CH (ChIP-Seq)");
    changes.put(LibColumns.SELECTION, "cDNA");
    changes.put(LibColumns.STRATEGY, "CLONE");
    changes.put(LibColumns.INDEX_FAMILY, NO_INDEX_FAMILY);
    changes.put(LibColumns.QC_STATUS, "Ready");
    changes.put(LibColumns.SIZE, "241");
    changes.put(LibColumns.VOLUME, "1.88");
    changes.put(LibColumns.CONCENTRATION, "12.34");
    changes.put(LibColumns.SPIKE_IN, "");
    fillRow(table, 0, changes);

    // set based on other changes
    changes.put(LibColumns.INDEX_1, "");
    changes.put(LibColumns.INDEX_2, "");
    changes.put(LibColumns.SPIKE_IN_DILUTION, "");
    changes.put(LibColumns.SPIKE_IN_VOL, "");

    // unchanged
    attrs.forEach((key, val) -> {
      if (!changes.containsKey(key)) {
        changes.put(key, val);
      }
    });
    assertColumnValues(table, 0, changes, "changes pre-save");

    assertTrue(page.save(false));
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
    attrs.put(LibColumns.ID_BARCODE, null);
    attrs.put(LibColumns.DESCRIPTION, null);
    attrs.put(LibColumns.DESIGN, "");
    attrs.put(LibColumns.CODE, "WG (Whole Genome)");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");
    attrs.put(LibColumns.INDEX_FAMILY, NO_INDEX_FAMILY);
    attrs.put(LibColumns.INDEX_1, "");
    attrs.put(LibColumns.INDEX_2, "");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_STATUS, "Not Ready");
    attrs.put(LibColumns.SIZE, null);
    attrs.put(LibColumns.VOLUME, null);
    attrs.put(LibColumns.CONCENTRATION, null);
    attrs.put(LibColumns.SPIKE_IN, null);
    attrs.put(LibColumns.SPIKE_IN_DILUTION, null);
    attrs.put(LibColumns.SPIKE_IN_VOL, null);
    assertColumnValues(table, 0, attrs, "loaded");

    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(LibColumns.ID_BARCODE, "changed_bar_100003");
    changes.put(LibColumns.DESCRIPTION, "changed_desc_100003");
    changes.put(LibColumns.DESIGN, "TS (PCR)");
    changes.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    changes.put(LibColumns.INDEX_1, "A04 (TTTGGG)");
    changes.put(LibColumns.INDEX_2, "B04 (TTTAAA)");
    changes.put(LibColumns.QC_STATUS, "Ready");
    changes.put(LibColumns.SIZE, "253");
    changes.put(LibColumns.VOLUME, "18.0");
    changes.put(LibColumns.CONCENTRATION, "7.6");
    fillRow(table, 0, changes);

    // changed because of design
    changes.put(LibColumns.CODE, "TS (Targeted Sequencing)");
    changes.put(LibColumns.SELECTION, "PCR");
    changes.put(LibColumns.STRATEGY, "AMPLICON");

    // unchanged
    attrs.forEach((key, val) -> {
      if (!changes.containsKey(key)) {
        changes.put(key, val);
      }
    });
    assertColumnValues(table, 0, changes, "changes pre-save");

    assertTrue(page.save(false));
    DetailedLibrary lib = (DetailedLibrary) getSession().get(LibraryImpl.class, 100003L);
    assertDetailedLibraryAttributes(changes, lib);
  }

  @Test
  public void testReceipt() {
    BulkLibraryPage page = BulkLibraryPage.getForReceive(getDriver(), getBaseUrl(), 1, null, 15L);
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
    attrs.put(LibColumns.RECEIVE_TIME, "3:00 pm");
    attrs.put(LibColumns.RECEIVED_FROM, "University Health Network - BioBank");
    attrs.put(LibColumns.RECEIVED_BY, "TestGroupOne");
    attrs.put(LibColumns.RECEIPT_CONFIRMED, "True");
    attrs.put(LibColumns.RECEIPT_QC_PASSED, "True");
    attrs.put(LibColumns.RECEIPT_QC_NOTE, "");
    attrs.put(LibColumns.DESIGN, "WG");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(LibColumns.INDEX_1, "A01 (AAACCC)");
    attrs.put(LibColumns.INDEX_2, "B01 (AAATTT)");
    attrs.put(LibColumns.UMIS, "False");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_STATUS, "Ready");
    attrs.put(LibColumns.SIZE, "123");
    attrs.put(LibColumns.VOLUME, "6.66");
    attrs.put(LibColumns.CONCENTRATION, "12.57");

    fillRow(table, 0, attrs);

    // should be set by selecting Design
    attrs.put(LibColumns.CODE, "WG (Whole Genome)");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");

    assertColumnValues(table, 0, attrs, "pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    Long newId = getSavedId(savedTable, 0);
    DetailedLibrary saved = (DetailedLibrary) getSession().get(LibraryImpl.class, newId);
    assertDetailedLibraryAttributes(attrs, saved);
    assertParentSampleAttributes(attrs, saved);
  }

  @Test
  public void testPropagateToEditToPropagate() {
    // propagate sample to library
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(100004L), Arrays.asList(1));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibColumns.SOP, "Library SOP 1 v.2.0");
    attrs.put(LibColumns.CODE, "WG (Whole Genome)");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "CAGE");
    attrs.put(LibColumns.STRATEGY, "AMPLICON");
    attrs.put(LibColumns.INDEX_FAMILY, NO_INDEX_FAMILY);
    attrs.put(LibColumns.UMIS, "False");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.KIT_LOT, "20200728");
    attrs.put(LibColumns.SIZE, "207");
    attrs.put(LibColumns.QC_STATUS, "Not Ready");
    fillRow(table, 0, attrs);
    assertColumnValues(table, 0, attrs, "pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    long newId = getSavedId(savedTable, 0);
    DetailedLibrary saved = (DetailedLibrary) getSession().get(LibraryImpl.class, newId);
    assertDetailedLibraryAttributes(attrs, saved);

    // chain edit library
    BulkLibraryPage page2 = page.chainEdit();
    HandsOnTable table2 = page2.getTable();
    assertColumnValues(table2, 0, attrs, "reload for edit");

    table2.enterText(LibColumns.SELECTION, 0, "cDNA");
    attrs.put(LibColumns.SELECTION, "cDNA");
    assertTrue(page2.save(false));
    HandsOnTable savedTable2 = page2.getTable();
    assertColumnValues(savedTable2, 0, attrs, "edit post-save");
    DetailedLibrary saved2 = (DetailedLibrary) getSession().get(LibraryImpl.class, newId);
    assertDetailedLibraryAttributes(attrs, saved2);

    // chain propagate library to library aliquot
    assertNotNull(page2.chainPropagateLibraryAliquots());
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

    assertTrue(page.save(false));
    DetailedLibrary saved = (DetailedLibrary) getSession().get(LibraryImpl.class, 100004L);
    assertEquals(descEdit1, saved.getDescription());

    // edit twice
    BulkLibraryPage page2 = page.chainEdit();
    HandsOnTable table2 = page2.getTable();
    assertEquals(descEdit1, table2.getText(LibColumns.DESCRIPTION, 0));
    String descEdit2 = "changed twice";
    table2.enterText(LibColumns.DESCRIPTION, 0, descEdit2);
    assertEquals(descEdit2, table2.getText(LibColumns.DESCRIPTION, 0));
    assertTrue(page2.save(false));

    DetailedLibrary saved2 = (DetailedLibrary) getSession().get(LibraryImpl.class, 100004L);
    assertEquals(descEdit2, saved2.getDescription());
  }

  @Test
  public void testSortByBoxPosition() {
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Lists.newArrayList(204L, 205L, 206L),
            Lists.newArrayList(1, 1, 1));
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

    initial.forEach((k, v) -> {
      String text = table.getText(LibColumns.SAMPLE_LOCATION, k);
      assertTrue("initial value expected %s in row %d, but was '%s'".formatted(v, k, text),
          text.endsWith(v));
    });

    Map<Integer, String> sortCols = new HashMap<>();
    sortCols.put(0, Libs.B05);
    sortCols.put(1, Libs.C06);
    sortCols.put(2, Libs.A07);

    page.sortTable(BulkLibraryPage.SORT_SAMPLE_LOCATION_COLS);
    HandsOnTable sort1Table = page.getTable();
    sortCols.forEach((k, v) -> {
      String text = sort1Table.getText(LibColumns.SAMPLE_LOCATION, k);
      assertTrue("sortCols value expected %s in row %d, but was '%s'".formatted(v, k, text),
          text.endsWith(v));
    });

    Map<Integer, String> sortRows = new HashMap<>();
    sortRows.put(0, Libs.A07);
    sortRows.put(1, Libs.B05);
    sortRows.put(2, Libs.C06);

    page.sortTable(BulkLibraryPage.SORT_SAMPLE_LOCATION_ROWS);
    HandsOnTable sort2Table = page.getTable();
    sortRows.forEach((k, v) -> {
      String text = sort2Table.getText(LibColumns.SAMPLE_LOCATION, k);
      assertTrue("sortRows value expected %s in row %d, but was '%s'".formatted(v, k, text),
          text.endsWith(v));
    });
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
    assertTrue(page.save(false));

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
    assertTrue(page.save(false));

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
    table.clearField(LibColumns.BOX_ALIAS, 0);
    assertTrue(page.save(false));

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
    table.enterText(LibColumns.DISCARDED, 0, "False");
    table.enterText(LibColumns.BOX_SEARCH, 0, "BOX100001");
    table.waitForSearch(LibColumns.BOX_ALIAS, 0);
    assertEquals("Bulk Boxables Test", table.getText(LibColumns.BOX_ALIAS, 0));
    table.enterText(LibColumns.BOX_POSITION, 0, "B01");
    assertEquals("B01", table.getText(LibColumns.BOX_POSITION, 0));
    assertTrue(page.save(false));

    DetailedLibrary after = (DetailedLibrary) getSession().get(LibraryImpl.class, libId);
    assertNotNull(after.getBox());
    assertEquals("BOX100001", after.getBox().getName());
    assertEquals("B01", after.getBoxPosition());
    assertFalse(after.isDiscarded());
  }

  @Test
  public void testPropagateSpecifiedReplicates() {
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(100004L, 110004L, 120004L),
            Arrays.asList(1, 2, 3));
    HandsOnTable table = page.getTable();
    assertEquals(6, table.getRowCount());
    assertEquals("LIBT_0001_Ly_P_1-1_D1", table.getText(LibColumns.SAMPLE_ALIAS, 0));
    assertEquals("1LIB_0001_Ly_P_1-1_D1", table.getText(LibColumns.SAMPLE_ALIAS, 1));
    assertEquals("1LIB_0001_Ly_P_1-1_D1", table.getText(LibColumns.SAMPLE_ALIAS, 2));
    assertEquals("1IPO_0001_Ly_P_1-1_D1", table.getText(LibColumns.SAMPLE_ALIAS, 3));
    assertEquals("1IPO_0001_Ly_P_1-1_D1", table.getText(LibColumns.SAMPLE_ALIAS, 4));
    assertEquals("1IPO_0001_Ly_P_1-1_D1", table.getText(LibColumns.SAMPLE_ALIAS, 5));
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
    testLibraryAttribute(LibColumns.INDEX_FAMILY, attributes, library,
        lib -> lib.getIndex1() == null ? null : lib.getIndex1().getFamily().getName());
    testLibraryAttribute(LibColumns.INDEX_1, attributes, library,
        lib -> lib.getIndex1() == null ? null : lib.getIndex1().getLabel());
    testLibraryAttribute(LibColumns.INDEX_2, attributes, library,
        lib -> lib.getIndex2() == null ? null : lib.getIndex2().getLabel());
    testLibraryAttribute(LibColumns.QC_STATUS, attributes, library,
        lib -> lib.getDetailedQcStatus() == null ? "Not Ready" : lib.getDetailedQcStatus().getDescription());
    testLibraryAttribute(LibColumns.QC_NOTE, attributes, library, Library::getDetailedQcStatusNote);
    testLibraryAttribute(LibColumns.SIZE, attributes, library, lib -> {
      return lib.getDnaSize() == null ? null : lib.getDnaSize().toString();
    });
    testLibraryAttribute(LibColumns.VOLUME, attributes, library, lib -> LimsUtils.toNiceString(lib.getVolume()));
    testLibraryAttribute(LibColumns.CONCENTRATION, attributes, library,
        lib -> LimsUtils.toNiceString(lib.getConcentration()));
    if (attributes.containsKey(LibColumns.RECEIVE_DATE)) {
      assertReceiptAttributes(attributes, library);
    }
  }

  protected void assertReceiptAttributes(Map<String, String> attributes, Library library) {
    QueryBuilder<TransferLibrary, TransferLibrary> builder =
        new QueryBuilder<>(getSession(), TransferLibrary.class, TransferLibrary.class);
    Join<TransferLibrary, Transfer> join = builder.getJoin(builder.getRoot(), TransferLibrary_.transfer);
    builder.addPredicate(builder.getCriteriaBuilder().equal(builder.getRoot().get(TransferLibrary_.item), library));
    builder.addPredicate(builder.getCriteriaBuilder().isNotNull(join.get(Transfer_.senderLab)));
    TransferLibrary receipt = builder.getSingleResultOrNull();

    assertNotNull("A receipt transfer should be created", receipt);

    assertEntityAttribute(LibColumns.RECEIVE_DATE, attributes, receipt,
        s -> s == null ? "" : LimsUtils.formatDate(s.getTransfer().getTransferTime()));
    assertEntityAttribute(LibColumns.RECEIVE_TIME, attributes, receipt, BulkLibraryIT::getReceiptTime);
    assertEntityAttribute(LibColumns.RECEIVED_FROM, attributes, receipt,
        s -> s == null ? "" : s.getTransfer().getSenderLab().getAlias());
    assertEntityAttribute(LibColumns.RECEIVED_BY, attributes, receipt,
        s -> s == null ? "" : s.getTransfer().getRecipientGroup().getName());
    assertEntityAttribute(LibColumns.RECEIPT_CONFIRMED, attributes, receipt,
        s -> s == null ? "" : booleanString(s.isReceived(), "Unknown"));
    assertEntityAttribute(LibColumns.RECEIPT_QC_PASSED, attributes, receipt,
        s -> s == null ? "" : booleanString(s.isQcPassed(), "Unknown"));
    assertEntityAttribute(LibColumns.RECEIPT_QC_NOTE, attributes, receipt,
        s -> s == null ? "" : emptyIfNull(s.getQcNote()));
  }

  private static String getReceiptTime(TransferLibrary receipt) {
    if (receipt == null) {
      return "";
    }
    DateFormat formatter = new SimpleDateFormat("h:mm a");
    return formatter.format(receipt.getTransfer().getTransferTime()).toLowerCase();
  }

  private void assertDetailedLibraryAttributes(Map<String, String> attributes, DetailedLibrary library) {
    assertPlainLibraryAttributes(attributes, library);

    testLibraryAttribute(LibColumns.DESIGN, attributes, library, lib -> {
      return lib.getLibraryDesign() == null ? null : lib.getLibraryDesign().getName();
    });
    testLibraryAttribute(LibColumns.KIT_DESCRIPTOR, attributes, library, lib -> lib.getKitDescriptor().getName());
    testLibraryAttribute(LibColumns.CODE, attributes, library, lib -> {
      return String.format("%s (%s)", lib.getLibraryDesignCode().getCode(),
          lib.getLibraryDesignCode().getDescription());
    });
    testLibraryAttribute(LibColumns.SELECTION, attributes, library, lib -> lib.getLibrarySelectionType().getName());
    testLibraryAttribute(LibColumns.STRATEGY, attributes, library, lib -> lib.getLibraryStrategyType().getName());
  }

  private void assertParentSampleAttributes(Map<String, String> attributes, DetailedLibrary library) {
    testLibraryAttribute(SamColumns.SAMPLE_TYPE, attributes, library, lib -> lib.getSample().getSampleType());
    testLibraryAttribute(SamColumns.SCIENTIFIC_NAME, attributes, library,
        lib -> lib.getSample().getScientificName().getAlias());
    testLibraryAttribute(SamColumns.PROJECT, attributes, library, lib -> lib.getSample().getProject().getCode());
    testLibraryAttribute(SamColumns.EXTERNAL_NAME, attributes, library,
        lib -> identityGetter.apply(lib).getExternalName());
    testLibraryAttribute(SamColumns.DONOR_SEX, attributes, library,
        lib -> identityGetter.apply(lib).getDonorSex().getLabel());
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

  private <T> void testLibraryAttribute(String column, Map<String, String> attributes, T object,
      Function<T, String> getter) {
    if (attributes.containsKey(column)) {
      String objectAttribute = getter.apply(object);
      String tableAttribute = cleanNullValues(column, attributes.get(column));
      if (tableAttribute == null) {
        assertTrue(String.format("persisted attribute expected empty '%s'", column),
            isStringEmptyOrNull(objectAttribute));
      } else {
        assertEquals(String.format("persisted attribute '%s'", column), tableAttribute, objectAttribute);
      }
    }
  }

  private String cleanNullValues(String key, String value) {
    if (key.equals(LibColumns.INDEX_FAMILY) && NO_INDEX_FAMILY.equals(value)) {
      return null;
    }
    return value == null || value.isEmpty() ? null : value;
  }

}
