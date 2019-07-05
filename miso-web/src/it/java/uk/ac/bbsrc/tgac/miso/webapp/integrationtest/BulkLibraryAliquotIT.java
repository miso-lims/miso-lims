package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils.saveAndAssertSuccess;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryAliquotPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryAliquotPage.LibraryAliquotColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkPoolPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkLibraryAliquotIT extends AbstractIT {

  private static final Set<String> columns = Sets.newHashSet(LibraryAliquotColumns.NAME, LibraryAliquotColumns.ALIAS,
      LibraryAliquotColumns.PARENT_ALIAS, LibraryAliquotColumns.ID_BARCODE, LibraryAliquotColumns.BOX_SEARCH,
      LibraryAliquotColumns.BOX_ALIAS, LibraryAliquotColumns.BOX_POSITION, LibraryAliquotColumns.DISCARDED,
      LibraryAliquotColumns.EFFECTIVE_GROUP_ID, LibraryAliquotColumns.GROUP_ID, LibraryAliquotColumns.GROUP_DESCRIPTION,
      LibraryAliquotColumns.DESIGN_CODE, LibraryAliquotColumns.SIZE, LibraryAliquotColumns.CONCENTRATION,
      LibraryAliquotColumns.CONCENTRATION_UNITS, LibraryAliquotColumns.VOLUME, LibraryAliquotColumns.VOLUME_UNITS,
      LibraryAliquotColumns.NG_USED, LibraryAliquotColumns.VOLUME_USED, LibraryAliquotColumns.CREATION_DATE,
      LibraryAliquotColumns.TARGETED_SEQUENCING);

  private static final String NO_TAR_SEQ = "(None)";

  private static final Set<String> editColumns = Sets.newHashSet(Columns.DISTRIBUTED, Columns.DISTRIBUTION_DATE,
      Columns.DISTRIBUTION_RECIPIENT);

  private static final double EPSILON = 0.000001;

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testEditSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(304L, 305L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    Set<String> expectedHeadings = Stream.of(columns, editColumns).flatMap(Set::stream).collect(Collectors.toSet());
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(2, table.getRowCount());
  }

  @Test
  public void testPropagateSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(),
        Sets.newHashSet(304L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(columns.size(), headings.size());
    for (String col : columns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());

    assertEquals("DILT_0001_nn_n_PE_304_WG", table.getText(LibraryAliquotColumns.PARENT_ALIAS, 0));
  }

  @Test
  public void testRepropagateSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForRepropagate(getDriver(), getBaseUrl(),
        Sets.newHashSet(304L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(columns.size(), headings.size());
    for (String col : columns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());

    assertEquals("DILT_0001_nn_n_PE_304_WG", table.getText(LibraryAliquotColumns.PARENT_ALIAS, 0));
  }

  @Test
  public void testCreateDropdowns() throws Exception {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(),
        Sets.newHashSet(304L));
    HandsOnTable table = page.getTable();
    
    Set<String> targetedSequencings = table.getDropdownOptions(LibraryAliquotColumns.TARGETED_SEQUENCING, 0);
    assertTrue(targetedSequencings.contains("Test TarSeq One"));
    assertTrue(targetedSequencings.contains("Test TarSeq Two"));
    assertTrue(targetedSequencings.contains("(None)"));
    assertEquals("targeted sequencing size", 3, targetedSequencings.size()); // two plus "(None)"
  }

  @Test
  public void testReadOnlyCells() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(304L));
    HandsOnTable table = page.getTable();

    assertFalse(table.isWritable(LibraryAliquotColumns.NAME, 0));
    assertFalse(table.isWritable(LibraryAliquotColumns.PARENT_ALIAS, 0));
  }
  
  @Test
  public void testPropagate() throws Exception {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(305L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.ID_BARCODE, "102938");
    attrs.put(LibraryAliquotColumns.CONCENTRATION, "3.45");
    attrs.put(LibraryAliquotColumns.CREATION_DATE, "2017-08-14");
    attrs.put(LibraryAliquotColumns.TARGETED_SEQUENCING, "Test TarSeq Two");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);
  }

  @Test
  public void testRepropagate() throws Exception {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForRepropagate(getDriver(), getBaseUrl(), Sets.newHashSet(305L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.ID_BARCODE, "987654");
    attrs.put(LibraryAliquotColumns.CONCENTRATION, "5.55");
    attrs.put(LibraryAliquotColumns.CREATION_DATE, "2019-07-08");
    attrs.put(LibraryAliquotColumns.DESIGN_CODE, "EX");
    attrs.put(LibraryAliquotColumns.ALIAS, "DILT_0001_nn_n_PE_304_EX");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);
  }

  @Test
  public void testPropagateToEditToPool() {
    // propagate library to library aliquot to pool separately
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(306L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.ID_BARCODE, "11223344");
    attrs.put(LibraryAliquotColumns.CONCENTRATION, "4.56");
    attrs.put(LibraryAliquotColumns.VOLUME, "9.77");
    attrs.put(LibraryAliquotColumns.CREATION_DATE, "2017-08-14");
    attrs.put(LibraryAliquotColumns.TARGETED_SEQUENCING, "Test TarSeq One");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    // chain edit library aliquotss
    BulkLibraryAliquotPage page2 = page.chainEdit();
    HandsOnTable table2 = page2.getTable();
    assertColumnValues(table2, 0, attrs, "reload for edit");

    table2.enterText(LibraryAliquotColumns.CONCENTRATION, 0, "5.67");
    attrs.put(LibraryAliquotColumns.CONCENTRATION, "5.67");
    
    table2.enterText(LibraryAliquotColumns.VOLUME, 0, "10.5");
    attrs.put(LibraryAliquotColumns.VOLUME, "10.5");
    saveAndAssertSuccess(table2);
    assertColumnValues(table2, 0, attrs, "edit post-save");
    LibraryAliquot saved2 = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved2);

    // chain pool aliquots separately
    assertNotNull(page2.chainPoolSeparately());
  }

  @Test
  public void testVolumeUsedWarning() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(700L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "100.01");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table, false);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue("Expected library volume to be negative, received positive value", savedLib.getVolume() < 0);
  }

  @Test
  public void testVolumeUsedNoWarning() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(701L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "99.99");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue("Expected library volume to be negative, received positive value", savedLib.getVolume() > 0);
  }

  @Test
  public void testRemoveLibraryVolumeForPropogateOne() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(801L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "10.1");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 89.9, savedLib.getVolume()),
        compareDoubles(savedLib.getVolume(), 89.9));
  }

  @Test
  public void testRemoveLibraryVolumeForPropogateMany() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(802L, 803L));
    HandsOnTable table = page.getTable();

    Map<String, String> row0 = Maps.newLinkedHashMap();
    row0.put(LibraryAliquotColumns.VOLUME_USED, "12.34");

    Map<String, String> row1 = Maps.newLinkedHashMap();
    row1.put(LibraryAliquotColumns.VOLUME_USED, "110.2");

    fillRow(table, 0, row0);
    fillRow(table, 1, row1);

    assertColumnValues(table, 0, row0, "pre-save row 0");
    assertColumnValues(table, 1, row1, "pre-save row 1");
    saveAndAssertSuccess(table, false);
    assertColumnValues(table, 0, row0, "post-save row 0");
    assertColumnValues(table, 1, row1, "post-save row 1");

    Long newId0 = getSavedId(table, 0);
    LibraryAliquot saved0 = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId0);
    assertLibraryAliquotAttributes(row0, saved0);

    Long newId1 = getSavedId(table, 1);
    LibraryAliquot saved1 = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId1);
    assertLibraryAliquotAttributes(row1, saved1);

    LibraryImpl savedLib0 = (LibraryImpl) getSession().get(LibraryImpl.class, saved0.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 87.66, savedLib0.getVolume()),
        savedLib0.getVolume() == 87.66);

    LibraryImpl savedLib1 = (LibraryImpl) getSession().get(LibraryImpl.class, saved1.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", -10.2, savedLib1.getVolume()),
        compareDoubles(savedLib1.getVolume(), -10.2));
  }

  @Test
  public void testRemoveLibraryVolumeForEdit() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(1001L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "50.2");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 49.8, savedLib.getVolume()),
        compareDoubles(savedLib.getVolume(), 49.8));
  }

  @Test
  public void testRemoveLibraryVolumeForEditFromNullVolUsed() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(1002L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "32.4");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 67.6, savedLib.getVolume()),
        compareDoubles(savedLib.getVolume(), 67.6));
  }

  @Test
  public void testAddLibraryVolumeForEdit() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(1003L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "12.2");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 87.8, savedLib.getVolume()),
        compareDoubles(savedLib.getVolume(), 87.8));
  }

  @Test
  public void testAddLibraryVolumeForEditToNullVolUsed() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(1004L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 100.0, savedLib.getVolume()),
        compareDoubles(savedLib.getVolume(), 100.0));
  }

  private long getSavedId(HandsOnTable table, int rowNum) {
    return Long.valueOf(table.getText(LibraryAliquotColumns.NAME, rowNum).substring(3, table.getText(LibraryAliquotColumns.NAME, 0).length()));
  }

  private void fillRow(HandsOnTable table, int rowNum, Map<String, String> attributes) {
    attributes.forEach((key, val) -> table.enterText(key, rowNum, val));
  }

  private void assertColumnValues(HandsOnTable table, int rowNum, Map<String, String> attributes, String hintMessage) {
    String formatString = hintMessage + " row %d column '%s' value";
    attributes.forEach((key, val) -> {
      if (isStringEmptyOrNull(val)) {
        assertTrue(String.format(formatString, rowNum, key) + " expected empty", isStringEmptyOrNull(table.getText(key, rowNum)));
      } else {
        assertEquals(String.format(formatString, rowNum, key), val, table.getText(key, rowNum));
      }
    });
  }

  private void assertLibraryAliquotAttributes(Map<String, String> attributes, LibraryAliquot aliquot) {
    testLibraryAliquotAttribute(LibraryAliquotColumns.NAME, attributes, aliquot, LibraryAliquot::getName);
    testLibraryAliquotAttribute(LibraryAliquotColumns.ID_BARCODE, attributes, aliquot, LibraryAliquot::getIdentificationBarcode);
    testLibraryAliquotAttribute(LibraryAliquotColumns.PARENT_ALIAS, attributes, aliquot,
        dil -> dil.getLibrary().getAlias());
    testLibraryAliquotAttribute(LibraryAliquotColumns.CONCENTRATION, attributes, aliquot, dil -> dil.getConcentration().toString());
    testLibraryAliquotAttribute(LibraryAliquotColumns.VOLUME, attributes, aliquot, dil -> dil.getVolume().toString());
    testLibraryAliquotAttribute(LibraryAliquotColumns.CREATION_DATE, attributes, aliquot, dil -> dil.getCreationDate().toString());
    testLibraryAliquotAttribute(LibraryAliquotColumns.NG_USED, attributes, aliquot, dil -> dil.getNgUsed().toString());
    testLibraryAliquotAttribute(LibraryAliquotColumns.VOLUME_USED, attributes, aliquot,
        dil -> dil.getVolumeUsed() == null ? "" : dil.getVolumeUsed().toString());
    testLibraryAliquotAttribute(LibraryAliquotColumns.TARGETED_SEQUENCING, attributes, aliquot, dil -> {
      if (dil.getTargetedSequencing() == null) {
        return null;
      }
      return dil.getTargetedSequencing().getAlias();
    });
  }

  private <T> void testLibraryAliquotAttribute(String column, Map<String, String> attributes, T object, Function<T, String> getter) {
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
    if (LibraryAliquotColumns.TARGETED_SEQUENCING.equals(key)) {
      return NO_TAR_SEQ.equals(value) ? null : value;
    } else {
      return value == null || value.isEmpty() ? null : value;
    }
  }

  private boolean compareDoubles(double d1, double d2) {
    return (Math.abs(d1 - d2) <= EPSILON);
  }
}
