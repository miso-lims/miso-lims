package uk.ac.bbsrc.tgac.miso.webapp.integrationtest;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Comparator;
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
      Columns.LIBRARY_ALIAS, Columns.MATRIX_BARCODE, Columns.PLATFORM, Columns.TARGETED_SEQUENCING, Columns.DIL_CONCENTRATION,
      Columns.VOLUME, Columns.CREATOR, Columns.CREATION_DATE);
  private static final Set<String> poolsColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS,
      Columns.DESCRIPTION, Columns.DATE_CREATED, Columns.POOL_CONCENTRATION, Columns.LOCATION,
      Columns.LAST_MODIFIED);
  private static final Set<String> ordersColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.DESCRIPTION,
      Columns.PLATFORM, Columns.LONGEST_INDEX, Columns.SEQUENCING_PARAMETERS, Columns.REMAINING, Columns.LAST_MODIFIED);
  private static final Set<String> containersColumns = Sets.newHashSet(Columns.SORT, Columns.SERIAL_NUMBER, Columns.LAST_RUN_NAME,
      Columns.LAST_RUN_ALIAS, Columns.LAST_SEQUENCER, Columns.LAST_MODIFIED);
  private static final Set<String> runsColumns = Sets.newHashSet(Columns.NAME, Columns.ALIAS, Columns.STATUS,
      Columns.START_DATE, Columns.END_DATE, Columns.LAST_MODIFIED);
  private static final Set<String> boxesColumns = Sets.newHashSet(Columns.SORT, Columns.NAME, Columns.ALIAS, Columns.LOCATION,
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
      Columns.RUNNING, Columns.FAILED, Columns.STARTED, Columns.STOPPED, Columns.UNKNOWN);

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

  private static final Comparator<String> standardComparator = (s1, s2) -> s1.compareTo(s2);

  /**
   * Comparator for QC Passed columns, which render the boolean values as symbols.
   */
  private static final Comparator<String> qcPassedComparator = (qcPassed1, qcPassed2) -> {
    return Integer.compare(getQcPassedValue(qcPassed1), getQcPassedValue(qcPassed2));
  };

  private static int getQcPassedValue(String symbol) {
    switch (symbol) {
    case "?":
      return -1;
    case "✘":
      return 0;
    case "✔":
      return 1;
    default:
      throw new IllegalArgumentException("Invalid QC Passed symbol");
    }
  }

  private static final String NAME_REGEX = "^[A-Z]{3}\\d+$";
  /**
   * Compares names with the same prefix by number (e.g. SAM8 and SAM10 compare as 8 and 10, ignoring the 'SAM').
   * If the names don't match the entity name pattern, they are compared regularly as Strings
   */
  private static final Comparator<String> nameNumericComparator = (name1, name2) -> {
    if (name1.matches(NAME_REGEX) && name2.matches(NAME_REGEX) && name1.substring(0, 3).equals(name2.substring(0, 3))) {
      int id1 = Integer.parseInt(name1.substring(3, name1.length()));
      int id2 = Integer.parseInt(name2.substring(3, name2.length()));
      return Integer.compare(id1, id2);
    } else {
      return standardComparator.compare(name1, name2);
    }
  };

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
    assertTrue(ordersColumns.size() <= headings.size());
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
    assertEquals("number of columns", targetColumns.size(), headings.size());
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
    assertEquals("number of columns", targetColumns.size(), headings.size());
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
    for (String heading : headings) {
      // sort one way
      page.sortByColumn(heading);
      assertTrue("first sort on column '" + heading, LimsUtils.isStringEmptyOrNull(page.getErrors().getText()));
      // if there are at least two rows, ensure that sort was correct
      if (!table.isTableEmpty() && table.countRows() > 1) {
        int sort1 = compareFirstTwoNonMatchingValues(table, heading);
        // sort the other way
        page.sortByColumn(heading);
        assertTrue("second sort on column '" + heading, LimsUtils.isStringEmptyOrNull(page.getErrors().getText()));
        int sort2 = compareFirstTwoNonMatchingValues(table, heading);

        // compare results (if either is 0, value of the other can be anything though)
        if (sort1 != 0) {
          assertTrue(heading + " column second sort order should differ from first", sort2 == 0 || sort1 > 0 != sort2 > 0);
        }
      }
    }
  }

  private int compareFirstTwoNonMatchingValues(DataTable table, String heading) {
    String row1Val = table.getTextAtCell(heading, 0);
    String row2Val = table.getTextAtCell(heading, 1);
    for (int rowNum = 2; row1Val.equals(row2Val) && rowNum < table.countRows(); rowNum++) {
      row1Val = row2Val;
      row2Val = table.getTextAtCell(heading, rowNum);
    }
    Comparator<String> columnComparator = getComparator(heading);
    return columnComparator.compare(row1Val, row2Val);
  }

  private static Comparator<String> getComparator(String column) {
    switch (column) {
    case Columns.QC_PASSED:
      return qcPassedComparator;
    case Columns.LIBRARY_NAME:
    case Columns.NAME:
    case Columns.SAMPLE_NAME:
      return nameNumericComparator;
    default:
      return standardComparator;
    }
  }

}
