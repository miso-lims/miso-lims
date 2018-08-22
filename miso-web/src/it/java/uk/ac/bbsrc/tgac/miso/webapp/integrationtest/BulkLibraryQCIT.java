package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.LibraryQC;

import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkQCPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkQCPage.QcColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTableSaveResult;

public class BulkLibraryQCIT extends AbstractIT {

  private static final Logger log = LoggerFactory.getLogger(BulkLibraryQCIT.class);

  private static final Set<String> qcColumns = Sets.newHashSet(QcColumns.LIBRARY_ALIAS, QcColumns.DATE, QcColumns.TYPE,
      QcColumns.RESULT, QcColumns.UNITS, QcColumns.DESCRIPTION);

  @Before
  public void setup() {
    loginAdmin();
  }

  private BulkQCPage getEditPage(List<Long> ids) {
    return BulkQCPage.getForEditLibrary(getDriver(), getBaseUrl(), ids);
  }

  private BulkQCPage getAddPage(List<Long> ids, int copies) {
    return BulkQCPage.getForAddLibrary(getDriver(), getBaseUrl(), ids, copies);
  }


  @Test
  public void testEditQcSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(qcColumns);

    BulkQCPage page = getEditPage(Arrays.asList(2201L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testAddQcSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(qcColumns);

    BulkQCPage page = getAddPage(Arrays.asList(2201L), 1);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testAddQc() throws Exception {
    // Goal: ensure a library QC can be added
    BulkQCPage page = getAddPage(Arrays.asList(2201L), 1);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(QcColumns.DATE, "2018-07-10");
    attrs.put(QcColumns.TYPE, "test edit qc");
    attrs.put(QcColumns.RESULT, "32.6");

    fillRow(table, 0, attrs);

    assertFalse(table.isWritable(QcColumns.LIBRARY_ALIAS, 0));
    assertFalse(table.isWritable(QcColumns.UNITS, 0));

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Criteria c = getSession().createCriteria(LibraryQC.class);
    c.addOrder(Order.desc("qcId"));
    c.setMaxResults(1);
    LibraryQC saved = (LibraryQC) c.uniqueResult();
    assertQCAttributes(attrs, saved);
  }

  @Test
  public void testEditQc() throws Exception {
    // Goal: ensure a library QC can be edited
    BulkQCPage page = getEditPage(Arrays.asList(2201L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(QcColumns.DATE, "2018-07-10");
    attrs.put(QcColumns.RESULT, "32.6");

    fillRow(table, 0, attrs);

    assertFalse(table.isWritable(QcColumns.LIBRARY_ALIAS, 0));
    assertFalse(table.isWritable(QcColumns.UNITS, 0));
    assertFalse(table.isWritable(QcColumns.TYPE, 0));

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Criteria c = getSession().createCriteria(LibraryQC.class);
    c.addOrder(Order.desc("qcId"));
    c.setMaxResults(1);
    LibraryQC saved = (LibraryQC) c.uniqueResult();
    assertQCAttributes(attrs, saved);
  }

  @Test
  public void testAutoUpdateVolume() throws Exception {
    // Goal: ensure that volume and volume units are updated by the QC
    BulkQCPage page = getAddPage(Arrays.asList(2201L), 1);
    HandsOnTable table = page.getTable();
    
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(QcColumns.DATE, "2018-07-10");
    attrs.put(QcColumns.TYPE, "update volume qc");
    attrs.put(QcColumns.RESULT, "10.43");
    
    fillRow(table, 0, attrs);
    
    assertFalse(table.isWritable(QcColumns.LIBRARY_ALIAS, 0));
    assertFalse(table.isWritable(QcColumns.UNITS, 0));

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Criteria c = getSession().createCriteria(LibraryQC.class);
    c.addOrder(Order.desc("qcId"));
    c.setMaxResults(1);
    LibraryQC saved = (LibraryQC) c.uniqueResult();
    assertQCAttributes(attrs, saved);
    
    assertTrue(String.format("Expected volume to be updated to %f, instead got %f", Double.parseDouble("10.43"), saved.getLibrary().getVolume()),
        saved.getLibrary().getVolume().equals(Double.parseDouble("10.43")));
    assertTrue(
        String.format("Expected volume units to be updated to %s, instead got %s", "&#181;L",
            saved.getLibrary().getVolumeUnits().getUnits()),
        saved.getLibrary().getVolumeUnits().getUnits().equals("&#181;L"));
  }

  @Test
  public void testAutoUpdateConcentration() throws Exception {
    // Goal: ensure that volume and volume units are updated by the QC
    BulkQCPage page = getAddPage(Arrays.asList(2201L), 1);
    HandsOnTable table = page.getTable();
    
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(QcColumns.DATE, "2018-07-10");
    attrs.put(QcColumns.TYPE, "update concentration qc");
    attrs.put(QcColumns.RESULT, "24.78");
    
    fillRow(table, 0, attrs);
    
    assertFalse(table.isWritable(QcColumns.LIBRARY_ALIAS, 0));
    assertFalse(table.isWritable(QcColumns.UNITS, 0));

    assertColumnValues(table, 0, attrs, "pre-save");
    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    Criteria c = getSession().createCriteria(LibraryQC.class);
    c.addOrder(Order.desc("qcId"));
    c.setMaxResults(1);
    LibraryQC saved = (LibraryQC) c.uniqueResult();
    assertQCAttributes(attrs, saved);
    
    assertTrue(
        String.format("Expected concentration to be updated to %f, instead got %f", Double.parseDouble("24.78"),
            saved.getLibrary().getInitialConcentration()),
        saved.getLibrary().getInitialConcentration().equals(Double.parseDouble("24.78")));
    assertTrue(
        String.format("Expected concentration units to be updated to %s, instead got %s", "nM",
            saved.getLibrary().getConcentrationUnits().getUnits()),
        saved.getLibrary().getConcentrationUnits().getUnits().equals("nM"));
  }
  
  private void saveAndAssertSuccess(HandsOnTable table) {
    HandsOnTableSaveResult result = table.save();

    if (result.getItemsSaved() != table.getRowCount()) {
      log.error(result.printSummary());
    }

    assertEquals("Save count", table.getRowCount(), result.getItemsSaved());
    assertTrue("Server error messages", result.getServerErrors().isEmpty());
    assertTrue("Save error messages", result.getSaveErrors().isEmpty());
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

  private void assertQCAttributes(Map<String, String> attributes, LibraryQC libraryQc) {
    testQCAttribute(QcColumns.DATE, attributes, libraryQc, qc -> qc.getDate().toString());
    testQCAttribute(QcColumns.TYPE, attributes, libraryQc, qc -> qc.getType().getName());
    testQCAttribute(QcColumns.RESULT, attributes, libraryQc, qc -> qc.getResults().toString());
    testQCAttribute(QcColumns.UNITS, attributes, libraryQc, qc -> qc.getType().getUnits());
  }

  private <T> void testQCAttribute(String column, Map<String, String> attributes, T object, Function<T, String> getter) {
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
}
