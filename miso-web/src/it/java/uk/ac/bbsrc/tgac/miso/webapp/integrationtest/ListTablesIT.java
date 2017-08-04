package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import uk.ac.bbsrc.tgac.miso.core.util.LimsUtils;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.Columns;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.AbstractListPage.ListTarget;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.ListTabbedPage.Tabs;
import uk.ac.bbsrc.tgac.miso.webapp.integrationtest.page.element.DataTable;

public class ListTablesIT extends AbstractIT {

  private static final Set<String> samplesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS, Columns.SAMPLE_CLASS,
      Columns.SAMPLE_TYPE, Columns.QC_PASSED, Columns.LOCATION, Columns.LAST_MODIFIED);
  private static final Set<String> librariesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS, Columns.SAMPLE_NAME,
      Columns.SAMPLE_ALIAS, Columns.QC_PASSED, Columns.INDICES, Columns.LOCATION, Columns.LAST_MODIFIED);
  private static final Set<String> dilutionsColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.LIBRARY_NAME,
      Columns.LIBRARY_ALIAS, Columns.CREATOR, Columns.CREATION_DATE, Columns.PLATFORM, Columns.DIL_CONCENTRATION);
  private static final Set<String> poolsColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS,
      Columns.DESCRIPTION, Columns.DATE_CREATED, Columns.POOL_CONCENTRATION, Columns.LOCATION,
      Columns.LAST_MODIFIED);
  private static final Set<String> ordersColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.PLATFORM, Columns.LONGEST_INDEX, Columns.SEQUENCING_PARAMETERS, Columns.LAST_MODIFIED);
  private static final Set<String> containersColumns = Sets.newHashSet(Columns.SERIAL_NUMBER, Columns.LAST_RUN_NAME,
      Columns.LAST_RUN_ALIAS, Columns.LAST_SEQUENCER, Columns.LAST_MODIFIED);
  private static final Set<String> runsColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.STATUS,
      Columns.START_DATE, Columns.END_DATE, Columns.LAST_MODIFIED);
  private static final Set<String> boxesColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.LOCATION,
      Columns.ITEMS_CAPACITY, Columns.SIZE);
  private static final Set<String> sequencersColumns = Sets.newHashSet(Columns.NAME, Columns.PLATFORM, Columns.MODEL, Columns.COMMISSIONED,
      Columns.DECOMMISSIONED, Columns.SERIAL_NUMBER);
  private static final Set<String> kitsColumns = Sets.newHashSet(Columns.NAME, Columns.VERSION, Columns.MANUFACTURER,
      Columns.PART_NUMBER, Columns.STOCK_LEVEL, Columns.PLATFORM);
  private static final Set<String> indicesColumns = Sets.newHashSet(Columns.FAMILY, Columns.NAME, Columns.SEQUENCE);
  private static final Set<String> studiesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.TYPE);
  private static final Set<String> printersColumns = Sets.newHashSet(Columns.SORT, Columns.PRINTER, Columns.DRIVER, Columns.BACKEND,
      Columns.AVAILABLE);
  private static final Set<String> projectsColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.SHORT_NAME, Columns.DESCRIPTION,
      Columns.PROGRESS);

  private static final Set<String> poolsTabs = Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO);
  private static final Set<String> ordersTabs = Sets.newHashSet(Tabs.ACTIVE, Tabs.ALL);
  private static final Set<String> containersTabs = Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO);
  private static final Set<String> runsTabs = Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO);
  private static final Set<String> boxesTabs = Sets.newHashSet(Tabs.DNA, Tabs.LIBRARIES, Tabs.RNA, Tabs.SEQUENCING, Tabs.STORAGE,
      Tabs.TISSUE);
  private static final Set<String> kitsTabs = Sets.newHashSet(Tabs.CLUSTERING, Tabs.EXTRACTION, Tabs.LIBRARY, Tabs.MULTIPLEXING,
      Tabs.SEQUENCING);
  private static final Set<String> indicesTabs = Sets.newHashSet(Tabs.ILLUMINA, Tabs.PACBIO);

  private static final Map<String, Set<String>> tabsForTarget;
  static {
    Map<String, Set<String>> tabs = new HashMap<>();
    tabs.put(ListTarget.POOLS, poolsTabs);
    tabs.put(ListTarget.ORDERS, ordersTabs);
    tabs.put(ListTarget.CONTAINERS, containersTabs);
    tabs.put(ListTarget.RUNS, runsTabs);
    tabs.put(ListTarget.BOXES, boxesTabs);
    tabs.put(ListTarget.KITS, kitsTabs);
    tabs.put(ListTarget.INDICES, indicesTabs);
    tabsForTarget = Collections.unmodifiableMap(tabs);
  }

  private static final Set<String> completionHeaders = Sets.newHashSet(Columns.COMPLETED, Columns.REQUESTED,
      Columns.RUNNING, Columns.REMAINING, Columns.FAILED);

  // some tabs have no data, so we want to ensure we do all sort tests on tabs with data
  private static final Map<String, String> sortOnTab;
  static {
    Map<String, String> preferredTab = new HashMap<>();
    preferredTab.put(ListTarget.POOLS, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.ORDERS, Tabs.ALL);
    preferredTab.put(ListTarget.CONTAINERS, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.RUNS, Tabs.ILLUMINA);
    preferredTab.put(ListTarget.BOXES, Tabs.STORAGE);
    preferredTab.put(ListTarget.KITS, Tabs.LIBRARY);
    preferredTab.put(ListTarget.INDICES, Tabs.ILLUMINA);
    sortOnTab = Collections.unmodifiableMap(preferredTab);
  }

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
    testPageSetup(ListTarget.SAMPLES, samplesColumns);
  }

  @Test
  public void testListSamplesColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort(ListTarget.SAMPLES);
  }

  @Test
  public void testListLibrariesPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testPageSetup(ListTarget.LIBRARIES, librariesColumns);
  }

  @Test
  public void testListLibrariesColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort(ListTarget.LIBRARIES);
  }

  @Test
  public void testListDilutionsPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testPageSetup(ListTarget.DILUTIONS, dilutionsColumns);
  }

  @Test
  public void testListDilutionsColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testColumnsSort(ListTarget.DILUTIONS);
  }

  @Test
  public void testListPoolsPageSetup() throws Exception {
    // Goal: ensure all expected columns are present and no extra
    testTabbedPageSetup(ListTarget.POOLS, poolsColumns);
  }

  @Test
  public void testListPoolsColumnSort() throws Exception {
    // Goal: ensure all sortable columns can be sorted without errors
    testTabbedColumnsSort(ListTarget.POOLS);
  }

  @Test
  public void testListOrdersSetup() throws Exception {
    // this one is special because the number of order completion states is variable
    ListTabbedPage page = getTabbedList(ListTarget.ORDERS);
    DataTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    // size = order columns + some number of completion state columns
    assertTrue(ordersColumns.size() < headings.size());
    for (String col : ordersColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
    headings.removeAll(ordersColumns);
    
    // confirm that order completion columns are part of the expected set and are not duplicated
    Set<String> foundCompletionHeaders = new HashSet<>();
    for (String remaining : headings) {
      if (!completionHeaders.contains(remaining)) throw new IllegalArgumentException("Found unexpected column '" + remaining + "'");
      if (!foundCompletionHeaders.add(remaining))
        throw new IllegalArgumentException("Found duplicate completion column '" + foundCompletionHeaders + "'");
    }
  }

  @Test
  public void testListOrdersColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.ORDERS);
  }

  @Test
  public void testListContainersSetup() throws Exception {
    testTabbedPageSetup(ListTarget.CONTAINERS, containersColumns);
  }

  @Test
  public void testListContainersColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.CONTAINERS);
  }

  @Test
  public void testListRunsSetup() throws Exception {
    testTabbedPageSetup(ListTarget.RUNS, runsColumns);
  }

  @Test
  public void testListRunsColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.RUNS);
  }

  @Test
  public void testListBoxesSetup() throws Exception {
    testTabbedPageSetup(ListTarget.BOXES, boxesColumns);
  }

  @Test
  public void testListBoxesColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.BOXES);
  }

  @Test
   public void testListSequencersSetup() throws Exception {
    testPageSetup(ListTarget.SEQUENCERS, sequencersColumns);
   }

  @Test
  public void testListSequencersColumnSort() throws Exception {
    testColumnsSort(ListTarget.SEQUENCERS);
  }

  @Test
  public void testListKitsSetup() throws Exception {
    testTabbedPageSetup(ListTarget.KITS, kitsColumns);
  }

  @Test
  public void testListKitsColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.KITS);
  }

  @Test
  public void testListIndicesSetup() throws Exception {
    testTabbedPageSetup(ListTarget.INDICES, indicesColumns);
  }

  @Test
  public void testListIndicesColumnSort() throws Exception {
    testTabbedColumnsSort(ListTarget.INDICES);
  }

  @Test
  public void testListStudiesSetup() throws Exception {
    testPageSetup(ListTarget.STUDIES, studiesColumns);
  }

  @Test
  public void testListStudiesColumnSort() throws Exception {
    testColumnsSort(ListTarget.STUDIES);
  }

  @Test
  public void testListPrintersSetup() throws Exception {
    testPageSetup(ListTarget.PRINTERS, printersColumns);
  }

  @Test
  public void testListPrintersColumnSort() throws Exception {
    testColumnsSort(ListTarget.PRINTERS);
  }

  @Test
  public void testListProjectsSetup() throws Exception {
    testPageSetup(ListTarget.PROJECTS, projectsColumns);
  }

  @Test
  public void testListProjectsColumnSort() throws Exception {
    testColumnsSort(ListTarget.PROJECTS);
  }

  private void testPageSetup(String listTarget, Set<String> targetColumns) {
    // Goal: confirm that all expected columns are present
    ListPage page = getList(listTarget);
    DataTable table = page.getTable();
    List<String> headings = table.getColumnHeadings();
    assertEquals("found expected number of columns", targetColumns.size(), headings.size());
    for (String col : targetColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
  }

  private void testTabbedPageSetup(String listTarget, Set<String> targetColumns) {
    // Goal: confirm that all expected tabs and columns are present
    ListTabbedPage page = getTabbedList(listTarget);
    DataTable table = page.getTable();
    // confirm expected number of tabs
    Set<String> tabs = tabsForTarget.get(listTarget);
    Set<String> foundTabs = page.getTabHeadings();
    for (String tab : tabs) {
      assertTrue("Check for tab '" + tab + "': ", foundTabs.contains(tab));
    }

    List<String> headings = table.getColumnHeadings();
    assertEquals("found expected number of columns", targetColumns.size(), headings.size());
    for (String col : targetColumns) {
      assertTrue("Check for column: '" + col + "'", headings.contains(col));
    }
  }

  private void testColumnsSort(String listTarget) {
    // confirm that sortable columns can be sorted on
    ListPage page = getList(listTarget);
    sortColumns(page.getTable(), page);
  }

  private void testTabbedColumnsSort(String listTarget) {
    // confirm that sortable columns can be sorted on
    // note that this sorts in a single tab only, as different tabs should not have different columns.
    ListTabbedPage page = getTabbedList(listTarget);
    DataTable table = page.getTable();
    sortColumns(table, page);

    page.clickTab(sortOnTab.get(listTarget));
    Set<String> tabHeadings = page.getTabHeadings();
    tabHeadings.forEach(tabHeading -> {
      page.clickTab(tabHeading);
      assertTrue("clicked tab without errors", LimsUtils.isStringEmptyOrNull(page.getErrors().getText()));
    });
  }

  private void sortColumns(DataTable table, AbstractListPage page) {
    List<String> headings = table.getSortableColumnHeadings();
    headings.forEach(heading -> {
      // sort one way
      page.sortByColumn(heading);
      assertTrue("sort once on column '" + heading + "' without errors", LimsUtils.isStringEmptyOrNull(page.getErrors().getText()));
      // if there are at least two rows, ensure that sort was correct
      if (!table.isTableEmpty()) {
        int numRows = table.countRows();
        String ascRow1Val, ascRow2Val, descRow1Val, descRow2Val;
        if (numRows > 1) {
          int num = 0;
          ascRow1Val = table.getTextAtCell(heading, num);
          num += 1;
          ascRow2Val = table.getTextAtCell(heading, num);
          while (ascRow1Val.equals(ascRow2Val) && numRows - num > 1) {
            ascRow1Val = table.getTextAtCell(heading, num);
            num += 1;
            ascRow2Val = table.getTextAtCell(heading, num);
          }
          // sort the other way
          page.sortByColumn(heading);
          assertTrue("sort twice on column '" + heading + "' without errors", LimsUtils.isStringEmptyOrNull(page.getErrors().getText()));

          num = 0;
          descRow1Val = table.getTextAtCell(heading, num);
          num += 1;
          descRow2Val = table.getTextAtCell(heading, num);
          while (descRow1Val.equals(descRow2Val) && numRows - num > 1) {
            descRow1Val = table.getTextAtCell(heading, num);
            num += 1;
            descRow2Val = table.getTextAtCell(heading, num);
          }
          // compare results if they are not equal
          if (!ascRow1Val.equals(ascRow2Val) || !descRow2Val.equals(descRow2Val)) {
            assertNotEquals(
                heading + " sort broken. asc1: " + ascRow1Val + ". asc2: " + ascRow2Val + ". desc1: " + descRow1Val + ". desc2: "
                    + descRow2Val,
                ascRow1Val.compareTo(ascRow2Val) >= 0,
                descRow1Val.compareTo(descRow2Val) >= 0);
          }
        }
      }
    });
  }
}
