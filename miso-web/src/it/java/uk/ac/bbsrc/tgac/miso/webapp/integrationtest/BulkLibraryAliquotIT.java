package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryAliquot;
import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryImpl;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryAliquotPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkLibraryAliquotPage.LibraryAliquotColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkSamplePage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils;

public class BulkLibraryAliquotIT extends AbstractIT {

  private static final Set<String> commonColumns = Sets.newHashSet(LibraryAliquotColumns.NAME,
      LibraryAliquotColumns.ALIAS, BulkSamplePage.SamColumns.PROJECT, BulkSamplePage.SamColumns.REQUISITION_ASSAY,
      LibraryAliquotColumns.TISSUE_ORIGIN, LibraryAliquotColumns.TISSUE_TYPE, LibraryAliquotColumns.DESCRIPTION,
      LibraryAliquotColumns.ID_BARCODE, LibraryAliquotColumns.BOX_SEARCH, LibraryAliquotColumns.BOX_ALIAS,
      LibraryAliquotColumns.BOX_POSITION, LibraryAliquotColumns.DISCARDED, LibraryAliquotColumns.EFFECTIVE_GROUP_ID,
      LibraryAliquotColumns.GROUP_ID, LibraryAliquotColumns.GROUP_DESCRIPTION, LibraryAliquotColumns.DESIGN_CODE,
      LibraryAliquotColumns.QC_STATUS, LibraryAliquotColumns.QC_NOTE, LibraryAliquotColumns.SIZE,
      LibraryAliquotColumns.CONCENTRATION, LibraryAliquotColumns.CONCENTRATION_UNITS, LibraryAliquotColumns.VOLUME,
      LibraryAliquotColumns.VOLUME_UNITS, LibraryAliquotColumns.NG_USED, LibraryAliquotColumns.VOLUME_USED,
      LibraryAliquotColumns.CREATION_DATE, LibraryAliquotColumns.KIT, LibraryAliquotColumns.KIT_LOT,
      LibraryAliquotColumns.TARGETED_SEQUENCING);

  private static final Set<String> propagateColumns = Sets.newHashSet(LibraryAliquotColumns.PARENT_NAME,
      LibraryAliquotColumns.PARENT_ALIAS, LibraryAliquotColumns.PARENT_LOCATION);

  @Before
  public void setup() {
    login();
  }

