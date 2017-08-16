package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

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

import uk.ac.bbsrc.tgac.miso.core.data.impl.LibraryDilution;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkDilutionPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkDilutionPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTableSaveResult;

public class BulkDilutionIT extends AbstractIT {

  private static final Logger log = LoggerFactory.getLogger(BulkDilutionIT.class);

  private static final Set<String> columns = Sets.newHashSet(Columns.NAME, Columns.ID_BARCODE, Columns.LIBRARY_ALIAS,
      Columns.CONCENTRATION, Columns.CREATION_DATE, Columns.TARGETED_SEQUENCING);

  private static final String NO_TAR_SEQ = "(None)";

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testEditSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkDilutionPage page = BulkDilutionPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(304L, 305L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(columns.size(), headings.size());
    for (String col : columns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(2, table.getRowCount());
  }

  @Test
  public void testPropagateSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    BulkDilutionPage page = BulkDilutionPage.getForPropagate(getDriver(), getBaseUrl(),
        Sets.newHashSet(304L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(columns.size(), headings.size());
    for (String col : columns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());

    assertEquals("DILT_0001_nn_n_PE_304_WG", table.getText(Columns.LIBRARY_ALIAS, 0));
  }

  @Test
  public void testCreateDropdowns() throws Exception {
    BulkDilutionPage page = BulkDilutionPage.getForPropagate(getDriver(), getBaseUrl(),
        Sets.newHashSet(304L));
    HandsOnTable table = page.getTable();
    
    List<String> targetedSequencings = table.getDropdownOptions(Columns.TARGETED_SEQUENCING, 0);
    assertTrue(targetedSequencings.contains("Test TarSeq One"));
    assertTrue(targetedSequencings.contains("Test TarSeq Two"));
    assertTrue(targetedSequencings.contains("(None)"));
    assertEquals("targeted sequencing size", 3, targetedSequencings.size()); // two plus "(None)"
  }

  @Test
  public void testReadOnlyCells() {
    BulkDilutionPage page = BulkDilutionPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(304L));
    HandsOnTable table = page.getTable();

    assertFalse(table.isWritable(Columns.NAME, 0));
    assertFalse(table.isWritable(Columns.LIBRARY_ALIAS, 0));
  }
  
  @Test
  public void testPropagate() throws Exception {
    BulkDilutionPage page = BulkDilutionPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(305L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.ID_BARCODE, "102938");
    attrs.put(Columns.CONCENTRATION, "3.45");
    attrs.put(Columns.CREATION_DATE, "2017-08-14");
    attrs.put(Columns.TARGETED_SEQUENCING, "Test TarSeq Two");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryDilution saved = (LibraryDilution) getSession().get(LibraryDilution.class, newId);
    assertDilutionAttributes(attrs, saved);
  }

  @Test
  public void testPropagateToEditToPool() {
    // propagate library to dilution to pool separately
    BulkDilutionPage page = BulkDilutionPage.getForPropagate(getDriver(), getBaseUrl(), Sets.newHashSet(306L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.ID_BARCODE, "11223344");
    attrs.put(Columns.CONCENTRATION, "4.56");
    attrs.put(Columns.CREATION_DATE, "2017-08-14");
    attrs.put(Columns.TARGETED_SEQUENCING, "Test TarSeq One");

    fillRow(table, 0, attrs);

    assertColumnValues(table, 0, attrs, "pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Long newId = getSavedId(table, 0);
    LibraryDilution saved = (LibraryDilution) getSession().get(LibraryDilution.class, newId);
    assertDilutionAttributes(attrs, saved);

    // chain edit dilutions
    BulkDilutionPage page2 = page.chainEdit();
    HandsOnTable table2 = page2.getTable();
    assertColumnValues(table2, 0, attrs, "reload for edit");

    table2.enterText(Columns.CONCENTRATION, 0, "5.67");
    attrs.put(Columns.CONCENTRATION, "5.67");
    saveAndAssertSuccess(table2);
    assertColumnValues(table2, 0, attrs, "edit post-save");
    LibraryDilution saved2 = (LibraryDilution) getSession().get(LibraryDilution.class, newId);
    assertDilutionAttributes(attrs, saved2);

    // chain pool dilutions separately
    assertNotNull(page2.chainPoolSeparately());
  }

  private long getSavedId(HandsOnTable table, int rowNum) {
    return Long.valueOf(table.getText(Columns.NAME, 0).substring(3, table.getText(Columns.NAME, 0).length()));
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

  private void saveAndAssertSuccess(HandsOnTable table) {
    HandsOnTableSaveResult result = table.save();

    if (result.getItemsSaved() != table.getRowCount()) {
      log.error(result.printSummary());
    }

    assertEquals("Save count", table.getRowCount(), result.getItemsSaved());
    assertTrue("Server error messages", result.getServerErrors().isEmpty());
    assertTrue("Save error messages", result.getSaveErrors().isEmpty());

    for (int i = 0; i < table.getRowCount(); i++) {
      assertTrue("Dilution name generation", table.getText(Columns.NAME, i).contains("LDI"));
    }
  }

  private void assertDilutionAttributes(Map<String, String> attributes, LibraryDilution dilution) {
    testDilutionAttribute(Columns.NAME, attributes, dilution, LibraryDilution::getName);
    testDilutionAttribute(Columns.ID_BARCODE, attributes, dilution, LibraryDilution::getIdentificationBarcode);
    testDilutionAttribute(Columns.LIBRARY_ALIAS, attributes, dilution, dil -> dil.getLibrary().getAlias());
    testDilutionAttribute(Columns.CONCENTRATION, attributes, dilution, dil-> dil.getConcentration().toString());
    testDilutionAttribute(Columns.CREATION_DATE, attributes, dilution, dil -> dil.getCreationDate().toString());
    testDilutionAttribute(Columns.TARGETED_SEQUENCING, attributes, dilution, dil -> {
      if (dil.getTargetedSequencing() == null) {
        return null;
      }
      return dil.getTargetedSequencing().getAlias();
    });
  }

  private <T> void testDilutionAttribute(String column, Map<String, String> attributes, T object, Function<T, String> getter) {
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
    if (Columns.TARGETED_SEQUENCING.equals(key)) {
      return NO_TAR_SEQ.equals(value) ? null : value;
    } else {
      return value == null || value.isEmpty() ? null : value;
    }
  }
}
