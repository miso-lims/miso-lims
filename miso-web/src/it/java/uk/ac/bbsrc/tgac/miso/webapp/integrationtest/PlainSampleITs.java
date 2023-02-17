package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryAliquotPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryAliquotPage.LibraryAliquotColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryPage.LibColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.TestUtils;

public class PlainSampleITs extends AbstractIT {

  private static final Set<String> sampleColumns = Sets.newHashSet(SamColumns.NAME, SamColumns.ALIAS,
      SamColumns.DESCRIPTION, SamColumns.REQUISITION_ASSAY, SamColumns.BOX_SEARCH, SamColumns.BOX_ALIAS,
      SamColumns.BOX_POSITION, SamColumns.DISCARDED, SamColumns.RECEIVE_DATE, SamColumns.RECEIVE_TIME,
      SamColumns.RECEIVED_FROM, SamColumns.RECEIVED_BY, SamColumns.RECEIPT_CONFIRMED, SamColumns.RECEIPT_QC_PASSED,
      SamColumns.RECEIPT_QC_NOTE, SamColumns.REQUISITION_ALIAS, SamColumns.REQUISITION, SamColumns.REQUISITION_ASSAY,
      SamColumns.SAMPLE_TYPE, SamColumns.SCIENTIFIC_NAME, SamColumns.PROJECT, SamColumns.VOLUME,
      SamColumns.VOLUME_UNITS, SamColumns.CONCENTRATION, SamColumns.CONCENTRATION_UNITS, SamColumns.QC_STATUS,
      SamColumns.QC_NOTE);

  private static final Set<String> libraryColumns = Sets.newHashSet(LibColumns.NAME, SamColumns.PROJECT,
      SamColumns.REQUISITION_ASSAY, LibColumns.SAMPLE_NAME, LibColumns.SAMPLE_ALIAS, LibColumns.SAMPLE_LOCATION,
      LibColumns.BOX_SEARCH, LibColumns.BOX_ALIAS, LibColumns.BOX_POSITION, LibColumns.DISCARDED,
      LibColumns.CREATION_DATE, LibColumns.WORKSTATION, LibColumns.THERMAL_CYCLER, LibColumns.PLATFORM,
      LibColumns.LIBRARY_TYPE, LibColumns.SELECTION, LibColumns.STRATEGY, LibColumns.INDEX_FAMILY, LibColumns.INDEX_1,
      LibColumns.INDEX_2, LibColumns.UMIS, LibColumns.KIT_DESCRIPTOR, LibColumns.KIT_LOT, LibColumns.QC_STATUS,
      LibColumns.QC_NOTE, LibColumns.SIZE, LibColumns.CONCENTRATION, LibColumns.CONCENTRATION_UNITS,
      LibColumns.SPIKE_IN, LibColumns.SPIKE_IN_DILUTION, LibColumns.SPIKE_IN_VOL);

  private static final Set<String> libraryAliquotColumns = Sets.newHashSet(LibraryAliquotColumns.NAME,
      LibraryAliquotColumns.ALIAS, SamColumns.PROJECT, SamColumns.REQUISITION_ASSAY, LibraryAliquotColumns.PARENT_NAME,
      LibraryAliquotColumns.PARENT_ALIAS, LibraryAliquotColumns.PARENT_LOCATION, LibraryAliquotColumns.DESCRIPTION,
      LibraryAliquotColumns.BOX_SEARCH, LibraryAliquotColumns.BOX_ALIAS, LibraryAliquotColumns.BOX_POSITION,
      LibraryAliquotColumns.DISCARDED, LibraryAliquotColumns.QC_STATUS, LibraryAliquotColumns.QC_NOTE,
      LibraryAliquotColumns.SIZE, LibraryAliquotColumns.CONCENTRATION, LibraryAliquotColumns.CONCENTRATION_UNITS,
      LibraryAliquotColumns.VOLUME, LibraryAliquotColumns.VOLUME_UNITS, LibraryAliquotColumns.NG_USED,
      LibraryAliquotColumns.VOLUME_USED, LibraryAliquotColumns.CREATION_DATE, LibraryAliquotColumns.KIT,
      LibraryAliquotColumns.KIT_LOT, LibraryAliquotColumns.TARGETED_SEQUENCING);

  @Override
  protected boolean isDetailedSampleMode() {
    return false;
  }

