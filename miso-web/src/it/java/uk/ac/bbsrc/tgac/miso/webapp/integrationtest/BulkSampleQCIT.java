package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.bbsrc.tgac.miso.core.util.LimsUtils.isStringEmptyOrNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.ConcentrationUnit;
import uk.ac.bbsrc.tgac.miso.core.data.VolumeUnit;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC;
import uk.ac.bbsrc.tgac.miso.core.data.qc.SampleQC_;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.persistence.impl.QueryBuilder;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkQCPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkQCPage.QcColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkSampleQCIT extends AbstractBulkSampleIT {

  private static final Set<String> qcColumns = Sets.newHashSet(QcColumns.SAMPLE_ALIAS, QcColumns.DATE, QcColumns.TYPE,
      QcColumns.INSTRUMENT, QcColumns.KIT, QcColumns.KIT_LOT, QcColumns.RESULT, QcColumns.UNITS, QcColumns.DESCRIPTION);

  private BulkQCPage getEditPage(List<Long> ids) {
    return BulkQCPage.getForEditSample(getDriver(), getBaseUrl(), ids, 0);
  }

  private BulkQCPage getAddPage(List<Long> ids, int copies) {
    return BulkQCPage.getForAddSample(getDriver(), getBaseUrl(), ids, copies, 0);
  }


  @Test
  public void testEditQcSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(qcColumns);

    BulkQCPage page = getEditPage(List.of(2201L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue(headings.contains(col), "Check for column: '" + col + "'");
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testAddQcSetup() throws Exception {
    // Goal: ensure all expected fields are present and no extra
    Set<String> expectedHeadings = Sets.newHashSet();
    expectedHeadings.addAll(qcColumns);

    BulkQCPage page = getAddPage(List.of(2201L), 1);
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue(headings.contains(col), "Check for column: '" + col + "'");
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testAddQc() throws Exception {
    // Goal: ensure a sample QC can be added
    BulkQCPage page = getAddPage(List.of(2201L), 1);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(QcColumns.DATE, "2018-07-10");
    attrs.put(QcColumns.TYPE, "test edit qc");
    attrs.put(QcColumns.RESULT, "32.6");

    fillRow(table, 0, attrs);

    assertFalse(table.isWritable(QcColumns.SAMPLE_ALIAS, 0));
    assertFalse(table.isWritable(QcColumns.UNITS, 0));

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(false));

    SampleQC saved = getLatestQc();
    assertQCAttributes(attrs, saved);
  }

  @Test
  public void testEditQc() throws Exception {
    // Goal: ensure a sample QC can be edited
    BulkQCPage page = getEditPage(List.of(4447L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(QcColumns.DATE, "2018-07-10");
    attrs.put(QcColumns.RESULT, "32.6");

    fillRow(table, 0, attrs);

    assertFalse(table.isWritable(QcColumns.SAMPLE_ALIAS, 0));
    assertFalse(table.isWritable(QcColumns.UNITS, 0));
    assertFalse(table.isWritable(QcColumns.TYPE, 0));

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(false));

    SampleQC saved = getLatestQc();
    assertQCAttributes(attrs, saved);
  }

  @Test
  public void testAutoUpdateVolume() throws Exception {
    // Goal: ensure that volume and volume units are updated by the QC
    BulkQCPage page = getAddPage(List.of(2201L), 1);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(QcColumns.DATE, "2018-07-10");
    attrs.put(QcColumns.TYPE, "update volume qc");
    attrs.put(QcColumns.RESULT, "10.43");

    fillRow(table, 0, attrs);

    assertFalse(table.isWritable(QcColumns.SAMPLE_ALIAS, 0));
    assertFalse(table.isWritable(QcColumns.UNITS, 0));

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(false));

    SampleQC saved = getLatestQc();
    assertQCAttributes(attrs, saved);

    assertEquals(0, saved.getSample().getVolume().compareTo(new BigDecimal("10.43")),
        String.format("Expected volume to be updated to %s, instead got %f", "10.43", saved.getSample().getVolume()));
    assertEquals(VolumeUnit.MICROLITRES, saved.getSample().getVolumeUnits(),
        String.format("Expected volume units to be updated to %s, instead got %s", VolumeUnit.MICROLITRES.getUnits(),
            saved.getSample().getVolumeUnits().getUnits()));
  }

  @Test
  public void testAutoUpdateConcentration() throws Exception {
    // Goal: ensure that volume and volume units are updated by the QC
    BulkQCPage page = getAddPage(List.of(2201L), 1);
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(QcColumns.DATE, "2018-07-10");
    attrs.put(QcColumns.TYPE, "update concentration qc");
    attrs.put(QcColumns.RESULT, "24.78");

    fillRow(table, 0, attrs);

    assertFalse(table.isWritable(QcColumns.SAMPLE_ALIAS, 0));
    assertFalse(table.isWritable(QcColumns.UNITS, 0));

    assertColumnValues(table, 0, attrs, "pre-save");
    assertTrue(page.save(false));

    SampleQC saved = getLatestQc();
    assertQCAttributes(attrs, saved);

    assertEquals(0, saved.getSample().getConcentration().compareTo(new BigDecimal("24.78")),
        String.format("Expected concentration to be updated to %s, instead got %f", "24.78",
            saved.getSample().getConcentration()));
    assertEquals(ConcentrationUnit.NANOMOLAR, saved.getSample().getConcentrationUnits(),
        String.format("Expected concentration units to be updated to %s, instead got %s",
            ConcentrationUnit.NANOMOLAR.getUnits(), saved.getSample().getConcentrationUnits().getUnits()));
  }

  private void fillRow(HandsOnTable table, int rowNum, Map<String, String> attributes) {
    attributes.forEach((key, val) -> table.enterText(key, rowNum, val));
  }

  private void assertColumnValues(HandsOnTable table, int rowNum, Map<String, String> attributes, String hintMessage) {
    String formatString = hintMessage + " row %d column '%s' value";
    attributes.forEach((key, val) -> {
      if (isStringEmptyOrNull(val)) {
        assertTrue(isStringEmptyOrNull(table.getText(key, rowNum)),
            String.format(formatString, rowNum, key) + " expected empty");
      } else {
        assertEquals(val, table.getText(key, rowNum), String.format(formatString, rowNum, key));
      }
    });
  }

  private void assertQCAttributes(Map<String, String> attributes, SampleQC sampleQc) {
    testQCAttribute(QcColumns.DATE, attributes, sampleQc, qc -> qc.getDate().toString());
    testQCAttribute(QcColumns.TYPE, attributes, sampleQc, qc -> qc.getType().getName());
    testQCAttribute(QcColumns.RESULT, attributes, sampleQc, qc -> LimsUtils.toNiceString(qc.getResults()));
    testQCAttribute(QcColumns.UNITS, attributes, sampleQc, qc -> qc.getType().getUnits());
  }

  private <T> void testQCAttribute(String column, Map<String, String> attributes, T object,
      Function<T, String> getter) {
    if (attributes.containsKey(column)) {
      String objectAttribute = getter.apply(object);
      String tableAttribute = cleanNullValues(column, attributes.get(column));
      if (tableAttribute == null) {
        assertTrue(isStringEmptyOrNull(objectAttribute),
            String.format("persisted attribute expected empty '%s'", column));
      } else {
        assertEquals(tableAttribute, objectAttribute, String.format("persisted attribute '%s'", column));
      }
    }
  }

  private String cleanNullValues(String key, String value) {
    return value == null || value.isEmpty() ? null : value;
  }

  private SampleQC getLatestQc() {
    QueryBuilder<SampleQC, SampleQC> builder = new QueryBuilder<>(getSession(), SampleQC.class, SampleQC.class);
    builder.addSort(builder.getRoot().get(SampleQC_.qcId), false);
    List<SampleQC> results = builder.getResultList(1, 0);
    return results == null || results.isEmpty() ? null : results.get(0);
  }
}
