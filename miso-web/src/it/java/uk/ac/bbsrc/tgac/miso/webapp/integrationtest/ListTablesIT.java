package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage.TabbedColumns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class ListTablesIT extends AbstractIT {

  private static final Set<String> samplesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS, Columns.SAMPLE_CLASS,
      Columns.SAMPLE_TYPE, Columns.QC_PASSED, Columns.LOCATION, Columns.LAST_MODIFIED);
  private static final Set<String> librariesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS, Columns.SAMPLE_NAME,
      Columns.SAMPLE_ALIAS, Columns.QC_PASSED, Columns.INDICES, Columns.LOCATION, Columns.LAST_MODIFIED);
  private static final Set<String> dilutionsColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.LIBRARY_NAME,
      Columns.LIBRARY_ALIAS, Columns.CREATOR, Columns.CREATION_DATE, Columns.PLATFORM, Columns.DIL_CONCENTRATION);
  private static final Set<String> poolsColumns = Sets.newHashSet(TabbedColumns.SORT, TabbedColumns.NAME, TabbedColumns.ALIAS,
      TabbedColumns.DESCRIPTION, TabbedColumns.DATE_CREATED, TabbedColumns.POOL_CONCENTRATION, TabbedColumns.LOCATION,
      TabbedColumns.LAST_MODIFIED);
  private static final Set<String> ordersColumns = Sets.newHashSet(TabbedColumns.NAME, TabbedColumns.ALIAS, TabbedColumns.DESCRIPTION,
      TabbedColumns.PLATFORM, TabbedColumns.LONGEST_INDEX, TabbedColumns.SEQUENCING_PARAMETERS);
  private static final Set<String> containersColumns = Sets.newHashSet(TabbedColumns.SERIAL_NUMBER, TabbedColumns.LAST_RUN_NAME,
      TabbedColumns.LAST_RUN_ALIAS, TabbedColumns.LAST_SEQUENCER, TabbedColumns.LAST_MODIFIED);
  private static final Set<String> runsColumns = Sets.newHashSet(TabbedColumns.NAME, TabbedColumns.ALIAS, TabbedColumns.STATUS,
      TabbedColumns.START_DATE, TabbedColumns.END_DATE, TabbedColumns.LAST_MODIFIED);
  private static final Set<String> boxesColumns = Sets.newHashSet(TabbedColumns.NAME, TabbedColumns.ALIAS, TabbedColumns.LOCATION,
      TabbedColumns.ITEMS_CAPACITY, TabbedColumns.SIZE);
  private static final Set<String> sequencersColumns = Sets.newHashSet(TabbedColumns.SEQUENCER_NAME, TabbedColumns.PLATFORM,
      TabbedColumns.MODEL, TabbedColumns.STATUS, TabbedColumns.LAST_SERVICED);
  private static final Set<String> kitsColumns = Sets.newHashSet(TabbedColumns.NAME, TabbedColumns.VERSION, TabbedColumns.MANUFACTURER,
      TabbedColumns.PART_NUMBER, TabbedColumns.STOCK_LEVEL, TabbedColumns.PLATFORM);
  private static final Set<String> indicesColumns = Sets.newHashSet(TabbedColumns.FAMILY, TabbedColumns.NAME, TabbedColumns.SEQUENCE);
  private static final Set<String> studiesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.TYPE);
  private static final Set<String> printersColumns = Sets.newHashSet(Columns.SORT, Columns.PRINTER, Columns.DRIVER, Columns.BACKEND,
      Columns.AVAILABLE);

  @Before
  public void setup() {
    loginAdmin();
  }

  private ListPage getList(String listTarget) {
    return ListPage.getListPage(getDriver(), getBaseUrl(), listTarget);
  }

  private ListTabbedPage getTabbedList(String listTarget) {
    return ListTabbedPage.getTabbedListPage(getDriver(), getBaseUrl(), listTarget);
  }
  @Test
  public void testListSamplesPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testPageSetup("samples", samplesColumns);
  }

  @Test
  public void testListSamplesColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort("samples");
  }

  @Test
  public void testListLibrariesPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testPageSetup("libraries", librariesColumns);
  }

  @Test
  public void testListLibrariesColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort("libraries");
  }

  @Test
  public void testListDilutionsPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testPageSetup("dilutions", dilutionsColumns);
  }

  @Test
  public void testListDilutionsColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort("dilutions");
  }

  @Test
  public void testListPoolsPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testTabbedPageSetup("pools", poolsColumns);
  }

  @Test
  public void testListPoolsColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testTabbedColumnsSort("pools");
  }

  @Test
  public void testListOrdersSetup() throws Exception {
    // this one is special because the number of order completion states is variable
    ListTabbedPage page = getTabbedList("poolorders");
    DataTable table = page.getSelectedTable();
    List<String> headings = table.getColumnHeadings();
    // size = order columns + some number of completion state columns
    assertTrue(ordersColumns.size() < headings.size());
    for (String col : ordersColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
  }

  @Test
  public void testListOrdersColumnSort() throws Exception {
    testTabbedColumnsSort("poolorders");
  }

  @Test
  public void testListContainersSetup() throws Exception {
    testTabbedPageSetup("containers", containersColumns);
  }

  @Test
  public void testListContainersColumnSort() throws Exception {
    testTabbedColumnsSort("containers");
  }

  @Test
  public void testListRunsSetup() throws Exception {
    testTabbedPageSetup("runs", runsColumns);
  }

  @Test
  public void testListRunsColumnSort() throws Exception {
    testTabbedColumnsSort("runs");
  }

  @Test
  public void testListBoxesSetup() throws Exception {
    testTabbedPageSetup("boxes", boxesColumns);
  }

  @Test
  public void testListBoxesColumnSort() throws Exception {
    testTabbedColumnsSort("boxes");
  }

  // TODO: re-add once sequencers are listified
  // @Test
  // public void testListSequencersSetup() throws Exception {
  // testTabbedPageSetup("sequencers", sequencersColumns);
  // }
  //
  // @Test
  // public void testListSequencersColumnSort() throws Exception {
  // testTabbedColumnsSort("sequencers");
  // }

  @Test
  public void testListKitsSetup() throws Exception {
    testTabbedPageSetup("kitdescriptors", kitsColumns);
  }

  @Test
  public void testListKitsColumnSort() throws Exception {
    testTabbedColumnsSort("kitdescriptors");
  }

  @Test
  public void testListIndicesSetup() throws Exception {
    testTabbedPageSetup("indices", indicesColumns);
  }

  @Test
  public void testListIndicesColumnSort() throws Exception {
    testTabbedColumnsSort("indices");
  }

  @Test
  public void testListStudiesSetup() throws Exception {
    testPageSetup("studies", studiesColumns);
  }

  @Test
  public void testListStudiesColumnSort() throws Exception {
    testColumnsSort("studies");
  }

  @Test
  public void testListPrintersSetup() throws Exception {
    testPageSetup("printers", printersColumns);
  }

  @Test
  public void testListPrintersColumnSort() throws Exception {
    testColumnsSort("printers");
  }

  private void testPageSetup(String listTarget, Set<String> targetColumns) {
    ListPage page = getList(listTarget);
    DataTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(targetColumns.size(), headings.size());
    for (String col : targetColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
  }

  private void testTabbedPageSetup(String listTarget, Set<String> targetColumns) {
    ListTabbedPage page = getTabbedList(listTarget);
    DataTable table = page.getSelectedTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals(targetColumns.size(), headings.size());
    for (String col : targetColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
  }

  private void testColumnsSort(String listTarget) {
    ListPage page = getList(listTarget);
    DataTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    headings.forEach(heading -> {
      // sort one way
      page.sortByColumn(heading);
      assertTrue("sort once on column '" + heading + "' without errors", LimsUtils.isStringEmptyOrNull(page.getErrors().getText()));
      // sort the other way
      page.sortByColumn(heading);
      assertTrue("sort twice on column '" + heading + "' without errors", LimsUtils.isStringEmptyOrNull(page.getErrors().getText()));
    });
  }

  private void testTabbedColumnsSort(String listTarget) {
    ListTabbedPage page = getTabbedList(listTarget);
    DataTable table = page.getSelectedTable();
    List<String> headings = table.getColumnHeadings();
    // List<String> tabs = page.getTabs();
    // tabs.forEach(tab -> {
    // page.clickTab(tab);
      headings.forEach(heading -> {
        // sort one way
        page.sortByColumn(heading);
        assertTrue("sort once on column '" + heading + "' without errors", LimsUtils.isStringEmptyOrNull(page.getErrors().getText()));
        // sort the other way
        page.sortByColumn(heading);
        assertTrue("sort twice on column '" + heading + "' without errors", LimsUtils.isStringEmptyOrNull(page.getErrors().getText()));
      });
    // });
  }
}