  @Test
  public void testCreatePlainSampleSetup() {
    // Goal: ensure all expected fields are present and no extra, and that dropdowns appear as expected
    // (dropdowns do not render properly when table is broken)
    login();
    BulkSamplePage page = BulkSamplePage.getForCreate(getDriver(), getBaseUrl(), 2, 1L, null);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(sampleColumns.size(), headings.size());
    sampleColumns.forEach(col -> assertTrue("Check for column: '" + col + "'", headings.contains(col)));
    assertEquals(2, table.getRowCount());

    Set<String> sampleTypes = table.getDropdownOptions(SamColumns.SAMPLE_TYPE, 0);
    assertFalse("Sample Types dropdown did not render; is table broken?", sampleTypes.isEmpty());
    assertTrue(sampleTypes.contains("GENOMIC"));
    assertTrue(sampleTypes.contains("METATRANSCRIPTOMIC"));
  }

  @Test
  public void testCreateOnePlainSampleNoProject() throws Exception {
    // Goal: ensure one sample can be saved
    login();
    BulkSamplePage page = BulkSamplePage.getForCreate(getDriver(), getBaseUrl(), 1, null, null);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.ALIAS, "PRO1_S02_1");
    attrs.put(SamColumns.DESCRIPTION, "description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-10-10");
    attrs.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    attrs.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    attrs.put(SamColumns.RECEIPT_CONFIRMED, "True");
    attrs.put(SamColumns.RECEIPT_QC_PASSED, "True");
    attrs.put(SamColumns.RECEIPT_QC_NOTE, "");
    attrs.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.PROJECT, "PRO1");
    attrs.put(SamColumns.QC_STATUS, "Ready");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    Long savedId = Long.valueOf(savedTable.getText(SamColumns.NAME, 0).substring(3));
    Sample saved = (Sample) getSession().get(SampleImpl.class, savedId);
    assertTrue("Sample name generation", saved.getName().contains("SAM"));
    assertTrue("Sample barcode generation", !isStringEmptyOrNull(saved.getIdentificationBarcode()));
  }

  @Test
  public void testCreatePlainLibrarySetup() {
    // Goal: ensure all expected fields are present and no extra and that dropdowns appear as expected
    // (dropdowns do not render properly when table is broken)
    login();
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(1L), Arrays.asList(1));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(libraryColumns.size(), headings.size());
    libraryColumns.forEach(col -> assertTrue("Check for column: '" + col + "'", headings.contains(col)));
    assertEquals(1, table.getRowCount());

    Set<String> platforms = table.getDropdownOptions(LibColumns.PLATFORM, 0);
    assertFalse("Platform dropdown did not render; confirm one active sequencer exists and that table is not broken",
        platforms.isEmpty());
    assertTrue(platforms.contains("Illumina"));
    Set<String> spikeIns = table.getDropdownOptions(LibColumns.SPIKE_IN, 0);
    assertFalse(spikeIns.isEmpty());
    assertTrue(spikeIns.contains("Spike-In One"));
    assertFalse(table.isWritable(LibColumns.SPIKE_IN_DILUTION, 0));
    assertFalse(table.isWritable(LibColumns.SPIKE_IN_VOL, 0));
    table.enterText(LibColumns.SPIKE_IN, 0, "Spike-In One");
    assertTrue(table.isWritable(LibColumns.SPIKE_IN_DILUTION, 0));
    assertTrue(table.isWritable(LibColumns.SPIKE_IN_VOL, 0));
    Set<String> dilutionFactors = table.getDropdownOptions(LibColumns.SPIKE_IN_DILUTION, 0);
    assertFalse(dilutionFactors.isEmpty());
    assertTrue(dilutionFactors.contains("1:100"));
  }

  @Test
  public void testPropagateOnePlainLibrary() {
    // Goal: ensure one library can be saved
    login();
    BulkLibraryPage page =
        BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(1L), Arrays.asList(1));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");
    attrs.put(LibColumns.INDEX_FAMILY, "Dual Index 6bp");
    attrs.put(LibColumns.INDEX_1, "A01 (AAACCC)");
    attrs.put(LibColumns.INDEX_2, "B01 (AAATTT)");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.KIT_LOT, "20200730");
    attrs.put(LibColumns.QC_STATUS, "Ready");
    attrs.put(LibColumns.SIZE, "321");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    Long savedId = Long.valueOf(savedTable.getText(LibColumns.NAME, 0).substring(3));
    Library saved = (Library) getSession().get(LibraryImpl.class, savedId);
    assertTrue("Library name generation", saved.getName().contains("LIB"));
    assertTrue("Library barcode generation", !isStringEmptyOrNull(saved.getIdentificationBarcode()));
  }

  @Test
  public void testReceiveLibrary() {
    login();
    BulkLibraryPage page = BulkLibraryPage.getForReceive(getDriver(), getBaseUrl(), 1, null, null);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(LibColumns.SAMPLE_ALIAS, "PRO1_S1000_test");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.PROJECT, "PRO1");
    attrs.put(LibColumns.RECEIVE_DATE, "2017-11-28");
    attrs.put(SamColumns.RECEIVED_FROM, "University Health Network - BioBank");
    attrs.put(SamColumns.RECEIVED_BY, "TestGroupOne");
    attrs.put(SamColumns.RECEIPT_CONFIRMED, "True");
    attrs.put(SamColumns.RECEIPT_QC_PASSED, "True");
    attrs.put(SamColumns.RECEIPT_QC_NOTE, "");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");
    attrs.put(LibColumns.INDEX_FAMILY, "No indices");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_STATUS, "Ready");
    attrs.put(LibColumns.SIZE, "321");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    Long savedId = Long.valueOf(savedTable.getText(LibColumns.NAME, 0).substring(3));
    Library saved = (Library) getSession().get(LibraryImpl.class, savedId);
    assertTrue("Library name generation", saved.getName().contains("LIB"));
    assertTrue("Library barcode generation", !isStringEmptyOrNull(saved.getIdentificationBarcode()));
    assertTrue("Library alias generation", !isStringEmptyOrNull(saved.getAlias()));
  }

  @Test
  public void testCreatePlainLibraryAliquotSetup() {
    // Goal: ensure all expected fields are present and no extra and that data can be entered in date
    // field
    // (date field cannot be entered when table is broken)
    login();
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(1L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(libraryAliquotColumns.size(), headings.size());
    libraryAliquotColumns.forEach(col -> assertTrue("Check for column: '" + col + "'", headings.contains(col)));
    assertEquals(1, table.getRowCount());

    String creationDate = "2017-10-11";
    table.enterText(LibraryAliquotColumns.CREATION_DATE, 0, creationDate);
    assertEquals(creationDate, table.getText(LibraryAliquotColumns.CREATION_DATE, 0));
  }

  @Test
  public void testCreateOnePlainLibraryAliquot() {
    // Goal: ensure one library aliquot can be saved
    login();
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(1L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(LibraryAliquotColumns.CONCENTRATION, "2.2");
    attrs.put(LibraryAliquotColumns.VOLUME, "22");
    attrs.put(LibraryAliquotColumns.CREATION_DATE, "2017-10-11");
    attrs.put(LibraryAliquotColumns.QC_STATUS, "Not Ready");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    Long savedId = Long.valueOf(savedTable.getText(LibraryAliquotColumns.NAME, 0).substring(3));
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, savedId);
    assertTrue("Library aliquot name generation", saved.getName().contains("LDI"));
    assertTrue("Library aliquot barcode generation", !isStringEmptyOrNull(saved.getIdentificationBarcode()));
  }

  @Test
  public void testNoErrorsOnPages() {
    Set<String> slugs = new HashSet<>();
    slugs.add("mainMenu");
    slugs.add("myAccount");
    slugs.add("projects");
    slugs.add("samples");
    slugs.add("libraries");
    slugs.add("libraryaliquots");
    slugs.add("pools");
    slugs.add("sequencingorders/outstanding");
    slugs.add("sequencingorders/all");
    slugs.add("sequencingorders/in-progress");
    slugs.add("containers");
    slugs.add("runs");
    slugs.add("boxes");
    slugs.add("instruments");
    slugs.add("kitdescriptors");
    slugs.add("indexfamily/list");
    slugs.add("studies");
    slugs.add("printers");
    slugs.add("experiments");
    slugs.add("submissions");

    slugs.add("project/1");
    slugs.add("sample/1");
    slugs.add("library/1");
    slugs.add("pool/1");
    final Set<String> urlSlugs = Collections.unmodifiableSet(slugs);

    login();
    long errors = urlSlugs.stream()
        .filter(slug -> TestUtils.checkForErrors(getDriver(), getBaseUrl(), slug))
        .count();
    assertEquals(0L, errors);
  }

  @Test
  public void testNoErrorsOnAdminPages() {
    Set<String> slugs = new HashSet<>();
    slugs.add("admin/users");
    slugs.add("admin/groups");
    final Set<String> urlSlugs = Collections.unmodifiableSet(slugs);

    loginAdmin();
    long errors = urlSlugs.stream()
        .filter(slug -> TestUtils.checkForErrors(getDriver(), getBaseUrl(), slug))
        .count();
    assertEquals(0L, errors);
  }

}