  @Test
  public void testEditSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(304L, 305L));
    HandsontableUtils.testTableSetup(page, commonColumns, 2);
  }

  @Test
  public void testPropagateSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(),
        Sets.newHashSet(304L));
    Set<String> expectedColumns = Sets.newHashSet();
    expectedColumns.addAll(commonColumns);
    expectedColumns.addAll(propagateColumns);
    HandsontableUtils.testTableSetup(page, expectedColumns, 1);

    HandsOnTable table = page.getTable();
    assertEquals("DILT_0001_nn_n_PE_304_WG", table.getText(LibraryAliquotColumns.PARENT_ALIAS, 0));
  }

  @Test
  public void testRepropagateSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForRepropagate(getDriver(), getBaseUrl(),
        Sets.newHashSet(304L));
    Set<String> expectedColumns = Sets.newHashSet();
    expectedColumns.addAll(commonColumns);
    expectedColumns.addAll(propagateColumns);
    HandsontableUtils.testTableSetup(page, expectedColumns, 1);

    HandsOnTable table = page.getTable();
    assertEquals("DILT_0001_nn_n_PE_304_WG", table.getText(LibraryAliquotColumns.PARENT_ALIAS, 0));
  }

  @Test
  public void testCreateDropdowns() throws Exception {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(),
        Sets.newHashSet(304L));
    HandsOnTable table = page.getTable();
    
    table.enterText(LibraryAliquotColumns.KIT, 0, "Test Kit");
    Set<String> targetedSequencings = table.getDropdownOptions(LibraryAliquotColumns.TARGETED_SEQUENCING, 0);
    assertTrue(targetedSequencings.contains("Test TarSeq One"));
    assertTrue(targetedSequencings.contains("Test TarSeq Two"));
    assertEquals("targeted sequencing size", 2, targetedSequencings.size());
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
    attrs.put(LibraryAliquotColumns.KIT, "Test Kit");
    attrs.put(LibraryAliquotColumns.KIT_LOT, "20210526");
    attrs.put(LibraryAliquotColumns.TARGETED_SEQUENCING, "Test TarSeq Two");
    attrs.put(LibraryAliquotColumns.QC_STATUS, "Ready");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    Long newId = getSavedId(savedTable, 0);
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
    attrs.put(LibraryAliquotColumns.QC_STATUS, "Not Ready");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    Long newId = getSavedId(savedTable, 0);
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
    attrs.put(LibraryAliquotColumns.KIT, "Test Kit");
    attrs.put(LibraryAliquotColumns.KIT_LOT, "20210526");
    attrs.put(LibraryAliquotColumns.TARGETED_SEQUENCING, "Test TarSeq One");
    attrs.put(LibraryAliquotColumns.QC_STATUS, "Not Ready");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    Long newId = getSavedId(savedTable, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    // chain edit library aliquots
    BulkLibraryAliquotPage page2 = page.chainEdit();
    HandsOnTable table2 = page2.getTable();
    assertColumnValues(table2, 0, attrs, "reload for edit");

    table2.enterText(LibraryAliquotColumns.CONCENTRATION, 0, "5.67");
    attrs.put(LibraryAliquotColumns.CONCENTRATION, "5.67");
    
    table2.enterText(LibraryAliquotColumns.VOLUME, 0, "10.5");
    attrs.put(LibraryAliquotColumns.VOLUME, "10.5");
    assertTrue(page2.save(false));
    HandsOnTable savedTable2 = page2.getTable();
    assertColumnValues(savedTable2, 0, attrs, "edit post-save");
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
    attrs.put(LibraryAliquotColumns.QC_STATUS, "Not Ready");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(true));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    Long newId = getSavedId(savedTable, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue("Expected library volume to be negative, received positive value", savedLib.getVolume().compareTo(BigDecimal.ZERO) < 0);
  }

  @Test
  public void testVolumeUsedNoWarning() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(701L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "99.99");
    attrs.put(LibraryAliquotColumns.QC_STATUS, "Not Ready");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    Long newId = getSavedId(savedTable, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue("Expected library volume to be positive, received negative value", savedLib.getVolume().compareTo(BigDecimal.ZERO) > 0);
  }

  @Test
  public void testRemoveLibraryVolumeForPropogateOne() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(801L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "10.1");
    attrs.put(LibraryAliquotColumns.QC_STATUS, "Not Ready");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    Long newId = getSavedId(savedTable, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 89.9, savedLib.getVolume()),
        compareDoubles(savedLib.getVolume(), "89.9"));
  }

  @Test
  public void testRemoveLibraryVolumeForPropogateMany() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(802L, 803L));
    HandsOnTable table = page.getTable();

    Map<String, String> row0 = Maps.newLinkedHashMap();
    row0.put(LibraryAliquotColumns.VOLUME_USED, "12.34");
    row0.put(LibraryAliquotColumns.QC_STATUS, "Not Ready");

    Map<String, String> row1 = Maps.newLinkedHashMap();
    row1.put(LibraryAliquotColumns.VOLUME_USED, "110.2");
    row1.put(LibraryAliquotColumns.QC_STATUS, "Not Ready");

    fillRow(table, 0, row0);
    fillRow(table, 1, row1);

    assertColumnValues(table, 0, row0, "pre-save row 0");
    assertColumnValues(table, 1, row1, "pre-save row 1");
    assertTrue(page.save(true));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, row0, "post-save row 0");
    assertColumnValues(savedTable, 1, row1, "post-save row 1");

    Long newId0 = getSavedId(savedTable, 0);
    LibraryAliquot saved0 = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId0);
    assertLibraryAliquotAttributes(row0, saved0);

    Long newId1 = getSavedId(savedTable, 1);
    LibraryAliquot saved1 = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId1);
    assertLibraryAliquotAttributes(row1, saved1);

    LibraryImpl savedLib0 = (LibraryImpl) getSession().get(LibraryImpl.class, saved0.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 87.66, savedLib0.getVolume()),
        compareDoubles(savedLib0.getVolume(), "87.66"));

    LibraryImpl savedLib1 = (LibraryImpl) getSession().get(LibraryImpl.class, saved1.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", -10.2, savedLib1.getVolume()),
        compareDoubles(savedLib1.getVolume(), "-10.2"));
  }

  @Test
  public void testRemoveLibraryVolumeForEdit() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(1001L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "50.2");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    Long newId = getSavedId(savedTable, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 49.8, savedLib.getVolume()),
        compareDoubles(savedLib.getVolume(), "49.8"));
  }

  @Test
  public void testRemoveLibraryVolumeForEditFromNullVolUsed() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(1002L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "32.4");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    Long newId = getSavedId(savedTable, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 67.6, savedLib.getVolume()),
        compareDoubles(savedLib.getVolume(), "67.6"));
  }

  @Test
  public void testAddLibraryVolumeForEdit() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(1003L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "12.2");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    Long newId = getSavedId(savedTable, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 87.8, savedLib.getVolume()),
        compareDoubles(savedLib.getVolume(), "87.8"));
  }

  @Test
  public void testAddLibraryVolumeForEditToNullVolUsed() {
    BulkLibraryAliquotPage page = BulkLibraryAliquotPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(1004L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(LibraryAliquotColumns.VOLUME_USED, "");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    Long newId = getSavedId(savedTable, 0);
    LibraryAliquot saved = (LibraryAliquot) getSession().get(LibraryAliquot.class, newId);
    assertLibraryAliquotAttributes(attrs, saved);

    LibraryImpl savedLib = (LibraryImpl) getSession().get(LibraryImpl.class, saved.getLibrary().getId());
    assertTrue(String.format("Expected library volume to be %f, actual library volume was %f", 100.0, savedLib.getVolume()),
        compareDoubles(savedLib.getVolume(), "100.0"));
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
    testLibraryAliquotAttribute(LibraryAliquotColumns.CONCENTRATION, attributes, aliquot,
        dil -> LimsUtils.toNiceString(dil.getConcentration()));
    testLibraryAliquotAttribute(LibraryAliquotColumns.VOLUME, attributes, aliquot, dil -> LimsUtils.toNiceString(dil.getVolume()));
    testLibraryAliquotAttribute(LibraryAliquotColumns.CREATION_DATE, attributes, aliquot, dil -> dil.getCreationDate().toString());
    testLibraryAliquotAttribute(LibraryAliquotColumns.NG_USED, attributes, aliquot, dil -> LimsUtils.toNiceString(dil.getNgUsed()));
    testLibraryAliquotAttribute(LibraryAliquotColumns.VOLUME_USED, attributes, aliquot,
        dil -> dil.getVolumeUsed() == null ? "" : LimsUtils.toNiceString(dil.getVolumeUsed()));
    testLibraryAliquotAttribute(LibraryAliquotColumns.KIT, attributes, aliquot,
        x -> x.getKitDescriptor() == null ? null : x.getKitDescriptor().getName());
    testLibraryAliquotAttribute(LibraryAliquotColumns.KIT_LOT, attributes, aliquot, LibraryAliquot::getKitLot);
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
    return value == null || value.isEmpty() ? null : value;
  }

  private boolean compareDoubles(BigDecimal d1, String d2) {
    return d1.compareTo(new BigDecimal(d2)) == 0;
  }
}
