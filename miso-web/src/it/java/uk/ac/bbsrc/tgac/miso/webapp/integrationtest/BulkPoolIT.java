package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;
import static uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.HandsontableUtils.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.data.Pool;
import uk.ac.bbsrc.tgac.miso.core.data.impl.PoolImpl;
import uk.ac.bbsrc.tgac.miso.core.data.impl.view.PoolElement;
import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkPoolCustomPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkPoolPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.BulkPoolPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListLibraryAliquotsPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListPoolsPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.HandsOnTable;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.util.TestUtils;

public class BulkPoolIT extends AbstractIT {

  private static final Set<String> commonColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.BARCODE,
      Columns.BOX_SEARCH, Columns.BOX_ALIAS, Columns.BOX_POSITION, Columns.DISCARDED, Columns.CREATE_DATE,
      Columns.SIZE, Columns.CONCENTRATION, Columns.CONCENTRATION_UNITS, Columns.VOLUME, Columns.VOLUME_UNITS,
      Columns.QC_STATUS, Columns.DESCRIPTION);

  private static final Set<String> libraryAliquotsToPoolColumns = Sets.newHashSet(Columns.LIBRARY_ALIQUOT_NAME, Columns.ALIAS,
      Columns.BOX_ALIAS, Columns.BOX_POSITION, Columns.LIBRARY_SIZE, Columns.POOL);

  @Before
  public void setup() {
    login();
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
  public void testMergeSetup() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForMerge(getDriver(), getBaseUrl(), Lists.newArrayList(200001L, 200002L), Lists.newArrayList(1, 2));
    HandsOnTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(commonColumns.size(), headings.size());
    for (String col : commonColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(1, table.getRowCount());
  }

  @Test
  public void testSelectForPoolTogether() {
    // goal: confirm that using the List Aliquots page to select aliquots for pooling together
    // directs the page to the correct link with the correct aliquots
    ListLibraryAliquotsPage listPage = ListLibraryAliquotsPage.getListPage(getDriver(), getBaseUrl());
    DataTable aliquots = listPage.getTable();
    aliquots.searchFor("LDI70*"); // should get LDI701 and LDI702
    assertEquals(2, aliquots.countRows());

    aliquots.checkBoxForRow(0);
    aliquots.checkBoxForRow(1);
    BulkPoolPage bulkPoolPage = listPage.poolSelectedSeparately();
    assertNotNull(bulkPoolPage);
  }

  @Test
  public void testSelectForPoolSeparately() {
    // goal: confirm that using the List Library Aliquots page to select aliquots for pooling separately
    // directs the page to the correct link with the correct aliquots
    ListLibraryAliquotsPage listPage = ListLibraryAliquotsPage.getListPage(getDriver(), getBaseUrl());
    DataTable aliquots = listPage.getTable();
    aliquots.searchFor("LDI70*"); // should get LDI701 and LDI702
    assertEquals(2, aliquots.countRows());

    aliquots.checkBoxForRow(0);
    aliquots.checkBoxForRow(1);
    BulkPoolPage bulkPoolPage = listPage.poolSelectedSeparately();
    assertNotNull(bulkPoolPage);
  }

  @Test
  public void testPoolTogetherSetup() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForPoolTogether(getDriver(), getBaseUrl(), Sets.newHashSet(200001L, 200002L));
    assertExpectedColumnsAndRows(page.getTable(), commonColumns, 1);
  }

  @Test
  public void testPoolSeparatelySetup() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForPoolSeparately(getDriver(), getBaseUrl(), Sets.newHashSet(200001L, 200002L));
    assertExpectedColumnsAndRows(page.getTable(), commonColumns, 2);
  }

  @Test
  public void testPoolCustomSetup() throws Exception {
    BulkPoolCustomPage page = BulkPoolCustomPage.get(getDriver(), getBaseUrl(), Sets.newHashSet(200001L, 200002L), 2);
    assertExpectedColumnsAndRows(page.getTable(), commonColumns, 2);

    page.switchToLibraryAliquotView();
    assertExpectedColumnsAndRows(page.getTable(), libraryAliquotsToPoolColumns, 2);
  }

  private void assertExpectedColumnsAndRows(HandsOnTable table, Set<String> expectedHeadings, int expectedRowCount) {
    List<String> headings = table.getColumnHeadings();
    assertEquals(expectedHeadings.size(), headings.size());
    for (String col : expectedHeadings) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    assertEquals(expectedRowCount, table.getRowCount());
  }

  @Test
  public void testDropdowns() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForEdit(getDriver(), getBaseUrl(), Sets.newHashSet(200001L));
    HandsOnTable table = page.getTable();

    Set<String> qcValues = table.getDropdownOptions(Columns.QC_STATUS, 0);
    assertEquals(3, qcValues.size());
    assertTrue(qcValues.contains("Ready"));
    assertTrue(qcValues.contains("Failed"));
    assertTrue(qcValues.contains("Not Ready"));
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
    attrs.put(Columns.QC_STATUS, "Failed");
    assertColumnValues(table, 0, attrs, "loaded");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.ALIAS, "IPOT_POOL_1_changed");
    changes.put(Columns.BARCODE, "ipobar200001_changed");
    changes.put(Columns.CREATE_DATE, "2016-07-14");
    changes.put(Columns.CONCENTRATION, "7.0");
    changes.put(Columns.VOLUME, "6.78");
    changes.put(Columns.QC_STATUS, "Ready");
    fillRow(table, 0, changes);
    
    // unchanged
    changes.put(Columns.NAME, attrs.get(Columns.NAME));
    assertColumnValues(table, 0, changes, "changes pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, changes, "post-save");

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
    attrs.put(Columns.QC_STATUS, "Not Ready");
    assertColumnValues(table, 0, attrs, "loaded");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.BARCODE, "ipobar200002");
    changes.put(Columns.VOLUME, "3.33");
    changes.put(Columns.QC_STATUS, "Ready");
    fillRow(table, 0, changes);

    // unchanged
    changes.put(Columns.NAME, attrs.get(Columns.NAME));
    changes.put(Columns.ALIAS, attrs.get(Columns.ALIAS));
    changes.put(Columns.CREATE_DATE, attrs.get(Columns.CREATE_DATE));
    changes.put(Columns.CONCENTRATION, attrs.get(Columns.CONCENTRATION));
    assertColumnValues(table, 0, changes, "changes pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, changes, "post-save");

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
    attrs.put(Columns.QC_STATUS, "Ready");
    assertColumnValues(table, 0, attrs, "loaded");

    // make changes
    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.BARCODE, null);
    changes.put(Columns.VOLUME, null);
    changes.put(Columns.QC_STATUS, "Not Ready");
    fillRow(table, 0, changes);

    // unchanged
    changes.put(Columns.NAME, attrs.get(Columns.NAME));
    changes.put(Columns.ALIAS, attrs.get(Columns.ALIAS));
    changes.put(Columns.CREATE_DATE, attrs.get(Columns.CREATE_DATE));
    changes.put(Columns.CONCENTRATION, attrs.get(Columns.CONCENTRATION));
    assertColumnValues(table, 0, changes, "changes pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, changes, "post-save");

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
    attrs.put(Columns.CONCENTRATION, null);
    attrs.put(Columns.VOLUME, null);
    attrs.put(Columns.QC_STATUS, "Not Ready");
    assertColumnValues(table, 0, attrs, "default values");

    // enter pool data
    attrs.remove(Columns.NAME);
    attrs.put(Columns.ALIAS, "IPOT_POOL_TOGETHER");
    attrs.put(Columns.BARCODE, null);
    attrs.put(Columns.CREATE_DATE, "2017-08-01");
    attrs.put(Columns.CONCENTRATION, "1.23");
    attrs.put(Columns.VOLUME, "4.56");
    attrs.put(Columns.QC_STATUS, "Ready");
    fillRow(table, 0, attrs);
    assertColumnValues(table, 0, attrs, "changes pre-save");

    assertTrue(page.save(true));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "post-save");

    String savedName = assertAndGetSavedName(savedTable, 0);
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
    row0.put(Columns.CONCENTRATION, null);
    row0.put(Columns.VOLUME, null);
    row0.put(Columns.QC_STATUS, "Not Ready");
    row0.put(Columns.CONCENTRATION, "4.0");
    row0.put(Columns.CONCENTRATION_UNITS, "ng/L");
    assertColumnValues(table, 0, row0, "row 0 default values");

    Map<String, String> row1 = Maps.newLinkedHashMap(row0);
    row1.put(Columns.ALIAS, "IPOT_0001_Pa_P_PE_252_WG_POOL");
    row1.put(Columns.CONCENTRATION, "3.0");
    assertColumnValues(table, 1, row1, "row 1 default values");

    // enter pool data
    row0.remove(Columns.NAME);
    row0.put(Columns.ALIAS, "IPOT_POOL_SEPARATE_1");
    row0.put(Columns.BARCODE, "ipotpoolseparate1bar");
    row0.put(Columns.CREATE_DATE, "2017-08-01");
    row0.put(Columns.CONCENTRATION, "1.23");
    row0.remove(Columns.CONCENTRATION_UNITS);
    row0.put(Columns.VOLUME, "4.56");
    row0.put(Columns.QC_STATUS, "Ready");
    fillRow(table, 0, row0);
    row0.put(Columns.CONCENTRATION_UNITS, "ng/L");
    assertColumnValues(table, 0, row0, "row 0 changes pre-save");

    row1.remove(Columns.NAME);
    row1.put(Columns.ALIAS, "IPOT_POOL_SEPARATE_2");
    row1.put(Columns.BARCODE, "ipotpoolseparate2bar");
    row1.put(Columns.CREATE_DATE, "2017-08-01");
    row1.put(Columns.CONCENTRATION, "1.25");
    row1.remove(Columns.CONCENTRATION_UNITS);
    row1.put(Columns.VOLUME, "4.53");
    row1.put(Columns.QC_STATUS, "Ready");
    fillRow(table, 1, row1);
    row1.put(Columns.CONCENTRATION_UNITS, "ng/L");
    assertColumnValues(table, 1, row1, "row 1 changes pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, row0, "row 0 post-save");
    assertColumnValues(savedTable, 1, row1, "row 1 post-save");

    String savedName0 = assertAndGetSavedName(savedTable, 0);
    String savedName1 = assertAndGetSavedName(savedTable, 1);
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
  @Ignore // TODO: fails on Travis only for reasons unknown (Save count expected:<2> but was:<0>)
  public void testPoolCustom() throws Exception {
    BulkPoolCustomPage page = BulkPoolCustomPage.get(getDriver(), getBaseUrl(), Sets.newHashSet(504L, 505L, 701L, 702L), 2);
    HandsOnTable table = page.getTable();

    final String pool1 = "IPOT_POOL_CUSTOM_A";
    final String pool2 = "IPOT_POOL_CUSTOM_B";

    Map<String, String> row0 = Maps.newLinkedHashMap();
    row0.put(Columns.ALIAS, pool1);
    row0.put(Columns.BARCODE, "ipotpoolcustom1bar");
    row0.put(Columns.CREATE_DATE, "2018-05-18");
    row0.put(Columns.CONCENTRATION, "1.23");
    row0.put(Columns.VOLUME, "4.56");
    row0.put(Columns.QC_STATUS, "Ready");
    fillRow(table, 0, row0);
    assertColumnValues(table, 0, row0, "row 0 changes pre-save");

    Map<String, String> row1 = Maps.newLinkedHashMap(row0);
    row1.put(Columns.ALIAS, pool2);
    row1.put(Columns.BARCODE, "ipotpoolcustom2bar");
    fillRow(table, 1, row1);
    assertColumnValues(table, 1, row1, "row 1 changes pre-save");

    page.switchToLibraryAliquotView();
    table = page.getTable();

    table.enterText(Columns.POOL, 0, pool1);
    table.enterText(Columns.POOL, 1, pool1);
    table.enterText(Columns.POOL, 2, pool2);
    table.enterText(Columns.POOL, 3, pool2);

    page.switchToPoolView();
    table = page.getTable();

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, row0, "row 0 post-save");
    assertColumnValues(savedTable, 1, row1, "row 1 post-save");

    String savedName0 = assertAndGetSavedName(savedTable, 0);
    String savedName1 = assertAndGetSavedName(savedTable, 1);
    row0.put(Columns.NAME, savedName0);
    row1.put(Columns.NAME, savedName1);
    Long savedId0 = Long.valueOf(savedName0.substring(3, savedName0.length()));
    Long savedId1 = Long.valueOf(savedName1.substring(3, savedName1.length()));

    Pool saved0 = (Pool) getSession().get(PoolImpl.class, savedId0);
    assertPoolAttributes(row0, saved0);
    assertEquals(2, saved0.getPoolContents().size());
    List<Long> pool0AliquotIds = saved0.getPoolContents().stream()
        .map(pd -> pd.getAliquot().getId())
        .collect(Collectors.toList());
    assertTrue(pool0AliquotIds.contains(Long.valueOf(504L)));
    assertTrue(pool0AliquotIds.contains(Long.valueOf(505L)));

    Pool saved1 = (Pool) getSession().get(PoolImpl.class, savedId1);
    assertPoolAttributes(row1, saved1);
    assertEquals(2, saved1.getPoolContents().size());
    List<Long> pool1AliquotIds = saved1.getPoolContents().stream()
        .map(pd -> pd.getAliquot().getId())
        .collect(Collectors.toList());
    assertTrue(pool1AliquotIds.contains(Long.valueOf(701L)));
    assertTrue(pool1AliquotIds.contains(Long.valueOf(702L)));
  }

  @Test
  public void testAutoCalculateVolumePoolTogether() {
    BulkPoolPage page = BulkPoolPage.getForPoolTogether(getDriver(), getBaseUrl(), Sets.newHashSet(901L, 902L));
    HandsOnTable table = page.getTable();

    // test default volume
    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.VOLUME, "36.0");
    assertColumnValues(table, 0, attrs, "initial volume calculation");

    // test auto-calculation when volume is null
    attrs.put(Columns.ALIAS, "auto_calculate_pool_together");
    attrs.put(Columns.BARCODE, "autocalculatepooltogether");
    attrs.put(Columns.VOLUME, null);

    fillRow(table, 0, attrs);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    String savedName = assertAndGetSavedName(savedTable, 0);
    attrs.put(Columns.NAME, savedName);
    Long savedId = Long.valueOf(savedName.substring(3, savedName.length()));

    Pool saved = (Pool) getSession().get(PoolImpl.class, savedId);
    attrs.put(Columns.VOLUME, "36.0");
    assertPoolAttributes(attrs, saved);
  }

  @Test
  public void testOverwriteVolumePoolTogether() {
    // test overwriting auto-calculated volume
    BulkPoolPage page = BulkPoolPage.getForPoolTogether(getDriver(), getBaseUrl(), Sets.newHashSet(901L, 902L));
    HandsOnTable table = page.getTable();

    Map<String, String> attrs = Maps.newLinkedHashMap();
    attrs.put(Columns.ALIAS, "no_auto_calculate_pool_together");
    attrs.put(Columns.BARCODE, "noautocalculatepooltogether");
    attrs.put(Columns.VOLUME, "10.0");
    fillRow(table, 0, attrs);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    String savedName = assertAndGetSavedName(savedTable, 0);
    attrs.put(Columns.NAME, savedName);
    Long savedId = Long.valueOf(savedName.substring(3, savedName.length()));

    Pool saved = (Pool) getSession().get(PoolImpl.class, savedId);
    assertPoolAttributes(attrs, saved);
  }

  @Test
  public void testAutoCalculateVolumePoolSeparately() {
    BulkPoolPage page = BulkPoolPage.getForPoolSeparately(getDriver(), getBaseUrl(), Sets.newHashSet(901L, 902L));
    HandsOnTable table = page.getTable();

    // check default volumes
    Map<String, String> row0 = Maps.newLinkedHashMap();
    row0.put(Columns.VOLUME, "14.7");
    assertColumnValues(table, 0, row0, "row 0 initial volume calculation");

    Map<String, String> row1 = Maps.newLinkedHashMap();
    row1.put(Columns.VOLUME, "21.3");
    assertColumnValues(table, 1, row1, "row 1 initial volume calculation");

    // test auto-calculation when volumes are null
    row0.put(Columns.ALIAS, "auto_calculate_pool_separate_1");
    row0.put(Columns.BARCODE, "autocalculatepoolseparate1");
    row0.put(Columns.VOLUME, null);

    fillRow(table, 0, row0);

    row1.put(Columns.ALIAS, "auto_calculate_pool_separate_2");
    row1.put(Columns.BARCODE, "autocalculatepoolseparate2");
    row1.put(Columns.VOLUME, null);

    fillRow(table, 1, row1);
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    String savedName0 = assertAndGetSavedName(savedTable, 0);
    String savedName1 = assertAndGetSavedName(savedTable, 1);

    row0.put(Columns.NAME, savedName0);
    row1.put(Columns.NAME, savedName1);

    Long savedId0 = Long.valueOf(savedName0.substring(3, savedName0.length()));
    Long savedId1 = Long.valueOf(savedName1.substring(3, savedName1.length()));

    row0.put(Columns.VOLUME, "14.7");
    row1.put(Columns.VOLUME, "21.3");

    Pool saved0 = (Pool) getSession().get(PoolImpl.class, savedId0);
    assertPoolAttributes(row0, saved0);
    Pool saved1 = (Pool) getSession().get(PoolImpl.class, savedId1);
    assertPoolAttributes(row1, saved1);
  }

  @Test
  public void testOverwriteVolumePoolSeparately() {
    // test overwriting auto-calculated volume
    BulkPoolPage page = BulkPoolPage.getForPoolSeparately(getDriver(), getBaseUrl(), Sets.newHashSet(901L, 902L));
    HandsOnTable table = page.getTable();

    Map<String, String> row0 = Maps.newLinkedHashMap();
    row0.put(Columns.ALIAS, "no_auto_calculate_pool_together_1");
    row0.put(Columns.BARCODE, "noautocalculatepooltogether1");
    row0.put(Columns.VOLUME, "10.0");
    fillRow(table, 0, row0);

    Map<String, String> row1 = Maps.newLinkedHashMap();
    row1.put(Columns.ALIAS, "no_auto_calculate_pool_together_2");
    row1.put(Columns.BARCODE, "noautocalculatepooltogether2");
    row1.put(Columns.VOLUME, "10.0");
    fillRow(table, 1, row1);

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();

    String savedName0 = assertAndGetSavedName(savedTable, 0);
    String savedName1 = assertAndGetSavedName(savedTable, 1);

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
    attrs.put(Columns.QC_STATUS, "Ready");
    assertColumnValues(table, 0, attrs, "loaded for first edit");

    // change 1
    attrs.put(Columns.VOLUME, "7.01");
    table.enterText(Columns.VOLUME, 0, attrs.get(Columns.VOLUME));
    assertColumnValues(table, 0, attrs, "first changes pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "first edit post-save");

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

    assertTrue(page2.save(false));
    HandsOnTable savedTable2 = page2.getTable();
    assertColumnValues(savedTable2, 0, attrs, "second edit post-save");

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
    attrs.put(Columns.CONCENTRATION, null);
    attrs.put(Columns.VOLUME, null);
    attrs.put(Columns.QC_STATUS, "Not Ready");
    assertColumnValues(table, 0, attrs, "default values");

    // enter pool data
    attrs.remove(Columns.NAME);
    attrs.put(Columns.ALIAS, "IPOT_POOL_TOGETHER_2");
    attrs.put(Columns.BARCODE, "ipotpooltogether2bar");
    attrs.put(Columns.CREATE_DATE, "2017-08-01");
    attrs.put(Columns.CONCENTRATION, "1.23");
    attrs.put(Columns.VOLUME, "4.56");
    attrs.put(Columns.QC_STATUS, "Ready");
    fillRow(table, 0, attrs);
    assertColumnValues(table, 0, attrs, "creation pre-save");

    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, attrs, "creation post-save");

    String savedName = assertAndGetSavedName(savedTable, 0);
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

    assertTrue(page2.save(false));
    HandsOnTable savedTable2 = page2.getTable();
    assertColumnValues(savedTable2, 0, attrs, "edits post-save");

    Pool saved2 = (Pool) getSession().get(PoolImpl.class, savedId);
    assertPoolAttributes(attrs, saved2);
  }

  @Test
  public void testSelectForMerge() {
    ListPoolsPage listPools = ListPoolsPage.getListPage(getDriver(), getBaseUrl());
    DataTable pools = listPools.getTable();
    pools.searchFor("IPO20000*"); // should get IPO200001 and IPO200002
    pools.checkBoxForRow(0);
    pools.checkBoxForRow(1);
    
    Map<String, Integer> proportions = new HashMap<>();
    proportions.put("IPO200001", 1);
    proportions.put("IPO200002", 3);
    BulkPoolPage newPage = listPools.mergeSelected(proportions);
    assertNotNull(newPage);
  }

  @Test
  public void testMerge() throws Exception {
    BulkPoolPage page = BulkPoolPage.getForMerge(getDriver(), getBaseUrl(), Lists.newArrayList(200005L, 200006L), Lists.newArrayList(1, 2));
    HandsOnTable table = page.getTable();

    Map<String, String> changes = Maps.newLinkedHashMap();
    changes.put(Columns.ALIAS, "IPOT_POOL_merged");
    changes.put(Columns.BARCODE, "ipobar_merged");
    changes.put(Columns.CREATE_DATE, "2016-07-14");
    changes.put(Columns.CONCENTRATION, "7.0");
    changes.put(Columns.VOLUME, "6.78");
    changes.put(Columns.QC_STATUS, "Ready");
    fillRow(table, 0, changes);

    assertColumnValues(table, 0, changes, "changes pre-save");
    assertTrue(page.save(false));
    HandsOnTable savedTable = page.getTable();
    assertColumnValues(savedTable, 0, changes, "post-save");

    String savedName = assertAndGetSavedName(savedTable, 0);
    changes.put(Columns.NAME, savedName);
    Long savedId = Long.valueOf(savedName.substring(3, savedName.length()));

    Pool saved = (Pool) getSession().get(PoolImpl.class, savedId);
    assertPoolAttributes(changes, saved);

    assertLibraryAliquotViews(saved, Lists.newArrayList(120001L, 200001L, 200002L), Lists.newArrayList(3, 2, 1));
  }

  private static void assertPoolAttributes(Map<String, String> attributes, Pool pool) {
    assertEntityAttribute(Columns.ALIAS, attributes, pool, Pool::getAlias);
    assertEntityAttribute(Columns.BARCODE, attributes, pool, Pool::getIdentificationBarcode);
    assertEntityAttribute(Columns.CREATE_DATE, attributes, pool, p -> LimsUtils.formatDate(p.getCreationDate()));
    assertEntityAttribute(Columns.CONCENTRATION, attributes, pool, p -> LimsUtils.toNiceString(p.getConcentration()));
    assertEntityAttribute(Columns.VOLUME, attributes, pool, p -> p.getVolume() == null ? null : LimsUtils.toNiceString(p.getVolume()));
    assertEntityAttribute(Columns.QC_STATUS, attributes, pool, p -> TestUtils.qcPassedToString(p.getQcPassed()));
  }

  private static void assertLibraryAliquotViews(Pool pool, List<Long> ids, List<Integer> proportions) {
    assertTrue("Incorrect number of pooled elements in pool", pool.getPoolContents().size() == ids.size());
    for (int i = 0; i < ids.size(); i++) {
      final Long id = ids.get(i);
      PoolElement poolElement = pool.getPoolContents().stream()
          .filter(pd -> Long.valueOf(pd.getAliquot().getId()).equals(id))
          .findFirst().orElse(null);
      assertNotNull("Pool does not contain element with id " + id, poolElement);
      assertEquals("Saved PoolElement has incorrect proportion", proportions.get(i), Integer.valueOf(poolElement.getProportion()));
    }
  }

  private static String assertAndGetSavedName(HandsOnTable table, int rowNum) {
    String savedName = table.getText(Columns.NAME, rowNum);
    assertFalse(LimsUtils.isStringEmptyOrNull(savedName));
    assertTrue(savedName.matches("^IPO\\d+$"));
    return savedName;
  }

}
