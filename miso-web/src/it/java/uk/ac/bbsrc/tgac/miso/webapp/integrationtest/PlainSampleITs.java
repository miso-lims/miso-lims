
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
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Library;
import uk.ac.bbsrc.tgac.miso.core.data.Sample;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.SampleImpl;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkDilutionPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkDilutionPage.DilColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryPage.LibColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage.SamColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTableSaveResult;

public class PlainSampleITs extends AbstractIT {

  private static final Set<String> sampleColumns = Sets.newHashSet(SamColumns.NAME, SamColumns.ALIAS, SamColumns.DESCRIPTION,
      SamColumns.BOX_SEARCH, SamColumns.BOX_ALIAS, SamColumns.BOX_POSITION, SamColumns.DISCARDED, SamColumns.RECEIVE_DATE,
      SamColumns.SAMPLE_TYPE, SamColumns.SCIENTIFIC_NAME, SamColumns.PROJECT, SamColumns.QC_PASSED);

  private static final Set<String> libraryColumns = Sets.newHashSet(LibColumns.NAME, LibColumns.SAMPLE_ALIAS, LibColumns.SAMPLE_LOCATION,
      LibColumns.BOX_SEARCH, LibColumns.BOX_ALIAS, LibColumns.BOX_POSITION, LibColumns.DISCARDED, LibColumns.CREATION_DATE, LibColumns.PLATFORM,
      LibColumns.LIBRARY_TYPE, LibColumns.SELECTION, LibColumns.STRATEGY, LibColumns.INDEX_FAMILY, LibColumns.INDEX_1, LibColumns.INDEX_2,
      LibColumns.KIT_DESCRIPTOR, LibColumns.QC_PASSED, LibColumns.SIZE, LibColumns.CONCENTRATION, LibColumns.CONCENTRATION_UNITS);

  private static final Set<String> dilutionColumns = Sets.newHashSet(DilColumns.NAME, DilColumns.LIBRARY_ALIAS, DilColumns.BOX_SEARCH,
      DilColumns.BOX_ALIAS, DilColumns.BOX_POSITION, DilColumns.DISCARDED, DilColumns.CONCENTRATION, DilColumns.CONCENTRATION_UNITS,
      DilColumns.VOLUME, DilColumns.VOLUME_UNITS, DilColumns.NG_USED, DilColumns.VOLUME_USED, DilColumns.CREATION_DATE);

  @Before
  public void setup() {
    loginAdmin();
  }

  @Override
  protected boolean isDetailedSampleMode() {
    return false;
  }

