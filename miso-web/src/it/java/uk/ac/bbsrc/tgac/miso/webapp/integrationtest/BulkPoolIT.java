package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkPoolPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkPoolPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;

public class BulkPoolIT extends AbstractIT {

  private static final Set<String> commonColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.BARCODE, Columns.CREATE_DATE,
      Columns.CONCENTRATION, Columns.VOLUME, Columns.QC_PASSED, Columns.READY_TO_RUN);

  @Before
  public void setup() {
    loginAdmin();
  }

  @Test
  public void testEditSetup() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(200001L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(commonColumns.size(), headings.size());
    for (String col : commonColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testPoolTogetherSetup() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForPoolTogether(getDriver(), getBaseUrl(), Sets.newHashSet(200001L, 200002L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(commonColumns.size(), headings.size());
    for (String col : commonColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testPoolSeparatelySetup() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForPoolSeparately(getDriver(), getBaseUrl(), Sets.newHashSet(200001L, 200002L));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(commonColumns.size(), headings.size());
    for (String col : commonColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(2, table.getRowCount());
  }

  @Test
  public void testDropdowns() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(200001L));
    HandsOnTable table = page.getTable();

    List<String> qcValues = table.getDropdownOptions(Columns.QC_PASSED, 0);
    assertEquals(3, qcValues.size());
    assertTrue(qcValues.contains("True"));
    assertTrue(qcValues.contains("False"));
    assertTrue(qcValues.contains("Unknown"));

    List<String> readyToRunValues = table.getDropdownOptions(Columns.READY_TO_RUN, 0);
    assertEquals(2, readyToRunValues.size());
    assertTrue(readyToRunValues.contains("True"));
    assertTrue(readyToRunValues.contains("False"));
  }

  @Test
  public void testEditChangeValues() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(200001L));
    HandsOnTable table = page.getTable();

    // test initial values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.NAME, "IPO200001");
    attrs.put(Columns.ALIAS, "IPOT_POOL_1");
    attrs.put(Columns.BARCODE, "ipobar200001");
    attrs.put(Columns.CREATE_DATE, "2017-08-15");
    attrs.put(Columns.CONCENTRATION, "6.5");
    attrs.put(Columns.VOLUME, "12.0");
    attrs.put(Columns.QC_PASSED, "False");
    attrs.put(Columns.READY_TO_RUN, "False");
    assertColumnValues(table, 0, attrs, "loaded");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.ALIAS, "IPOT_POOL_1_changed");
    changes.put(Columns.BARCODE, "ipobar200001_changed");
    changes.put(Columns.CREATE_DATE, "2016-07-14");
    changes.put(Columns.CONCENTRATION, "7.0");
    changes.put(Columns.VOLUME, "6.78");
    changes.put(Columns.QC_PASSED, "True");
    changes.put(Columns.READY_TO_RUN, "True");
    fillRow(table, 0, changes);
    
    // unchanged
    changes.put(Columns.NAME, attrs.get(Columns.NAME));
    assertColumnValues(table, 0, changes, "changes pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, changes, "post-save");

    Pool saved = (Pool) getSession().get(PoolImpl.class, 200001L);
    assertPoolAttributes(changes, saved);
  }

  @Test
  public void testEditAddValues() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(200002L));
    HandsOnTable table = page.getTable();

    // test initial values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.NAME, "IPO200002");
    attrs.put(Columns.ALIAS, "IPOT_POOL_2");
    attrs.put(Columns.BARCODE, null);
    attrs.put(Columns.CREATE_DATE, "2017-08-15");
    attrs.put(Columns.CONCENTRATION, "6.5");
    attrs.put(Columns.VOLUME, null);
    attrs.put(Columns.QC_PASSED, "Unknown");
    attrs.put(Columns.READY_TO_RUN, "True");
    assertColumnValues(table, 0, attrs, "loaded");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.BARCODE, "ipobar200002");
    changes.put(Columns.VOLUME, "3.33");
    changes.put(Columns.QC_PASSED, "True");
    fillRow(table, 0, changes);

    // unchanged
    changes.put(Columns.NAME, attrs.get(Columns.NAME));
    changes.put(Columns.ALIAS, attrs.get(Columns.ALIAS));
    changes.put(Columns.CREATE_DATE, attrs.get(Columns.CREATE_DATE));
    changes.put(Columns.CONCENTRATION, attrs.get(Columns.CONCENTRATION));
    changes.put(Columns.READY_TO_RUN, attrs.get(Columns.READY_TO_RUN));
    assertColumnValues(table, 0, changes, "changes pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, changes, "post-save");

    Pool saved = (Pool) getSession().get(PoolImpl.class, 200002L);
    assertPoolAttributes(changes, saved);
  }

  @Test
  public void testEditRemoveValues() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(200003L));
    HandsOnTable table = page.getTable();

    // test initial values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.NAME, "IPO200003");
    attrs.put(Columns.ALIAS, "IPOT_POOL_3");
    attrs.put(Columns.BARCODE, "ipobar200003");
    attrs.put(Columns.CREATE_DATE, "2017-08-15");
    attrs.put(Columns.CONCENTRATION, "6.5");
    attrs.put(Columns.VOLUME, "7.92");
    attrs.put(Columns.QC_PASSED, "True");
    attrs.put(Columns.READY_TO_RUN, "True");
    assertColumnValues(table, 0, attrs, "loaded");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.BARCODE, null);
    changes.put(Columns.VOLUME, null);
    changes.put(Columns.QC_PASSED, "Unknown");
    fillRow(table, 0, changes);

    // unchanged
    changes.put(Columns.NAME, attrs.get(Columns.NAME));
    changes.put(Columns.ALIAS, attrs.get(Columns.ALIAS));
    changes.put(Columns.CREATE_DATE, attrs.get(Columns.CREATE_DATE));
    changes.put(Columns.CONCENTRATION, attrs.get(Columns.CONCENTRATION));
    changes.put(Columns.READY_TO_RUN, attrs.get(Columns.READY_TO_RUN));
    assertColumnValues(table, 0, changes, "changes pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, changes, "post-save");

    Pool saved = (Pool) getSession().get(PoolImpl.class, 200003L);
    assertPoolAttributes(changes, saved);
  }

  @Test
  public void testPoolTogether() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForPoolTogether(getDriver(), getBaseUrl(), Sets.newHashSet(200001L, 200002L));
    HandsOnTable table = page.getTable();

    // check default values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.NAME, null);
    attrs.put(Columns.ALIAS, "IPOT_0001_Pa_P_PE_POOL");
    attrs.put(Columns.BARCODE, null);
    attrs.put(Columns.CREATE_DATE, null);
    attrs.put(Columns.CONCENTRATION, null);
    attrs.put(Columns.VOLUME, null);
    attrs.put(Columns.QC_PASSED, "Unknown");
    attrs.put(Columns.READY_TO_RUN, "True");
    assertColumnValues(table, 0, attrs, "default values");

    // enter pool data
    attrs.remove(Columns.NAME);
    attrs.put(Columns.ALIAS, "IPOT_POOL_TOGETHER");
    attrs.put(Columns.BARCODE, "ipotpooltogetherbar");
    attrs.put(Columns.CREATE_DATE, "2017-08-01");
    attrs.put(Columns.CONCENTRATION, "1.23");
    attrs.put(Columns.VOLUME, "4.56");
    attrs.put(Columns.QC_PASSED, "True");
    attrs.put(Columns.READY_TO_RUN, "True");
    fillRow(table, 0, attrs);
    assertColumnValues(table, 0, attrs, "changes pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "post-save");

    String savedName = assertAndGetSavedName(table, 0);
    attrs.put(Columns.NAME, savedName);
    Long savedId = Long.valueOf(savedName.substring(3, savedName.length()));

    Pool saved = (Pool) getSession().get(PoolImpl.class, savedId);
    assertPoolAttributes(attrs, saved);
  }

  @Test
  public void testPoolSeparately() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForPoolSeparately(getDriver(), getBaseUrl(), Sets.newHashSet(200001L, 200002L));
    HandsOnTable table = page.getTable();

    // check default values
    Map<String, String> row0 = Maps.newLinkedHashMap();
    row0.put(Columns.NAME, null);
    row0.put(Columns.ALIAS, "IPOT_0001_Pa_P_PE_251_WG_POOL");
    row0.put(Columns.BARCODE, null);
    row0.put(Columns.CREATE_DATE, null);
    row0.put(Columns.CONCENTRATION, null);
    row0.put(Columns.VOLUME, null);
    row0.put(Columns.QC_PASSED, "Unknown");
    row0.put(Columns.READY_TO_RUN, "True");
    assertColumnValues(table, 0, row0, "row 0 default values");

    Map<String, String> row1 = Maps.newLinkedHashMap(row0);
    row1.put(Columns.ALIAS, "IPOT_0001_Pa_P_PE_252_WG_POOL");
    assertColumnValues(table, 1, row1, "row 1 default values");

    // enter pool data
    row0.remove(Columns.NAME);
    row0.put(Columns.ALIAS, "IPOT_POOL_SEPARATE_1");
    row0.put(Columns.BARCODE, "ipotpoolseparate1bar");
    row0.put(Columns.CREATE_DATE, "2017-08-01");
    row0.put(Columns.CONCENTRATION, "1.23");
    row0.put(Columns.VOLUME, "4.56");
    row0.put(Columns.QC_PASSED, "True");
    row0.put(Columns.READY_TO_RUN, "True");
    fillRow(table, 0, row0);
    assertColumnValues(table, 0, row0, "row 0 changes pre-save");

    row1.remove(Columns.NAME);
    row1.put(Columns.ALIAS, "IPOT_POOL_SEPARATE_2");
    row1.put(Columns.BARCODE, "ipotpoolseparate2bar");
    row1.put(Columns.CREATE_DATE, "2017-08-01");
    row1.put(Columns.CONCENTRATION, "1.25");
    row1.put(Columns.VOLUME, "4.53");
    row1.put(Columns.QC_PASSED, "True");
    row1.put(Columns.READY_TO_RUN, "True");
    fillRow(table, 1, row1);
    assertColumnValues(table, 1, row1, "row 1 changes pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, row0, "row 0 post-save");
    assertColumnValues(table, 1, row1, "row 1 post-save");

    String savedName0 = assertAndGetSavedName(table, 0);
    String savedName1 = assertAndGetSavedName(table, 1);
    row0.put(Columns.NAME, savedName0);
    row1.put(Columns.NAME, savedName1);
    Long savedId0 = Long.valueOf(savedName0.substring(3, savedName0.length()));
    Long savedId1 = Long.valueOf(savedName1.substring(3, savedName1.length()));

    Pool saved0 = (Pool) getSession().get(PoolImpl.class, savedId0);
    assertPoolAttributes(row0, saved0);
    Pool saved1 = (Pool) getSession().get(PoolImpl.class, savedId1);
    assertPoolAttributes(row1, saved1);
  }

  @Test
  public void testEditTwice() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(200004L));
    HandsOnTable table = page.getTable();

    // test initial values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.NAME, "IPO200004");
    attrs.put(Columns.ALIAS, "IPOT_POOL_4");
    attrs.put(Columns.BARCODE, "ipobar200004");
    attrs.put(Columns.CREATE_DATE, "2017-08-15");
    attrs.put(Columns.CONCENTRATION, "6.5");
    attrs.put(Columns.VOLUME, "7.92");
    attrs.put(Columns.QC_PASSED, "True");
    attrs.put(Columns.READY_TO_RUN, "True");
    assertColumnValues(table, 0, attrs, "loaded for first edit");

    // change 1
    attrs.put(Columns.VOLUME, "7.01");
    table.enterText(Columns.VOLUME, 0, attrs.get(Columns.VOLUME));
    assertColumnValues(table, 0, attrs, "first changes pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "first edit post-save");

    Pool saved = (Pool) getSession().get(PoolImpl.class, 200004L);
    assertPoolAttributes(attrs, saved);

    BulkPoolPage page2 = page.chainEdit();
    assertNotNull(page2);
    HandsOnTable table2 = page2.getTable();

    // change 2
    assertColumnValues(table2, 0, attrs, "reloaded for second edit");
    attrs.put(Columns.VOLUME, "5.99");
    table2.enterText(Columns.VOLUME, 0, attrs.get(Columns.VOLUME));
    assertColumnValues(table2, 0, attrs, "second changes pre-save");

    saveAndAssertSuccess(table2);
    assertColumnValues(table2, 0, attrs, "second edit post-save");

    Pool saved2 = (Pool) getSession().get(PoolImpl.class, 200004L);
    assertPoolAttributes(attrs, saved2);
  }

  @Test
  public void testPropagateToEdit() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForPoolTogether(getDriver(), getBaseUrl(), Sets.newHashSet(200001L, 200002L));
    HandsOnTable table = page.getTable();

    // check default values
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.NAME, null);
    attrs.put(Columns.ALIAS, "IPOT_0001_Pa_P_PE_POOL");
    attrs.put(Columns.BARCODE, null);
    attrs.put(Columns.CREATE_DATE, null);
    attrs.put(Columns.CONCENTRATION, null);
    attrs.put(Columns.VOLUME, null);
    attrs.put(Columns.QC_PASSED, "Unknown");
    attrs.put(Columns.READY_TO_RUN, "True");
    assertColumnValues(table, 0, attrs, "default values");

    // enter pool data
    attrs.remove(Columns.NAME);
    attrs.put(Columns.ALIAS, "IPOT_POOL_TOGETHER_2");
    attrs.put(Columns.BARCODE, "ipotpooltogether2bar");
    attrs.put(Columns.CREATE_DATE, "2017-08-01");
    attrs.put(Columns.CONCENTRATION, "1.23");
    attrs.put(Columns.VOLUME, "4.56");
    attrs.put(Columns.QC_PASSED, "True");
    attrs.put(Columns.READY_TO_RUN, "True");
    fillRow(table, 0, attrs);
    assertColumnValues(table, 0, attrs, "creation pre-save");

    saveAndAssertSuccess(table);
    assertColumnValues(table, 0, attrs, "creation post-save");

    String savedName = assertAndGetSavedName(table, 0);
    attrs.put(Columns.NAME, savedName);
    Long savedId = Long.valueOf(savedName.substring(3, savedName.length()));

    Pool saved = (Pool) getSession().get(PoolImpl.class, savedId);
    assertPoolAttributes(attrs, saved);

    BulkPoolPage page2 = page.chainEdit();
    assertNotNull(page2);
    HandsOnTable table2 = page2.getTable();

    // change 2
    assertColumnValues(table2, 0, attrs, "reloaded for edit");
    attrs.put(Columns.VOLUME, "5.99");
    table2.enterText(Columns.VOLUME, 0, attrs.get(Columns.VOLUME));
    assertColumnValues(table2, 0, attrs, "edits pre-save");

    saveAndAssertSuccess(table2);
    assertColumnValues(table2, 0, attrs, "edits post-save");

    Pool saved2 = (Pool) getSession().get(PoolImpl.class, savedId);
    assertPoolAttributes(attrs, saved2);
  }

  private static void assertPoolAttributes(Map<String, String> attributes, Pool pool) {
    assertEntityAttribute(Columns.ALIAS, attributes, pool, Pool::getAlias);
    assertEntityAttribute(Columns.BARCODE, attributes, pool, Pool::getIdentificationBarcode);
    assertEntityAttribute(Columns.CREATE_DATE, attributes, pool, p -> LimsUtils.getDateAsString(p.getCreationDate()));
    assertEntityAttribute(Columns.CONCENTRATION, attributes, pool, p -> p.getConcentration().toString());
    assertEntityAttribute(Columns.VOLUME, attributes, pool, p -> p.getVolume() == null ? null : p.getVolume().toString());
    assertEntityAttribute(Columns.QC_PASSED, attributes, pool, p -> getQcPassedString(p.getQcPassed()));
    assertEntityAttribute(Columns.READY_TO_RUN, attributes, pool, p -> p.getReadyToRun() ? "True" : "False");
  }

  private static String assertAndGetSavedName(HandsOnTable table, int rowNum) {
    String savedName = table.getText(Columns.NAME, rowNum);
    assertFalse(LimsUtils.isStringEmptyOrNull(savedName));
    assertTrue(savedName.matches("^IPO\\d+$"));
    return savedName;
  }

}