  @Test
  public void testCreatePlainSampleSetup() {
    // Goal: ensure all expected fields are present and no extra, and that dropdowns appear as expected
    // (dropdowns do not render properly when table is broken)
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
    BulkSamplePage page = BulkSamplePage.getForCreate(getDriver(), getBaseUrl(), 1, null, null);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(SamColumns.ALIAS, "PRO1_S02_1");
    attrs.put(SamColumns.DESCRIPTION, "description");
    attrs.put(SamColumns.RECEIVE_DATE, "2017-10-10");
    attrs.put(SamColumns.SAMPLE_TYPE, "TRANSCRIPTOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Homo sapiens");
    attrs.put(SamColumns.PROJECT, "PRO1");
    attrs.put(SamColumns.QC_PASSED, "True");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));
    HandsOnTableSaveResult result = table.save();

    assertTrue("Sample save", result.getItemsSaved() == 1);
    assertTrue("Server errors", result.getServerErrors().isEmpty());
    assertTrue("Save errors", result.getSaveErrors().isEmpty());

    Long savedId = Long.valueOf(table.getText(SamColumns.NAME, 0).substring(3));
    Sample saved = (Sample) getSession().get(SampleImpl.class, savedId);
    assertTrue("Sample name generation", saved.getName().contains("SAM"));
    assertTrue("Sample barcode generation", !isStringEmptyOrNull(saved.getIdentificationBarcode()));
  }

  @Test
  public void testCreatePlainLibrarySetup() {
    // Goal: ensure all expected fields are present and no extra and that dropdowns appear as expected
    // (dropdowns do not render properly when table is broken)
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(1L), 1);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(libraryColumns.size(), headings.size());
    libraryColumns.forEach(col -> assertTrue("Check for column: '" + col + "'", headings.contains(col)));
    assertEquals(1, table.getRowCount());

    Set<String> platforms = table.getDropdownOptions(LibColumns.PLATFORM, 0);
    assertFalse("Platform dropdown did not render; confirm one active sequencer exists and that table is not broken", platforms.isEmpty());
    assertTrue(platforms.contains("Illumina"));
  }

  @Test
  public void testPropagateOnePlainLibrary() {
    // Goal: ensure one library can be saved
    BulkLibraryPage page = BulkLibraryPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(1L), 1);
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
    attrs.put(LibColumns.QC_PASSED, "True");
    attrs.put(LibColumns.SIZE, "321");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));
    HandsOnTableSaveResult result = table.save();

    assertTrue("Library save", result.getItemsSaved() == 1);
    assertTrue("Server errors", result.getServerErrors().isEmpty());
    assertTrue("Save errors", result.getSaveErrors().isEmpty());

    Long savedId = Long.valueOf(table.getText(LibColumns.NAME, 0).substring(3));
    Library saved = (Library) getSession().get(LibraryImpl.class, savedId);
    assertTrue("Library name generation", saved.getName().contains("LIB"));
    assertTrue("Library barcode generation", !isStringEmptyOrNull(saved.getIdentificationBarcode()));
  }

  @Test
  public void testReceiveLibrary() {
    BulkLibraryPage page = BulkLibraryPage.getForReceive(getDriver(), getBaseUrl(), 1, null, null);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(LibColumns.SAMPLE_ALIAS, "PRO1_S1000_test");
    attrs.put(SamColumns.SAMPLE_TYPE, "GENOMIC");
    attrs.put(SamColumns.SCIENTIFIC_NAME, "Hank");
    attrs.put(SamColumns.PROJECT, "PRO1");
    attrs.put(LibColumns.RECEIVE_DATE, "2017-11-28");
    attrs.put(LibColumns.PLATFORM, "Illumina");
    attrs.put(LibColumns.LIBRARY_TYPE, "Paired End");
    attrs.put(LibColumns.SELECTION, "PCR");
    attrs.put(LibColumns.STRATEGY, "WGS");
    attrs.put(LibColumns.INDEX_FAMILY, "No indices");
    attrs.put(LibColumns.KIT_DESCRIPTOR, "Test Kit");
    attrs.put(LibColumns.QC_PASSED, "True");
    attrs.put(LibColumns.SIZE, "321");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));
    HandsOnTableSaveResult result = table.save();

    assertTrue("Library save", result.getItemsSaved() == 1);
    assertTrue("Server errors", result.getServerErrors().isEmpty());
    assertTrue("Save errors", result.getSaveErrors().isEmpty());

    Long savedId = Long.valueOf(table.getText(LibColumns.NAME, 0).substring(3));
    Library saved = (Library) getSession().get(LibraryImpl.class, savedId);
    assertTrue("Library name generation", saved.getName().contains("LIB"));
    assertTrue("Library barcode generation", !isStringEmptyOrNull(saved.getIdentificationBarcode()));
    assertTrue("Library alias generation", !isStringEmptyOrNull(saved.getAlias()));
  }

  @Test
  public void testCreatePlainDilutionSetup() {
    // Goal: ensure all expected fields are present and no extra and that data can be entered in date field
    // (date field cannot be entered when table is broken)
    BulkDilutionPage page = BulkDilutionPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(1L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(dilutionColumns.size(), headings.size());
    dilutionColumns.forEach(col -> assertTrue("Check for column: '" + col + "'", headings.contains(col)));
    assertEquals(1, table.getRowCount());

    String creationDate = "2017-10-11";
    table.enterText(DilColumns.CREATION_DATE, 0, creationDate);
    assertEquals(creationDate, table.getText(DilColumns.CREATION_DATE, 0));
  }

  @Test
  public void testCreateOnePlainDilution() {
    // Goal: ensure one dilution can be saved
    BulkDilutionPage page = BulkDilutionPage.getForPropagate(getDriver(), getBaseUrl(), Arrays.asList(1L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(DilColumns.CONCENTRATION, "2.2");
    attrs.put(DilColumns.VOLUME, "22");
    attrs.put(DilColumns.CREATION_DATE, "2017-10-11");

    attrs.forEach((k, v) -> table.enterText(k, 0, v));
    HandsOnTableSaveResult result = table.save();

    assertTrue("Dilution save", result.getItemsSaved() == 1);
    assertTrue("Server errors", result.getServerErrors().isEmpty());
    assertTrue("Save errors", result.getSaveErrors().isEmpty());

    Long savedId = Long.valueOf(table.getText(DilColumns.NAME, 0).substring(3));
    LibraryDilution saved = (LibraryDilution) getSession().get(LibraryDilution.class, savedId);
    assertTrue("Dilution name generation", saved.getName().contains("LDI"));
    assertTrue("Dilution barcode generation", !isStringEmptyOrNull(saved.getIdentificationBarcode()));
  }

  @Test
  public void testNoErrorsOnPages() {
    Set<String> slugs = new HashSet<>();
    slugs.add("admin/users");
    slugs.add("admin/groups");
    slugs.add("mainMenu");
    slugs.add("myAccount");
    slugs.add("projects");
    slugs.add("samples");
    slugs.add("libraries");
    slugs.add("dilutions");
    slugs.add("pools");
    slugs.add("poolorders/active");
    slugs.add("poolorders/all");
    slugs.add("poolorders/pending");
    slugs.add("containers");
    slugs.add("runs");
    slugs.add("boxes");
    slugs.add("instruments");
    slugs.add("kitdescriptors");
    slugs.add("indices");
    slugs.add("studies");
    slugs.add("printers");
    slugs.add("experiments");
    slugs.add("submissions");

    slugs.add("project/1");
    slugs.add("sample/1");
    slugs.add("library/1");
    slugs.add("pool/1");
    slugs.add("sample/receipt");
    slugs.add("importexport");
    final Set<String> urlSlugs = Collections.unmodifiableSet(slugs);

    String errors = urlSlugs.stream()
        .filter(slug -> AbstractPage.checkForErrors(getDriver(), getBaseUrl(), slug))
        .collect(Collectors.joining("\n"));
    if (!LimsUtils.isStringEmptyOrNull(errors)) throw new IllegalArgumentException("Errors on PLAIN sample page(s): " + errors);

  }
}
